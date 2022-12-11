package main.java.com.hodgepodge.framework.id;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class EmptyUtils {
    private EmptyUtils() {
    }

    public static boolean isNotBlank(Object... params) {
        if (isBlankP(params)) {
            return false;
        } else {
            for (int i = 0; i < params.length; ++i) {
                Object param = params[i];
                if (isBlankP(param)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isBlank(Object... params) {
        if (isBlankP(params)) {
            return true;
        } else {
            for (int i = 0; i < params.length; ++i) {
                Object param = params[i];
                if (!isBlankP(param)) {
                    return false;
                }
            }

            return true;
        }
    }

    private static boolean isBlankP(Object param) {
        if (param == null) {
            return true;
        } else if (param instanceof String) {
            String temp = ((String) param).trim();
            return temp.length() == 0;
        } else if (param.getClass().isArray()) {
            return Array.getLength(param) == 0;
        } else if (param instanceof Collection) {
            return ((Collection) param).isEmpty();
        } else if (param instanceof Map) {
            return ((Map) param).size() == 0;
        } else {
            return false;
        }
    }
}
