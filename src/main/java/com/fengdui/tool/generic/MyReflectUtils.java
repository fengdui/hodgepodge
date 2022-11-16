package com.fengdui.tool.generic;

import org.springframework.core.DefaultParameterNameDiscoverer;

import java.lang.reflect.Method;

/**
 * @author FD
 * @since 2020/10/22 4:10 下午
 */
public class MyReflectUtils {

    //java8可以通过反射获取参数名，StandardReflectionParameterNameDiscoverer
    //但是需要使用-parameters参数开启这个功能
    //低于1.8时使用new LocalVariableTableParameterNameDiscoverer()来解析参数名
    public String[] getParameterNames(Method method) throws NoSuchMethodException {
        return new DefaultParameterNameDiscoverer().getParameterNames(String.class.getMethod("toString", null));
    }
}
