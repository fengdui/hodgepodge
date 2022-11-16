package com.fengdui.framwork.dubbo.spi.bootstrap;

import com.fengdui.framwork.dubbo.spi.provider.ExtensionAnnotationPostProcessor;
import com.fengdui.framwork.dubbo.spi.utils.ApplicationContextHolder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

public class SpiProviderBootStrap implements BeanDefinitionRegistryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        registry.registerBeanDefinition("SpringContextHolder",new RootBeanDefinition(ApplicationContextHolder.class));
        RootBeanDefinition bd = new RootBeanDefinition(ExtensionAnnotationPostProcessor.class);
        registry.registerBeanDefinition("ExtensionAnnotationPostProcessor",bd);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
