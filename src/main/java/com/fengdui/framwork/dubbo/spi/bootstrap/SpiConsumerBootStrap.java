package com.fengdui.framwork.dubbo.spi.bootstrap;

import com.fengdui.framwork.dubbo.spi.common.SpiException;
import com.fengdui.framwork.dubbo.spi.common.SpiConfigDTO;
import com.fengdui.framwork.dubbo.spi.router.SpiRegistry;
import com.fengdui.framwork.dubbo.spi.utils.ApplicationContextHolder;
import lombok.Setter;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

import org.springframework.util.ClassUtils;

import java.util.List;

public class SpiConsumerBootStrap implements BeanDefinitionRegistryPostProcessor {

    @Setter
    private String appName;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        registry.registerBeanDefinition("SpringContextHolder",new RootBeanDefinition(ApplicationContextHolder.class));
        List<SpiConfigDTO> spiConfigDTOList = Lists.newArrayList();
        spiConfigDTOList.stream().map(SpiConfigDTO::getSpiInterface)
                .distinct()
                .forEach(i->{
                    Class clazz = null;
                    try {
                        clazz = ClassUtils.forName(i,Thread.currentThread().getContextClassLoader());
                    } catch (ClassNotFoundException e) {
                        throw new SpiException("获取spi接口失败");
                    }
                    RootBeanDefinition beanDefinition = new RootBeanDefinition(SpiFactoryBean.class);
                    ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
                    constructorArgumentValues.addIndexedArgumentValue(0,clazz);
                    beanDefinition.setConstructorArgumentValues(constructorArgumentValues);
                    beanDefinition.setLazyInit(false);
                    registry.registerBeanDefinition(clazz.getSimpleName(),beanDefinition);
                });
        spiConfigDTOList.stream().forEach(dto -> {
            SpiRegistry.register(dto);
        });

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
