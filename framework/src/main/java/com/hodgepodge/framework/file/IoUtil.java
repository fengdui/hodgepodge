package com.hodgepodge.framework.file;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collection;

public class IoUtil {

    /**
     * 默认缓存大小
     */
    public final static int DEFAULT_BUFFER_SIZE = 1024;

    /**
     * 将Reader中的内容复制到Writer中 使用默认缓存大小
     *
     * @param reader Reader
     * @param writer Writer
     * @return 拷贝的字节数
     * @throws IOException
     */
    public static int copy(Reader reader, Writer writer) throws IOException {
        return copy(reader, writer, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 将Reader中的内容复制到Writer中
     */
    public static int copy(Reader reader, Writer writer, int bufferSize) throws IOException {
        char[] buffer = new char[bufferSize];
        int count = 0;
        int readSize;
        while ((readSize = reader.read(buffer, 0, bufferSize)) >= 0) {
            writer.write(buffer, 0, readSize);
            count += readSize;
        }
        writer.flush();
        return count;
    }

    /**
     * 拷贝流，使用默认Buffer大小
     *
     * @param in  输入流
     * @param out 输出流
     * @throws IOException
     */
    public static int copy(InputStream in, OutputStream out) throws IOException {
        return copy(in, out, DEFAULT_BUFFER_SIZE);
    }

    /**
     * 拷贝流
     *
     * @param in         输入流
     * @param out        输出流
     * @param bufferSize 缓存大小
     * @throws IOException
     */
    public static int copy(InputStream in, OutputStream out, int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        int count = 0;
        for (int n = -1; (n = in.read(buffer)) != -1; ) {
            out.write(buffer, 0, n);
            count += n;
        }
        out.flush();

        return count;
    }

    /**
     * 拷贝文件流，使用NIO
     *
     * @param in  输入
     * @param out 输出
     * @return 拷贝的字节数
     * @throws IOException
     */
    public static long copy(FileInputStream in, FileOutputStream out) throws IOException {
        FileChannel inChannel = in.getChannel();
        FileChannel outChannel = out.getChannel();

        return inChannel.transferTo(0, inChannel.size(), outChannel);
    }

    /**
     * 获得一个文件读取器
     *
     * @param in      输入流
     * @param charset 字符集
     * @return BufferedReader对象
     * @throws IOException
     */
    public static BufferedReader getReader(InputStream in, String charset) throws IOException {
        InputStreamReader reader = null;
        if (StringUtils.isBlank(charset)) {
            reader = new InputStreamReader(in);
        } else {
            reader = new InputStreamReader(in, charset);
        }
        return new BufferedReader(reader);
    }

    /**
     * 从流中读取内容
     *
     * @param in      输入流
     * @param charset 字符集
     * @return 内容
     * @throws IOException
     */
    public static String getString(InputStream in, String charset) throws IOException {
        final long len = in.available();
        if (len >= Integer.MAX_VALUE) {
            throw new IOException("File is larger then max array size");
        }

        byte[] bytes = new byte[(int) len];
        in.read(bytes);
        return new String(bytes, charset);
    }

    /**
     * 从流中读取内容
     *
     * @param in         输入流
     * @param charset    字符集
     * @param collection 返回集合
     * @return 内容
     * @throws IOException
     */
    public static <T extends Collection<String>> T getLines(InputStream in, String charset, T collection) throws IOException {
        // 从返回的内容中读取所需内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
        String line = null;
        while ((line = reader.readLine()) != null) {
            collection.add(line);
        }

        return collection;
    }

    /**
     * 从FileChannel中读取内容
     *
     * @param fileChannel 文件管道
     * @param charset     字符集
     * @return 内容
     * @throws IOException
     */
    public static String getString(FileChannel fileChannel, String charset) throws IOException {
        final MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size()).load();
//		return CharsetUtil.str(buffer, charset);
        return "";
    }

    /**
     * String 转为 流
     *
     * @param content 内容
     * @param charset 编码
     * @return 字节流
     */
    public static ByteArrayInputStream toStream(String content, String charset) {
        if (content == null) {
            return null;
        }

        byte[] data = null;
        try {
            data = StringUtils.isBlank(charset) ? content.getBytes() : content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(String.format("Invalid charset [{}] !", charset), e);
        }

        return new ByteArrayInputStream(data);
    }

    /**
     * 将多部分内容写到流中，自动转换为字符串
     *
     * @param out        输出流
     * @param charset    写出的内容的字符集
     * @param isCloseOut 写入完毕是否关闭输出流
     * @param contents   写入的内容，调用toString()方法，不包括不会自动换行
     * @throws IOException
     */
    public static void write(OutputStream out, String charset, boolean isCloseOut, Object... contents) throws IOException {
        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(out, charset);
            for (Object content : contents) {
                if (content != null) {
                    osw.write(content.toString());
                }
            }
        } catch (Exception e) {
            throw new IOException("Write content to OutputStream error!", e);
        } finally {
            if (isCloseOut) {
                FileUtil.close(osw);
            }
        }
    }

    public static byte[] inputStreamToByte(InputStream inputStream) throws IOException {
        return IOUtils.toByteArray(inputStream);
    }

}
