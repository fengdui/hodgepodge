package com.fengdui.framwork.dubbo.spi.call.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.fengdui.framwork.dubbo.spi.call.WrapperGenerator;
import com.fengdui.framwork.dubbo.spi.common.SpiException;
import com.fengdui.framwork.dubbo.spi.common.SpiTypeEnum;
import com.fengdui.framwork.dubbo.spi.common.SpiConfigDTO;
import com.fengdui.framwork.dubbo.spi.utils.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class DubboWrapperGenerator implements WrapperGenerator {

    @Override
    public void preCheck() {
        if (ApplicationContextHolder.getApplicationContext() == null) {
            throw new SpiException("spring容器未初始化");
        }
    }

    @Override
    public boolean support(SpiConfigDTO configDTO) {
        return SpiTypeEnum.DUBBO.name().equalsIgnoreCase(configDTO.getInvokeMethod());
    }

    /**
     * 生成dubbo代理
     * @return the object
     * @throws RuntimeException
     */
    @Override
    public Object genericWrapper(SpiConfigDTO configDTO) {

        ReferenceConfig reference = buildReferenceConfig(configDTO);

        ReferenceConfigCache referenceConfigCache = ReferenceConfigCache.getCache();

        Object wrapper;

        try {
            wrapper = referenceConfigCache.get(reference);
            if (Objects.isNull(wrapper)) {
                referenceConfigCache.destroy(reference);
                log.error("生成dubbo代理失败");
            }
        } catch (NullPointerException ex) {
            log.error("生成dubbo代理失败", ex);
            referenceConfigCache.destroy(reference);
            throw new SpiException(ex.getMessage());
        }
        return wrapper;
    }

    @Override
    public void destroyWrapper( final SpiConfigDTO configDTO) {

        ReferenceConfig<GenericService> reference = buildReferenceConfig(configDTO);

        ReferenceConfigCache referenceConfigCache = ReferenceConfigCache.getCache();

        referenceConfigCache.destroy(reference);
    }


    private ReferenceConfig buildReferenceConfig(final SpiConfigDTO configDTO) {

        ReferenceConfig referenceConfig = new ReferenceConfig();

        //生成ApplicationConfig
        ApplicationConfig application = new ApplicationConfig(configDTO.getAppId());
        referenceConfig.setApplication(application);
        //获取spring容器 目前下面的配置是让使用方使用dubbo.xml配置一份 后期可以优化 不依赖spring 使用自己的配置文件构造后传入
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        if (applicationContext == null) {
            log.error("ApplicationContextHolder.getApplicationContext() 为空, 代理dubbo服务失败");
            throw new SpiException("ApplicationContextHolder.getApplicationContext() 为空, 代理dubbo服务失败");
        }
        //从spring容器获取ProviderConfig
        if (referenceConfig.getConsumer() == null) {
            Map<String, ConsumerConfig> consumerConfigMap = applicationContext == null ? null  : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ConsumerConfig.class, false, false);
            if (consumerConfigMap != null && consumerConfigMap.size() > 0) {
                ConsumerConfig consumerConfig = null;
                for (ConsumerConfig config : consumerConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        if (consumerConfig != null) {
                            throw new IllegalStateException("Duplicate consumer configs: " + consumerConfig + " and " + config);
                        }
                        consumerConfig = config;
                    }
                }
                if (consumerConfig != null) {
                    referenceConfig.setConsumer(consumerConfig);
                }
            }
        }
        if (referenceConfig.getApplication() == null
                && (referenceConfig.getConsumer() == null || referenceConfig.getConsumer().getApplication() == null)) {
            Map<String, ApplicationConfig> applicationConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, ApplicationConfig.class, false, false);
            if (applicationConfigMap != null && applicationConfigMap.size() > 0) {
                ApplicationConfig applicationConfig = null;
                for (ApplicationConfig config : applicationConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        if (applicationConfig != null) {
                            throw new IllegalStateException("Duplicate application configs: " + applicationConfig + " and " + config);
                        }
                        applicationConfig = config;
                    }
                }
                if (applicationConfig != null) {
                    referenceConfig.setApplication(applicationConfig);
                }
            }
        }
        //从spring容器获取注册中心
        if ((referenceConfig.getRegistries() == null || referenceConfig.getRegistries().size() == 0)
                && (referenceConfig.getConsumer() == null || referenceConfig.getConsumer().getRegistries() == null || referenceConfig.getConsumer().getRegistries().size() == 0)
                && (referenceConfig.getApplication() == null || referenceConfig.getApplication().getRegistries() == null || referenceConfig.getApplication().getRegistries().size() == 0)) {
            Map<String, RegistryConfig> registryConfigMap = applicationContext == null ? null : BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, RegistryConfig.class, false, false);
            if (registryConfigMap != null && registryConfigMap.size() > 0) {
                List<RegistryConfig> registryConfigs = new ArrayList<RegistryConfig>();
                for (RegistryConfig config : registryConfigMap.values()) {
                    if (config.isDefault() == null || config.isDefault().booleanValue()) {
                        registryConfigs.add(config);
                    }
                }
                if (registryConfigs != null && registryConfigs.size() > 0) {
                    referenceConfig.setRegistries(registryConfigs);
                }
            }
        }

        referenceConfig.setInterface(configDTO.getSpiInterface());

        referenceConfig.setGroup(configDTO.getBizCode());

//        referenceConfig.setCluster(SpiFailoverCluster.NAME);

        Optional.ofNullable(configDTO.getExpireTime()).ifPresent(referenceConfig::setTimeout);

        return referenceConfig;
    }
}
