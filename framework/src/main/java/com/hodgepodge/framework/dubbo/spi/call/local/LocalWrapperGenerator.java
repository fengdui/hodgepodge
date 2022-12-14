package com.hodgepodge.framework.dubbo.spi.call.local;


import com.hodgepodge.framework.dubbo.spi.annotation.Extension;
import com.hodgepodge.framework.dubbo.spi.call.WrapperGenerator;
import com.hodgepodge.framework.dubbo.spi.common.SpiException;
import com.hodgepodge.framework.dubbo.spi.common.SpiTypeEnum;
import com.hodgepodge.framework.dubbo.spi.common.SpiConfigDTO;
import com.hodgepodge.framework.dubbo.spi.utils.ApplicationContextHolder;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;


public class LocalWrapperGenerator implements WrapperGenerator {

    @Override
    public void preCheck() {
        if (ApplicationContextHolder.getApplicationContext() == null) {
            throw new SpiException("spring容器未初始化");
        }
    }

    @Override
    public boolean support(SpiConfigDTO configDTO) {
        return SpiTypeEnum.LOCAL.toString().equalsIgnoreCase(configDTO.getInvokeMethod());
    }

    /**
     * local实现可能有多个实现 所以使用bizCode+interface方式获取保证唯一
     * @param configDTO
     * @return
     */
    @Override
    public Object genericWrapper(SpiConfigDTO configDTO) {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        try {
            String[] names = applicationContext.getBeanNamesForType(ClassUtils.getClass(configDTO.getSpiInterface()));
            for(String name : names){
                Object bean = applicationContext.getBean(name);
                Extension extension;
                if((extension = AnnotationUtils.findAnnotation(AopUtils.getTargetClass(bean), Extension.class))!=null && extension.bizCode().equals(configDTO.getBizCode())){
                    return bean;
                }
            }
            throw new RuntimeException("spi配置有误,找不到本地接口实现");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("spi配置有误,加载不到接口类");
        }
    }

    @Override
    public void destroyWrapper(SpiConfigDTO spiConfigDTO) {

    }
}
