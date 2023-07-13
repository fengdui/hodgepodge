package com.hodgepodge.framework.generic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.SneakyThrows;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;

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

    public void s() {
        Method[] allDeclaredMethods = ReflectionUtils.getAllDeclaredMethods(MyReflectUtils.class);
    }

    public static class ResultBean<T> {
        private T data;
    }
    public static class CanEncryptDataDTO {

    }
    public void a() {
        String result = "null";
        ResultBean<CanEncryptDataDTO> resultBean = JSON.parseObject(result, new TypeReference<ResultBean<CanEncryptDataDTO>>() {
        });
    }

    @SneakyThrows
    public Object invoke() {
        return MethodUtils.invokeMethod(new MethodUtils(), true, "springGeneric", null);

    }

    public void springGeneric() throws NoSuchFieldException {

        ResolvableType.forField(getClass().getDeclaredField("myMap"));

    }
}
