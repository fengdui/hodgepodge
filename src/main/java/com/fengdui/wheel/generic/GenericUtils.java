package com.fengdui.wheel.generic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericUtils.class);

    /**
     * 获取泛型类型
     * @param clazz
     * @param index
     * @return
     */
    public static Class getClassGenericType(final Class clazz, final int index) {

        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            LOGGER.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
            return Object.class;
        }

        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if ((index >= params.length) || (index < 0)) {
            LOGGER.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
                    + params.length);
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            LOGGER.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
            return Object.class;
        }

        return (Class) params[index];
    }

    public void springGeneric() throws NoSuchFieldException {

        ResolvableType.forField(getClass().getDeclaredField("myMap"));

    }
}
