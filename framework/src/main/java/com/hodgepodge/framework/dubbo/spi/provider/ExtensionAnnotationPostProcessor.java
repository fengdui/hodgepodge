package com.hodgepodge.framework.dubbo.spi.provider;

import com.hodgepodge.framework.dubbo.spi.annotation.Extension;
import com.hodgepodge.framework.dubbo.spi.call.WrapperGeneratorComposite;
import com.hodgepodge.framework.dubbo.spi.common.SpiTypeEnum;
import com.hodgepodge.framework.dubbo.spi.provider.dubbo.DubboServiceExporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;

@Slf4j
public class ExtensionAnnotationPostProcessor implements BeanPostProcessor, Ordered {

    private static final boolean DUBBO_PRESENT = ClassUtils.isPresent("com.alibaba.dubbo.common.URL", WrapperGeneratorComposite.class.getClassLoader());

    private DubboServiceExporter dubboServiceExporter;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {


        if (AopUtils.getTargetClass(bean).isAnnotationPresent(Extension.class)) {
            Extension extension = AopUtils.getTargetClass(bean).getAnnotation(Extension.class);
            if (SpiTypeEnum.DUBBO.equals(extension.invokeMethod())) {
                dubboServiceExporter.exportService(bean);
            }
        }

        return bean;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @PostConstruct
    public void init() {
        if (DUBBO_PRESENT) {
            dubboServiceExporter = new DubboServiceExporter();
        } else {
            log.info("spi框架支持invokeMethod为dubbo的方式, 但是未加入dubbo依赖包");
        }
    }
}
