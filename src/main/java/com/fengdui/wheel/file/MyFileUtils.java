package com.fengdui.wheel.file;

import net.smacke.jaydio.DirectRandomAccessFile;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class MyFileUtils {

    public MyFileUtils() throws FileNotFoundException {}

    public void fileChannel() throws IOException {
        byte[] data = new byte[4096];
        long position = 1024L;
        //指定 position 写入 4kb 的数据
        FileChannel fileChannel = new RandomAccessFile(new File("db.data"), "rw").getChannel();
        fileChannel.write(ByteBuffer.wrap(data), position);
        //从当前文件指针的位置写入 4kb 的数据
        fileChannel.write(ByteBuffer.wrap(data));
        // 读
        ByteBuffer buffer = ByteBuffer.allocate(4096);
        //指定 position 读取 4kb 的数据
        fileChannel.read(buffer, position);
        //从当前文件指针的位置读取 4kb 的数据
        fileChannel.read(buffer);
    }

    /**
     * 顺序读写是优先分配一块文件空间，然后后续内容追加到对应空间内。
     *
     * 在使用顺序IO进行文件读写时候，需要知道上次写入的地方，所以需要维护一个索引或者轮询获得一个没有写入位置。
     * @throws IOException
     */
    public void mappedByteBuffer() throws IOException {
        FileChannel fileChannel = new RandomAccessFile(new File("db.data"), "rw").getChannel();
        MappedByteBuffer mappedByteBuffer = fileChannel.map(
                FileChannel.MapMode.READ_WRITE, 0, fileChannel.size());
        // 写
        byte[] data = new byte[4];
        int position = 8;
        //从当前 mmap 指针的位置写入 4b 的数据
        mappedByteBuffer.put(data);
        //指定 position 写入 4b 的数据
        ByteBuffer subBuffer = mappedByteBuffer.slice();
        subBuffer.position(position);
        subBuffer.put(data);
        // 读
        //从当前 mmap 指针的位置读取 4b 的数据
        mappedByteBuffer.get(data);
        //指定 position 读取 4b 的数据
        subBuffer = mappedByteBuffer.slice();
        subBuffer.position(position);
        subBuffer.get(data);
    }

    public static void clean(MappedByteBuffer mappedByteBuffer) {
        ByteBuffer buffer = mappedByteBuffer;
        if (buffer == null || !buffer.isDirect() || buffer.capacity() == 0)
            return;
        invoke(invoke(viewed(buffer), "cleaner"), "clean");
    }

    private static Object invoke(final Object target, final
    String methodName, final Class<?>... args) {
        return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                try {
                    Method method = method(target, methodName, args);
                    method.setAccessible(true);
                    return method.invoke(target);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        });
    }
    private static Method method(Object target, String methodName, Class<?>[] args) throws
            NoSuchMethodException {
        try {
            return target.getClass().getMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            return target.getClass().getDeclaredMethod(methodName, args);
        }
    }
    private static ByteBuffer viewed(ByteBuffer buffer) {
        String methodName = "viewedBuffer";
        Method[] methods = buffer.getClass().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals("attachment")) {
                methodName = "attachment";
                break;
            }
        }
        ByteBuffer viewedBuffer = (ByteBuffer) invoke(buffer, methodName);
        if (viewedBuffer == null)
            return buffer;
        else
            return viewed(viewedBuffer);
    }

    private AtomicLong wrotePosition;
    private FileChannel fileChannel = new RandomAccessFile(new File("db.data"), "rw").getChannel();


    /**
     * 顺序写
     */
    public void OrderWrite() {
        ExecutorService executor = Executors.newFixedThreadPool(64);
        wrotePosition = new AtomicLong(0);
        for (int i = 0; i < 1024; i++) {
            final int index = i;
            executor.execute(() -> {
                try {
                    write(new byte[4 * 1024]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    public synchronized void write(byte[] data) throws IOException {
        fileChannel.write(ByteBuffer.wrap(new byte[4 * 1024]), wrotePosition.getAndAdd(4 * 1024));
    }

    public static final Unsafe UNSAFE;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe) field.get(null);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void directIO(){
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * 1024 * 1024);
        long addresses = ((DirectBuffer) buffer).address();
        byte[] data = new byte[4 * 1024 * 1024];
        UNSAFE.copyMemory(data, 16, null, addresses, 4 * 1024 * 1024);
    }

    public void jaydIO() throws IOException {
        int bufferSize = 20 * 1024 * 1024;
        DirectRandomAccessFile directFile = new DirectRandomAccessFile(new File("dio.data"), "rw", bufferSize);
        for (int i = 0; i < bufferSize / 4096; i++){
            byte[] buffer = new byte[4 * 1024];
            directFile.read(buffer);
            directFile.readFully(buffer);
        }
        directFile.close();
    }
}
