package com.fengdui.wheel.media;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**

 * @desc SerializeUtil.java<br>
 *       TLV编解码工具类<br>
 *       TLV:Tag Length Value<br>
 *       编码的字节顺序为大端顺序<br>
 *       当前支持的类型：<br>
 *       1.基本类型及其包装类<br>
 *       2.java.util.Date<br>
 *       3.String<br>
 *       4.以上类型的数组
 */
public class TLVUtil {

	private static final ConcurrentHashMap<String, Future<Map<Short, TLVConfig>>> tlvConfigs = new ConcurrentHashMap<String, Future<Map<Short, TLVConfig>>>();
	private static final String CHARSET = "UTF-8";

	/** 将一个对象编码为(大端)字节数组 */
	@SuppressWarnings("rawtypes")
	public static byte[] encode(Object obj) {
		if (obj == null) {
			return null;
		}
		final Class cls = obj.getClass();
		// 编码基本类型
		if (cls == boolean.class || cls == Boolean.class) {
			byte b = (byte) (((Boolean) obj) ? 1 : 0);
			return new byte[] { b };
		}
		if (cls == int.class || cls == Integer.class) {
			return i2b((Integer) obj);
		}
		if (cls == long.class || cls == Long.class) {
			return l2b((Long) obj);
		}
		if (cls == double.class || cls == Double.class) {
			return d2b((Double) obj);
		}
		if (cls == float.class || cls == Float.class) {
			return f2b((Float) obj);
		}
		if (cls == byte.class || cls == Byte.class) {
			return new byte[] { (Byte) obj };
		}
		if (cls == String.class) {
			try {
				return obj.toString().getBytes(CHARSET);
			} catch (UnsupportedEncodingException impossiable) {
				throw new RuntimeException(impossiable);
			}
		}
		if (cls == Date.class) {// 日期类型,转化成long进行编码
			return l2b(((Date) obj).getTime());
		}
		if (cls == short.class || cls == Short.class) {
			return s2b((Short) obj);
		}
		// 编码定长类型的数组,只需要编码为一个v
		if (cls == byte[].class) {
			return (byte[]) obj;
		}
		if (cls == int[].class || cls == Integer[].class || cls == Byte[].class || cls == boolean[].class || cls == Boolean[].class || cls == long[].class || cls == Long[].class
				|| cls == Date[].class || cls == short[].class || cls == Short[].class) {
			return encodePrimitiveArray(obj);
		}
		// 编码其他类型的数组,需要编码为lv
		if (cls.isArray()) {
			int arrLen = Array.getLength(obj);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			for (int i = 0; i < arrLen; i++) {
				Object elem = Array.get(obj, i);
				byte[] elemBytes = encode(elem);
				if (elemBytes != null && elemBytes.length > 0) {
					byte[] lBytes = i2b(elemBytes.length);
					try {
						bos.write(lBytes);
						bos.write(elemBytes);
					} catch (IOException impossiable) {
						throw new RuntimeException(impossiable);
					}

				}
			}
			return bos.toByteArray();
		}
		if (List.class.isAssignableFrom(cls)) {
			List list = (List) obj;
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			for (int i = 0; i < list.size(); i++) {
				Object elem = list.get(i);
				byte[] elemBytes = encode(elem);
				if (elemBytes != null && elemBytes.length > 0) {
					byte[] lBytes = i2b(elemBytes.length);
					try {
						bos.write(lBytes);
						bos.write(elemBytes);
					} catch (IOException impossiable) {
						throw new RuntimeException(impossiable);
					}

				}
			}
			return bos.toByteArray();
		}
		// 其余类型，需要编码为tlv
		Map<Short, TLVConfig> tlvConfig = getTLVConfig(cls);
		if (tlvConfig == null || tlvConfig.isEmpty()) {
			throw new RuntimeException("unknown class " + cls.getCanonicalName() + ",please add annotation " + TLVField.class.getCanonicalName() + " on the fields of this class");
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (TLVConfig cfg : tlvConfig.values()) {
			short tag = cfg.tag;
			Field field = cfg.field;
			try {
				Object value = field.get(obj);
				byte[] vBytes = encode(value);
				if (vBytes != null && vBytes.length > 0) {
					byte[] tagBytes = s2b(tag);
					byte[] lBytes = i2b(vBytes.length);
					bos.write(tagBytes);
					bos.write(lBytes);
					bos.write(vBytes);
				}
			} catch (Exception e) {
				throw new RuntimeException("fail to encode field " + field.getName() + " of " + obj.getClass(), e);
			}
		}
		return bos.toByteArray();
	}

	@SuppressWarnings("rawtypes")
	private static byte[] encodePrimitiveArray(Object val) {
		Class elementType = val.getClass().getComponentType();
		int step = 1;
		if (elementType == int.class || elementType == Integer.class) {
			step = 4;

		} else if (elementType == long.class || elementType == Long.class || elementType == Date.class) {
			step = 8;
		} else if (elementType == short.class) {
			step = 2;
		}
		int arrayLen = Array.getLength(val);
		byte[] bytes = new byte[arrayLen * step];
		for (int i = 0; i < arrayLen; i++) {
			byte[] elemBytes = null;
			Object elem = Array.get(val, i);
			switch (step) {
			case 1:// Byte or Boolean
				if (elementType == Byte.class) {
					elemBytes = new byte[] { (Byte) elem };
				} else if (elementType == boolean.class || elementType == Boolean.class) {
					byte b = (byte) (((Boolean) elem) ? 1 : 0);
					elemBytes = new byte[] { b };
				}
				break;
			case 2:// short
				elemBytes = s2b((Short) elem);
				break;
			case 4:
				elemBytes = i2b((Integer) elem);
				break;
			case 8:
				if (elem instanceof Date) {
					elemBytes = l2b(((Date) elem).getTime());
				} else {
					elemBytes = l2b((Long) elem);
				}
				break;
			default:
				break;
			}
			System.arraycopy(elemBytes, 0, bytes, i * step, step);
		}
		return bytes;
	}

	@SuppressWarnings("rawtypes")
	private static Map<Short, TLVConfig> getTLVConfig(final Class cls) {
		Future<Map<Short, TLVConfig>> configFuture = tlvConfigs.get(cls.getCanonicalName());
		if (configFuture == null) {
			configFuture = new FutureTask<Map<Short, TLVConfig>>(new Callable<Map<Short, TLVConfig>>() {
				@Override
				public Map<Short, TLVConfig> call() throws Exception {
					return resolveTLVConfigs(cls);
				}
			});
			Future<Map<Short, TLVConfig>> oldFuture = tlvConfigs.putIfAbsent(cls.getCanonicalName(), configFuture);
			if (oldFuture != null) {
				configFuture = oldFuture;
			}
			((FutureTask) configFuture).run();
		}
		try {
			return configFuture.get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T decode(byte[] bytes, Class<T> cls) {
		return decode(bytes, cls, null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> T decode(byte[] bytes, Class<T> cls, Class<?> clsComponent) {
		if (bytes == null || bytes.length == 0 || cls == null) {
			return null;
		}
		Object pVal;
		// 处理基本类型
		if (cls == Integer.class || cls == int.class) {
			pVal = b2i(bytes);
			return (T) pVal;
		}
		if (cls == boolean.class || cls == Boolean.class) {
			pVal = bytes[0] > 0 ? true : false;
			return (T) pVal;
		}
		if (cls == Long.class || cls == long.class) {
			pVal = b2l(bytes);
			return (T) pVal;
		}
		if (cls == String.class) {
			try {
				pVal = new String(bytes, CHARSET);
			} catch (UnsupportedEncodingException impossiable) {
				throw new RuntimeException("unsupported encoding " + CHARSET, impossiable);
			}
			return (T) pVal;
		}
		if (cls == double.class || cls == Double.class) {
			pVal = b2d(bytes);
			return (T) pVal;
		}
		if (cls == float.class || cls == Float.class) {
			pVal = b2f(bytes);
			return (T) pVal;
		}
		if (cls == Date.class) {
			pVal = new Date(b2l(bytes));
			return (T) pVal;
		}
		if (cls == byte.class || cls == Byte.class) {
			pVal = bytes[0];
			return (T) pVal;
		}
		if (cls == short.class || cls == Short.class) {
			pVal = b2s(bytes);
			return (T) pVal;
		}
		// 基本类型的数组
		if (cls == byte[].class) {
			pVal = bytes;
			return (T) pVal;
		}
		if (cls == int[].class || cls == Integer[].class || cls == Byte[].class || cls == boolean[].class || cls == Boolean[].class || cls == long[].class || cls == Long[].class
				|| cls == Date[].class || cls == short[].class || cls == Short[].class) {
			pVal = decodePrimitiveArray(bytes, cls);
			return (T) pVal;
		}
		// 其他类型的数组
		if (cls.isArray()) {
			List elements = new ArrayList();
			Class elementType = cls.getComponentType();
			int j = 0;
			while (j < bytes.length) {
				// get length
				byte[] eleLBytes = new byte[4];
				System.arraycopy(bytes, j, eleLBytes, 0, 4);
				j += 4;
				int eleLen = b2i(eleLBytes);
				byte[] eleVBytes = new byte[eleLen];
				System.arraycopy(bytes, j, eleVBytes, 0, eleLen);
				j += eleLen;
				Object element = decode(eleVBytes, elementType);
				elements.add(element);
			}
			pVal = Array.newInstance(elementType, elements.size());
			int arrIdx = 0;
			for (Object element : elements) {
				Array.set(pVal, arrIdx, element);
				arrIdx++;
			}
			return (T) pVal;
		}
		if (List.class.isAssignableFrom(cls)) {
			List elements = new ArrayList();
			int j = 0;
			while (j < bytes.length) {
				// get length
				byte[] eleLBytes = new byte[4];
				System.arraycopy(bytes, j, eleLBytes, 0, 4);
				j += 4;
				int eleLen = b2i(eleLBytes);
				byte[] eleVBytes = new byte[eleLen];
				System.arraycopy(bytes, j, eleVBytes, 0, eleLen);
				j += eleLen;
				Object element = decode(eleVBytes, clsComponent);
				elements.add(element);
			}
			return (T) elements;
		}
		// 解码一个对象
		T val = null;
		try {
			val = cls.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("fail to instantitate " + cls.getCanonicalName(), e);
		}
		Map<Short, TLVConfig> configs = getTLVConfig(cls);
		if (configs == null || configs.isEmpty()) {
			return val;
		}
		int i = 0;
		int totaFields = configs.size();
		while (i < bytes.length && totaFields > 0) {
			// get tag
			byte[] tBytes = new byte[2];
			System.arraycopy(bytes, i, tBytes, 0, 2);
			i += 2;
			short tag = b2s(tBytes);
			// get length
			byte[] lBytes = new byte[4];
			System.arraycopy(bytes, i, lBytes, 0, 4);
			i += 4;
			int length = b2i(lBytes);
			// get value
			TLVConfig config = configs.get(tag);
			if (config != null) {// unknown tag will just be ignored,for compatable reason
				byte[] vBytes = new byte[length];
				System.arraycopy(bytes, i, vBytes, 0, length);
				Field field = config.field;
				Class type = field.getType();
				Object fieldVal = null;
				fieldVal = decode(vBytes, type);
				try {
					field.set(val, fieldVal);
				} catch (Exception e) {
					throw new RuntimeException("fail to set " + fieldVal + " to " + field.getName() + " of " + cls.getCanonicalName(), e);
				}
				totaFields--;
			}
			i += length;
		}
		return val;
	}

	// 对基本类型的数据类型进行解码
	@SuppressWarnings("rawtypes")
	private static Object decodePrimitiveArray(byte[] bytes, Class cls) {
		int step = 1;
		if (cls == Integer[].class || cls == int[].class) {
			step = 4;
		} else if (cls == Long[].class || cls == long[].class || cls == Date[].class) {
			step = 8;
		} else if (cls == short[].class || cls == Short[].class) {
			step = 2;
		}
		int len = bytes.length / step;
		Object arr = Array.newInstance(cls.getComponentType(), len);
		int arrIndex = 0;
		for (int i = 0; i < bytes.length; i += step) {
			byte[] elemBytes = new byte[step];
			System.arraycopy(bytes, i, elemBytes, 0, step);
			Object elemVal = null;
			switch (step) {
			case 1:// Byte or Boolean
				if (cls == Byte[].class) {
					elemVal = elemBytes[0];
				} else if (cls == Boolean[].class || cls == boolean[].class) {
					elemVal = elemBytes[0] > 0 ? true : false;
				}
				break;
			case 2:// short
				elemVal = b2s(elemBytes);
				break;
			case 4:// int
				elemVal = b2i(elemBytes);
				break;
			case 8:// long or Date
				if (cls == Date[].class) {
					elemVal = new Date(b2l(elemBytes));
				} else {
					elemVal = b2l(elemBytes);
				}
				break;
			}
			Array.set(arr, arrIndex, elemVal);
			arrIndex++;
		}
		return arr;
	}

	private static class TLVConfig {
		public short tag;
		public Field field;

		private TLVConfig(Field field, short tag) {
			this.tag = tag;
			this.field = field;
		}
	}

	@SuppressWarnings("rawtypes")
	private static Map<Short, TLVConfig> resolveTLVConfigs(Class cls) {
		List<Class> classes = new ArrayList<Class>();
		classes.add(cls);
		while ((cls = cls.getSuperclass()) != Object.class) {
			classes.add(cls);
		}
		// 子类的配置将覆盖父类的配置
		int l = classes.size();
		Map<Short, TLVConfig> clsTLVConfigs = new HashMap<Short, TLVConfig>();
		for (int i = l - 1; i >= 0; i--) {
			Class curCls = classes.get(i);
			Field[] fields = curCls.getDeclaredFields();
			for (Field f : fields) {
				TLVField config = f.getAnnotation(TLVField.class);
				if (config != null) {
					short tag = config.index();
					f.setAccessible(true);
					TLVConfig tlvConfig = new TLVConfig(f, tag);
					clsTLVConfigs.put(tag, tlvConfig);
				}
			}
		}
		return clsTLVConfigs;
	}

	@SuppressWarnings("rawtypes")
	public static boolean hasTLVConfig(Class cls) {
		List<Class> classes = new ArrayList<Class>();
		classes.add(cls);
		while ((cls = cls.getSuperclass()) != Object.class) {
			classes.add(cls);
		}
		int l = classes.size();
		for (int i = l - 1; i >= 0; i--) {
			Class curCls = classes.get(i);
			Field[] fields = curCls.getDeclaredFields();
			for (Field f : fields) {
				TLVField config = f.getAnnotation(TLVField.class);
				if (config != null) {
					return true;
				}
			}
		}
		return false;
	}

	/** encode a short to 2 bytes(big endian) */
	private static byte[] s2b(short s) {
		byte[] bytes = new byte[2];
		bytes[1] = (byte) s;
		bytes[0] = (byte) (s >>> 8);
		return bytes;
	}

	/** decode 2 bytes to short value */
	private static short b2s(byte[] bytes) {
		if (bytes.length != 2) {
			throw new IllegalArgumentException("can't decode " + bytes.length + " bytes to 2 bytes short value");
		}
		short val = 0;
		val += (bytes[0] & 0xff) << 8;
		val += bytes[1] & 0xff;
		return val;
	}

	/** endcode a integer to 4 bytes(big endian) */
	private static byte[] i2b(int i) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (i >>> 24);
		bytes[1] = (byte) (i >>> 16);
		bytes[2] = (byte) (i >>> 8);
		bytes[3] = (byte) i;
		return bytes;
	}

	/** decode 4 bytes(big endian) to a integer */
	private static int b2i(byte[] bytes) {
		int val = 0;
		int l = bytes.length - 1;
		if (l != 3) {
			throw new IllegalArgumentException("can't convert " + (l + 1) + " bytes to 4 bytes int");
		}
		for (int i = 0; i <= l; i++) {
			byte b = bytes[i];
			int t = (int) (b & 0xff);
			val += t << ((l - i) << 3);
		}
		return val;
	}

	/** encode a long to 8 bytes(big endian) */
	private static byte[] l2b(long l) {
		byte[] bytes = new byte[8];
		bytes[0] = (byte) (l >>> 56);
		bytes[1] = (byte) (l >>> 48);
		bytes[2] = (byte) (l >>> 40);
		bytes[3] = (byte) (l >>> 32);
		bytes[4] = (byte) (l >>> 24);
		bytes[5] = (byte) (l >>> 16);
		bytes[6] = (byte) (l >>> 8);
		bytes[7] = (byte) l;
		return bytes;
	}

	/** decode 8 bytes(big edian) to a long */
	private static long b2l(byte[] bytes) {
		long val = 0;
		int l = bytes.length - 1;
		if (l != 7) {
			throw new IllegalArgumentException("can't convert " + (l + 1) + " bytes to 8 bytes long");
		}
		for (int i = 0; i <= l; i++) {
			byte b = bytes[i];
			long t = b & 0xff;
			val += t << ((l - i) << 3);
		}
		return val;
	}

	private static byte[] f2b(float f) {
		try {
			return Float.toString(f).getBytes(CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private static float b2f(byte[] bytes) {
		try {
			return Float.valueOf(new String(bytes, CHARSET));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static byte[] d2b(double d) {
		try {
			return Double.toString(d).getBytes(CHARSET);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private static double b2d(byte[] bytes) {
		try {
			return Double.valueOf(new String(bytes, CHARSET));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
