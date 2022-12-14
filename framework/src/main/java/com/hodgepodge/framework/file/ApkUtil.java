package com.hodgepodge.framework.file;

import org.springframework.web.context.ContextLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ApkUtil {

    public static void getApkInfo(File apk) throws Exception {
        Process process = null;
        String command = "aapt dump badging " + apk.getAbsolutePath();
        try {
            process = Runtime.getRuntime().exec(command);
        } catch (Exception e) {
            try {
                command = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/resources/command") + File.separator + command;
                process = Runtime.getRuntime().exec(command);
            } catch (Exception e1) {
                process = null;
                throw e1;
            }
        }

        InputStream is = null;
        BufferedReader br = null;
        try {
            Map<String, List<String>> dumpMap = new HashMap<String, List<String>>();
            process.waitFor();
            is = process.getInputStream();
            if (is.available() <= 0) {
                throw new Exception("解析错误");
            }
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] lineArray = line.split(":", 2);
                if (lineArray.length == 2) {
                    String line0 = lineArray[0].trim();
                    String line1 = lineArray[1].trim();
                    List<String> list = dumpMap.get(line0);
                    if (null == list) {
                        list = new ArrayList<String>();
                    }
                    list.add(line1);
                    dumpMap.put(line0, list);
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (null != is) {
                    is.close();
                    is = null;
                }
                if (null != br) {
                    br.close();
                    br = null;
                }
                if (null != process) {
                    process.destroy();
                    process = null;
                }
            } catch (Exception e) {
                throw e;
            }
        }
    }

    @SuppressWarnings("resource")
    private static File getApkIconFile(File apkFile, String density, String iconPathOfZip) throws Exception {
        ZipFile zipFile = new ZipFile(apkFile);
        ZipEntry zipEntry = null;// 得到zip包中文件
        Enumeration<?> enume = zipFile.entries();

        while (enume.hasMoreElements()) {
            zipEntry = (ZipEntry) enume.nextElement();
            if (zipEntry.getName().equalsIgnoreCase(iconPathOfZip)) {
                InputStream is = null;
                try {
                    is = zipFile.getInputStream(zipEntry);
                    StringBuilder iconPath = new StringBuilder(apkFile.getParent());
                    iconPath.append(File.separator);
                    iconPath.append(apkFile.getName().substring(0, apkFile.getName().lastIndexOf(".")) + "_" + density + "_" + iconPathOfZip.substring(iconPathOfZip.lastIndexOf("/") + 1));
                    File iconFile = new File(iconPath.toString());
                    FileUtils.copyInputStreamToFile(is, iconFile);
                    return iconFile;
                } catch (Exception e) {
                    throw e;
                } finally {
                    try {
                        if (null != is) {
                            is.close();
                            is = null;
                        }
                    } catch (Exception e) {
                        throw e;
                    }
                }
            }
        }
        throw new Exception("没有找到" + iconPathOfZip);
    }
}
