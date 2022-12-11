package main.java.com.hodgepodge.framework.dubbo.spi.router;

import main.java.com.hodgepodge.framework.dubbo.spi.BusinessContext;
import main.java.com.hodgepodge.framework.dubbo.spi.annotation.Extension;

import java.lang.reflect.Method;
import java.util.Optional;

public class SpiRouter {


    private static SpiRegistry spiRegistry = SpiRegistry.getInstance();

    public static Object route(String spi, Object proxy, Method method, Object[] args){
        Object impl = Optional.ofNullable(SpiContainer.get(spi, BusinessContext.getBizCode())).orElse(SpiContainer.get(spi, Extension.DEFAULT_BIZ_CODE));
        if (impl == null) {
            throw new RuntimeException("找不到spi实现类");
        }
        try {
            return method.invoke(impl, args);
        } catch (Exception e) {
            throw new RuntimeException("执行spi实现失败",e);
        }
    }
}
