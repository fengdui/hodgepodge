package com.fengdui.wheel.serialize;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fengdui.wheel.media.TLVUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;


public class ProtocolUtil {

	private static final String METHOD_GET = "GET";
	private static final String METHOD_POST = "POST";

	private static int TIMEOUT_CONNECTION = 20000;
	private static int TIMEOUT_SO = 20000;
	private static int TIMEOUT_READ = 20000;

	private static String PROTOCOL_HEADER_KEY;
	private static String PROTOCOL_SECURE_KEY;

	public static void setTimeout(int timeoutConnection, int timeout) {
		TIMEOUT_CONNECTION = timeoutConnection;
		TIMEOUT_SO = timeout;
		TIMEOUT_READ = TIMEOUT_SO;
	}

	public static void setProtocolKey(String protocolHeaderKey, String protocolSecureKey) {
		PROTOCOL_HEADER_KEY = protocolHeaderKey;
		PROTOCOL_SECURE_KEY = protocolSecureKey;
	}

	public static void setProtocolHeaderKey(String protocolHeaderKey) {
		PROTOCOL_HEADER_KEY = protocolHeaderKey;
	}

	public static void setProtocolSecureKey(String protocolSecureKey) {
		PROTOCOL_SECURE_KEY = protocolSecureKey;
	}

	public static <T> T sendDataByGet(String urlStr, Object objReq, Class<T> classResp) throws Exception {
		return sendDataByGet(urlStr, objReq, classResp, null, null);
	}

	public static <T> T sendDataByGet(String urlStr, Object objReq, Class<T> classResp, String serializeType, Boolean encrypt) throws Exception {
		return sendDataByGet(urlStr, objReq, classResp, null, serializeType, encrypt);
	}

	public static <T> T sendDataByGet(String urlStr, Object objReq, Class<T> classResp, Class<?> classRespComponent, String serializeType, Boolean encrypt) throws Exception {
		return sendData(METHOD_GET, urlStr, objReq, classResp, classRespComponent, serializeType, encrypt);
	}

	public static <T> T sendDataByPost(String urlStr, Object objReq, Class<T> classResp) throws Exception {
		return sendDataByPost(urlStr, objReq, classResp, null, null);
	}

	public static <T> T sendDataByPost(String urlStr, Object objReq, Class<T> classResp, String serializeType, Boolean encrypt) throws Exception {
		return sendDataByPost(urlStr, objReq, classResp, null, serializeType, encrypt);
	}

	public static <T> T sendDataByPost(String urlStr, Object objReq, Class<T> classResp, Class<?> classRespComponent, String serializeType, Boolean encrypt) throws Exception {
		return sendData(METHOD_POST, urlStr, objReq, classResp, classRespComponent, serializeType, encrypt);
	}

