package com.hodgepodge.framework.file;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Calendar;


/**
 * <p>
 * 指定存储路径工具类
 * </p>
 * <code>
 * 返回的路径都是基于指定绝对路径获得，绝对路径通过spring配置的.<br />
 * 变量名称为：file.store.path
 * </code>
 */
public class StorePathUtils {

    private static String storePath;

    /**
     * 获取指定相对路径的文件流
     *
     * @param relative
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream downloadByRelativePath(String relative) throws FileNotFoundException {
        File file = new File(PathUtils.trimEndFileSeparator(getStorePath())
                + PathUtils.appendBeginFileSeparator(relative));
        return new FileInputStream(file);
    }

    /**
     * 获取指定相对路径的文件
     *
     * @param relative
     * @return
     * @throws FileNotFoundException
     */
    public static File getFileByRelativePath(String relative) {
        File file = new File(PathUtils.trimEndFileSeparator(getStorePath())
                + PathUtils.appendBeginFileSeparator(relative));
        return file;
    }

    /**
     * 获取指定相对路径的绝对路径
     *
     * @param relative
     * @return
     */
    public static String getPathByRelativePath(String relative) {
        return PathUtils.trimEndFileSeparator(getStorePath())
                + PathUtils.appendBeginFileSeparator(relative);
    }

    /**
     * 获取指定相对路径的文件二进制数据
     *
     * @param relative
     * @return
     * @throws IOException
     */
    public static byte[] getByteByRelativePath(String relative) throws IOException {
        File file = getFileByRelativePath(relative);
        return FileUtils.readFileToByteArray(file);
    }

    /**
     * 获取指定相对路径文件流
     *
     * @param relative
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream getInputStreamByRelativePath(String relative) throws FileNotFoundException {
        File file = getFileByRelativePath(relative);
        return new FileInputStream(file);
    }

    /**
     * 获取指定绝对路径文件流
     *
     * @param filePath
     * @return
     * @throws FileNotFoundException
     */
    public static InputStream download(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        return new FileInputStream(file);
    }

    /**
     * 返回指定文件夹的具体绝对路径
     *
     * @param folder
     * @return
     */
    public static String getStorePath(final String folder) {
        return new StringBuffer().append(PathUtils.trimEndFileSeparator(getStorePath())).append(
                        PathUtils.appendBeginFileSeparator(folder)).append(PathUtils.appendBeginFileSeparator(getSubFolder()))
                .toString();
    }

    /**
     * 根据指定路径返回相对路径
     *
     * @param path
     * @return
     */
    public static String getSubFolder(final String path) {
        if (StringUtils.isNotBlank(path)) {
            return PathUtils.appendBeginFileSeparator(path.replaceFirst(PathUtils.replacePathSeparator(getStorePath()),
                    ""));
        }
        return null;
    }

    /**
     * 根据当前年份和月份组成一个子目录名称并返回，如1209
     *
     * @return
     */
    public static String getSubFolder() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        return new StringBuffer().append(year).append(month).toString();
    }

    /**
     * 获取配置的路径
     *
     * @return
     */
    public static String getStorePath() {
        return storePath;
    }

}
