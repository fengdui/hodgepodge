package com.hodgepodge.framework.dubbo.spi.annotation;

import com.hodgepodge.framework.dubbo.spi.common.SpiTypeEnum;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Extension {
    /** 默认实现bizCode **/
    String DEFAULT_BIZ_CODE = "$$$";

    String bizCode() default DEFAULT_BIZ_CODE;

    /**
     * 调用方式
     * 目前支持 本地、dubbo
     */
    SpiTypeEnum invokeMethod() default SpiTypeEnum.LOCAL;
    
}