	public static <T> T sendData(String methodType, String urlStr, Object objReq, Class<T> classResp, Class<?> classRespComponent, String serializeType, Boolean encrypt) throws Exception {
		if (StringUtils.isBlank(methodType) || (!METHOD_GET.equalsIgnoreCase(methodType) && !METHOD_POST.equalsIgnoreCase(methodType))) {
			throw new Exception("method type is wrong");
		}

		if (METHOD_GET.equalsIgnoreCase(methodType)) {
			return sendDataWithGet(methodType, urlStr, objReq, classResp, classRespComponent, serializeType, encrypt);
		} else if (METHOD_POST.equalsIgnoreCase(methodType)) {
			return sendDataWithPost(urlStr, objReq, classResp, classRespComponent, serializeType, encrypt);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static <T> T sendDataWithGet(String methodType, String urlStr, Object objReq, Class<T> classResp, Class<?> classRespComponent, String serializeType, Boolean encrypt)
			throws Exception {
		Class<?> classObjReq = objReq.getClass();
		if (METHOD_GET.equalsIgnoreCase(methodType)) {
			urlStr = buildUrlOfGet(objReq, classObjReq, urlStr);
		}

		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(methodType);
		if (TIMEOUT_CONNECTION > 0) {
			connection.setConnectTimeout(TIMEOUT_CONNECTION);
		}
		if (TIMEOUT_READ > 0) {
			connection.setReadTimeout(TIMEOUT_READ);
		}

		if (METHOD_GET.equalsIgnoreCase(methodType)) {
			Object[] httpInfo = buildHttpInfo(null, serializeType, encrypt);
			Map<String, String> header = (Map<String, String>) httpInfo[0];
			for (Entry<String, String> entry : header.entrySet()) {
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}

		connection.connect();

		int resultCode = connection.getResponseCode();
		InputStream is = null;
		if (resultCode < HttpURLConnection.HTTP_BAD_REQUEST) {
			is = connection.getInputStream();
		} else {
			is = connection.getErrorStream();
		}
		DataInputStream input = new DataInputStream(is);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] bufferByte = new byte[1024];
		int j = -1;
		while ((j = input.read(bufferByte)) > -1) {
			out.write(bufferByte, 0, j);
			out.flush();
		}
		byte[] resultByte = out.toByteArray();

		if (resultCode == HttpURLConnection.HTTP_OK) {
			Map<String, String> headerMap = new HashMap<String, String>();
			for (Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
				headerMap.put(header.getKey(), header.getValue().get(0));
			}
			headerMap = fillHeaderInfo(headerMap);
			return generateObj(classResp, classRespComponent, headerMap, resultByte);
		} else {
			throw new Exception(new String(resultByte));
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> T sendDataWithPost(String urlStr, Object objReq, Class<T> classResp, Class<?> classRespComponent, String serializeType, Boolean encrypt) throws Exception {
		HttpPost httpPost = new HttpPost(urlStr);
		Class<?> classObjReq = objReq.getClass();
		HttpEntity entity = null;

		if (Map.class.isAssignableFrom(classObjReq)) {
			Map<String, String> objReqMap = (Map<String, String>) objReq;
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (Entry<String, String> entry : objReqMap.entrySet()) {
				list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			entity = new UrlEncodedFormEntity(list, "UTF-8");
		} else {
			byte[] byteArray = null;

			Object[] headerInfo = checkHeaderInfo(classObjReq, serializeType, encrypt);
			serializeType = (String) headerInfo[0];
			encrypt = (Boolean) headerInfo[1];

			Object[] httpInfo = buildHttpInfo(objReq, serializeType, encrypt);
			Map<String, String> header = (Map<String, String>) httpInfo[0];
			for (Entry<String, String> entry : header.entrySet()) {
				httpPost.setHeader(entry.getKey(), entry.getValue());
			}
			byteArray = (byte[]) httpInfo[1];
			entity = new ByteArrayEntity(byteArray);
		}
		// httpPost.setHeader("Connection", "close");
		httpPost.setEntity(entity);

		HttpClient client = new DefaultHttpClient();
		if (TIMEOUT_CONNECTION > 0) {
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT_CONNECTION);
		}
		if (TIMEOUT_SO > 0) {
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, TIMEOUT_SO);
		}
		HttpResponse httpResponse = client.execute(httpPost);

		InputStream input = new DataInputStream(httpResponse.getEntity().getContent());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] bufferByte = new byte[1024];
		int j = -1;
		while ((j = input.read(bufferByte)) > -1) {
			out.write(bufferByte, 0, j);
			out.flush();
		}
		byte[] resultByte = out.toByteArray();

		// 关闭连接
		if (null != input) {
			input.close();
		}
		httpPost.releaseConnection();
		client.getConnectionManager().shutdown();
		
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			Map<String, String> headerMap = new HashMap<String, String>();
			for (Header header : httpResponse.getAllHeaders()) {
				headerMap.put(header.getName(), header.getValue());
			}
			headerMap = fillHeaderInfo(headerMap);
			return generateObj(classResp, classRespComponent, headerMap, resultByte);
		} else {
			throw new Exception(new String(resultByte));
		}
	}

	/*@SuppressWarnings("unchecked")
	public static <T> T sendData(String methodType, String urlStr, Object objReq, Class<T> classResp, Class<?> classRespComponent, String serializeType, Boolean encrypt) throws Exception {
		if (StringUtils.isBlank(methodType) || (!METHOD_GET.equalsIgnoreCase(methodType) && !METHOD_POST.equalsIgnoreCase(methodType))) {
			throw new Exception("method type is wrong");
		}

		Class<?> classObjReq = objReq.getClass();
		if (METHOD_GET.equalsIgnoreCase(methodType)) {
			urlStr = buildUrlOfGet(objReq, classObjReq, urlStr);
		}

		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(methodType);
		if (TIMEOUT_CONNECTION > 0) {
			connection.setConnectTimeout(TIMEOUT_CONNECTION);
		}
		if (TIMEOUT_READ > 0) {
			connection.setReadTimeout(TIMEOUT_READ);
		}

		if (METHOD_POST.equalsIgnoreCase(methodType)) {
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);

			byte[] paramsByte = null;
			if (Map.class.isAssignableFrom(classObjReq)) {
				paramsByte = getParam((Map<String, String>) objReq).getBytes();
			} else {
				Object[] headerInfo = checkHeaderInfo(classObjReq, serializeType, encrypt);
				serializeType = (String) headerInfo[0];
				encrypt = (Boolean) headerInfo[1];

				Object[] httpInfo = buildHttpInfo(objReq, serializeType, encrypt);
				Map<String, String> header = (Map<String, String>) httpInfo[0];
				for (Entry<String, String> entry : header.entrySet()) {
					connection.setRequestProperty(entry.getKey(), entry.getValue());
				}
				paramsByte = (byte[]) httpInfo[1];
			}

			// 发送请求参数
			DataOutputStream printOut = new DataOutputStream(connection.getOutputStream());
			printOut.write(paramsByte);
			printOut.flush();
			printOut.close();
		} else if (METHOD_GET.equalsIgnoreCase(methodType)) {
			Object[] httpInfo = buildHttpInfo(null, serializeType, encrypt);
			Map<String, String> header = (Map<String, String>) httpInfo[0];
			for (Entry<String, String> entry : header.entrySet()) {
				connection.setRequestProperty(entry.getKey(), entry.getValue());
			}
		}

		connection.connect();

		int resultCode = connection.getResponseCode();
		InputStream is = null;
		if (resultCode < HttpURLConnection.HTTP_BAD_REQUEST) {
			is = connection.getInputStream();
		} else {
			is = connection.getErrorStream();
		}
		DataInputStream input = new DataInputStream(is);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] bufferByte = new byte[1024];
		int j = -1;
		while ((j = input.read(bufferByte)) > -1) {
			out.write(bufferByte, 0, j);
			out.flush();
		}
		byte[] resultByte = out.toByteArray();

		if (resultCode == HttpURLConnection.HTTP_OK) {
			Map<String, String> headerMap = new HashMap<String, String>();
			for (Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
				headerMap.put(header.getKey(), header.getValue().get(0));
			}
			headerMap = fillHeaderInfo(headerMap);
			return generateObj(classResp, classRespComponent, headerMap, resultByte);
		} else {
			throw new Exception(new String(resultByte));
		}
	}*/

	@SuppressWarnings("unchecked")
	private static String buildUrlOfGet(Object obj, Class<?> classObj, String url) throws Exception {
		if (null != obj) {
			if (url.contains("?")) {
				if (!url.endsWith("?") && !url.endsWith("&")) {
					url += "&";
				}
			} else {
				url += "?";
			}
			if (classObj == String.class) {
				url += obj;
			} else if (Map.class.isAssignableFrom(classObj)) {
				url += getParam((Map<String, String>) obj);
			} else {
				throw new Exception("objReq type is wrong");
			}
		}
		return url;
	}

	private static String getParam(Map<String, String> map) {
		String params = null;
		for (Entry<String, String> entry : map.entrySet()) {
			if (null != params) {
				params += "&";
			} else {
				params = "";
			}
			params += entry.getKey() + "=" + entry.getValue();
		}
		return params;
	}

	public static Object[] checkHeaderInfo(Class<?> classObj, String serializeType, Boolean encrypt) throws Exception {
		if (classObj.isPrimitive() || ClassTypeUtil.isWrapClass(classObj) || classObj.isArray() || List.class.isAssignableFrom(classObj) || classObj == String.class
				|| classObj == Date.class) {
			if (null == encrypt) {
				encrypt = false;
			}
		} else {
			if (StringUtils.isBlank(serializeType) || (!serializeType.equals(SerializeTypeEnum.DEFAULT.getType()) && !serializeType.equals(SerializeTypeEnum.TLV.getType()))) {
				serializeType = SerializeTypeEnum.DEFAULT.getType();
			}
			if (serializeType.equals(SerializeTypeEnum.DEFAULT.getType())) {
				if (Serializable.class.isAssignableFrom(classObj)) {
					encrypt = needEncrypt(classObj, encrypt);
				} else {
					if (TLVUtil.hasTLVConfig(classObj)) {
						serializeType = SerializeTypeEnum.TLV.getType();
						encrypt = needEncrypt(classObj, encrypt);
					} else {
						throw new Exception("无法序列化");
					}
				}
			} else if (serializeType.equals(SerializeTypeEnum.TLV.getType())) {
				if (TLVUtil.hasTLVConfig(classObj)) {
					encrypt = needEncrypt(classObj, encrypt);
				} else {
					if (Serializable.class.isAssignableFrom(classObj)) {
						serializeType = SerializeTypeEnum.DEFAULT.getType();
						encrypt = needEncrypt(classObj, encrypt);
					} else {
						throw new Exception("无法序列化");
					}
				}
			} else {
				throw new Exception("无法序列化");
			}
		}
		return new Object[] { serializeType, encrypt };
	}

	public static boolean needEncrypt(Class<?> clazz, Boolean encrypt) {
		if (null == encrypt) {
			SerializeClass serializeClassAnno = clazz.getAnnotation(SerializeClass.class);
			if (null == serializeClassAnno) {
				encrypt = false;
			} else {
				encrypt = serializeClassAnno.encrypt();
			}
		}
		return encrypt;
	}

	public static String buildHeaderVal(String serializeType, Boolean encrypt) {
		StringBuffer headerVal = new StringBuffer();
		headerVal.append(ConstantProtocol.VERSION_KEY + "=" + ConstantProtocol.VERSION_VAL);
		if (StringUtils.isNotBlank(serializeType)) {
			headerVal.append(";");
			headerVal.append(ConstantProtocol.SERIALIZE_TYPE_KEY + "=" + serializeType);
		}
		if (null != encrypt) {
			headerVal.append(";");
			headerVal.append(ConstantProtocol.ENCRYPT_KEY + "=" + encrypt);
		}
		return headerVal.toString();
	}

	public static Object[] buildHttpInfo(Object obj, String serializeType, Boolean encrypt) throws Exception {
		DESUtil desUtil = DESUtil.getInstance(PROTOCOL_SECURE_KEY);
		Map<String, String> header = new HashMap<String, String>();
		byte[] body = null;
		if (StringUtils.isNotBlank(serializeType)) {
			if (serializeType.equals(SerializeTypeEnum.DEFAULT.getType())) {
				header.put(PROTOCOL_HEADER_KEY, desUtil.encrypt(buildHeaderVal(serializeType, encrypt)));
				body = ByteObjConverter.ObjectToByte(obj);
			} else if (serializeType.equals(SerializeTypeEnum.TLV.getType())) {
				header.put(PROTOCOL_HEADER_KEY, desUtil.encrypt(buildHeaderVal(serializeType, encrypt)));
				body = TLVUtil.encode(obj);
			} else {
				body = ByteObjConverter.ObjectToByte(obj);
			}
		} else {
			body = ByteObjConverter.ObjectToByte(obj);
		}
		if (null != encrypt && encrypt) {
			if (null == header.get(PROTOCOL_HEADER_KEY)) {
				header.put(PROTOCOL_HEADER_KEY, desUtil.encrypt(buildHeaderVal(SerializeTypeEnum.DEFAULT.getType(), encrypt)));
			}
			body = desUtil.encrypt(body);
		}
		return new Object[] { header, body };
	}

	/** 解析协议头 */
	public static Map<String, String> fillHeaderInfo(Map<String, String> headerMap) throws Exception {
		if (StringUtils.isNotBlank(PROTOCOL_HEADER_KEY)) {
			String protocolHeader = headerMap.get(PROTOCOL_HEADER_KEY);
			if (StringUtils.isNotBlank(protocolHeader)) {
				try {
					DESUtil desUtil = DESUtil.getInstance(PROTOCOL_SECURE_KEY);
					String protocolHeaderVal = desUtil.decrypt(protocolHeader);
					String[] propGroup = protocolHeaderVal.split(ConstantProtocol.SEPARATOR_GROUP);
					for (String prop : propGroup) {
						String[] propArray = prop.split(ConstantProtocol.SEPARATOR_PROPERTY);
						headerMap.put(propArray[0], propArray[1]);
					}
				} catch (Exception e) {
					throw new Exception("DESUtil decrypt [" + protocolHeader + "] error : " + e);
				}
			}
		}
		return headerMap;
	}

	/** 解析 object 内容 */
	@SuppressWarnings("unchecked")
	public static <T> T generateObj(Class<T> clazz, Class<?> classComponent, Map<String, String> headers, byte[] byteArray) throws Exception {
		Object obj = null;
		if (byteArray.length > 0) {
			boolean encrypt = false;
			try {
				String encryptVal = headers.get(ConstantProtocol.ENCRYPT_KEY);
				if (StringUtils.isBlank(encryptVal)) {
					encrypt = false;
				} else {
					encrypt = Boolean.parseBoolean(encryptVal);
				}
			} catch (Exception e) {
				e.printStackTrace();
				encrypt = false;
			}
			if (encrypt) {
				try {
					DESUtil desUtil = DESUtil.getInstance(PROTOCOL_SECURE_KEY);
					byteArray = desUtil.decrypt(byteArray);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			String serializeType = headers.get(ConstantProtocol.SERIALIZE_TYPE_KEY);
			if (StringUtils.isBlank(serializeType) || (!serializeType.equals(SerializeTypeEnum.DEFAULT.getType()) && !serializeType.equals(SerializeTypeEnum.TLV.getType()))) {
				serializeType = SerializeTypeEnum.DEFAULT.getType();
			}
			if (serializeType.equals(SerializeTypeEnum.DEFAULT.getType())) {
				try {
					obj = ByteObjConverter.ByteToObject(byteArray);
				} catch (Exception e) {
					try {
						obj = TLVUtil.decode(byteArray, clazz, classComponent);
					} catch (Exception e2) {
						throw new Exception("getObjectByTLV error : " + e);
					}
				}
			} else if (serializeType.equals(SerializeTypeEnum.TLV.getType())) {
				try {
					obj = TLVUtil.decode(byteArray, clazz, classComponent);
				} catch (Exception e) {
					try {
						obj = ByteObjConverter.ByteToObject(byteArray);
					} catch (Exception e2) {
						throw new Exception("getObjectByDefault error : " + e);
					}
				}
			}
		}
		return (T) obj;
	}

}
