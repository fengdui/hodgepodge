package com.hodgepodge.framework.spring;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

@Slf4j
public abstract class DispatcherController {
    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @SneakyThrows
    protected Object runMethod(String serviceName, String method, NativeWebRequest webRequest, ParamMethod queryMethod) {

        Object[] params = convertToParams(queryMethod, webRequest);
        Object result = queryMethod.invoke(params);
        return result;
    }

    @SneakyThrows
    private Object[] convertToParams(ParamMethod method, NativeWebRequest webRequest) {
        Object[] result = new Object[method.getNamedMethodParameters().length];
        for (int i = 0; i < method.getNamedMethodParameters().length; i++) {
            MethodParameter methodParameter = method.getNamedMethodParameters()[i];
            WebDataBinderFactory webDataBinderFactory = createWebDataBinderFactory(method.getHandlerMethod());
            result[i] = resolveArgument(webRequest, methodParameter, webDataBinderFactory);
        }
        return result;
    }

    private Object resolveArgument(NativeWebRequest webRequest, MethodParameter methodParameter, WebDataBinderFactory webDataBinderFactory) {
        List<HandlerMethodArgumentResolver> argumentResolvers = this.requestMappingHandlerAdapter.getArgumentResolvers();
        return argumentResolvers.stream()
                .filter(resolver -> resolver.supportsParameter(methodParameter))
                .map(resolver -> {
                    try {
                        return resolver.resolveArgument(methodParameter, new ModelAndViewContainer(), webRequest, webDataBinderFactory);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .findFirst()
                .orElse(null);
    }

    @SneakyThrows
    private WebDataBinderFactory createWebDataBinderFactory(HandlerMethod handlerMethod) {
        return (WebDataBinderFactory) MethodUtils.invokeMethod(this.requestMappingHandlerAdapter, true, "getDataBinderFactory", handlerMethod);
    }
}
