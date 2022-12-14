package com.hodgepodge.framework.spring;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Value;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by taoli on 2022/10/14.
 * gitee : https://gitee.com/litao851025/lego
 * 编程就像玩 Lego
 */
@Value
public class ParamMethod {
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();
    private final Object bean;
    private final Method method;
    @Getter
    private final Class[] paramTypes;
    @Getter
    private final String[] paramNames;

    @Getter
    private final MethodParameter[] namedMethodParameters;

    @Getter
    private final HandlerMethod handlerMethod;

    public ParamMethod(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
        this.paramTypes = method.getParameterTypes();
        this.paramNames = PARAMETER_NAME_DISCOVERER.getParameterNames(this.method);
        this.namedMethodParameters = buildParameters();
        this.handlerMethod = new HandlerMethod(bean, method);
    }

    private MethodParameter[] buildParameters() {
        MethodParameter[] result = new MethodParameter[getParamTypes().length];
        for (int i = 0; i < getParamNames().length; i++) {
            String paramName = getParamNames()[i];
            Class paramCls = getParamTypes()[i];
            Set<Class<? extends Annotation>> annotations = Sets.newHashSet();
            if (BeanUtils.isSimpleValueType(paramCls)) {
                annotations.add(RequestParam.class);
            } else {
                annotations.add(ModelAttribute.class);
            }
            MethodParameter methodParameter = new MethodParameter(getMethod(), i);
            result[i] = methodParameter;
        }
        return result;
    }

    public Object invoke(Object[] params) throws Exception {
        return MethodUtils.invokeMethod(this.bean, this.method.getName(), params);
    }
}
