package com.fengdui.framework.classloader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class ClassLoaderUtil {
    private static Method ADD_URL = initAddMethod();
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassLoaderUtil.class);
    private static URLClassLoader CLASSLOADER = (URLClassLoader) ClassLoader.getSystemClassLoader();

    private static final Method initAddMethod() {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            return method;
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    private static final void loopFiles(File file, List<File> files) {
        if (file.isDirectory()) {
            File[] tmps = file.listFiles();
            if ((tmps == null) || (tmps.length == 0)) {
                return;
            }
            for (File tmp : tmps) {
                loopFiles(tmp, files);
            }
        } else if ((file.getAbsolutePath().endsWith(".jar")) || (file.getAbsolutePath().endsWith(".zip"))) {
            files.add(file);
        }
    }

    public static final void loadJarFile(File file) {
        try {
            ADD_URL.invoke(CLASSLOADER, new Object[]{file.toURI().toURL()});
//			LOGGER.info("load jar[{}]", file.getAbsolutePath());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }

    public static final void loadJarPath(String path) {
        List<File> files = new ArrayList<File>();
        File lib = new File(path);
        loopFiles(lib, files);
        for (File file : files) {
            loadJarFile(file);
        }
    }

}
