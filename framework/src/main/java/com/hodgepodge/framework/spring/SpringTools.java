package com.hodgepodge.framework.spring;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class SpringTools {

    private static boolean romePresent =
            ClassUtils.isPresent("com.rometools.rome.feed.WireFeed",
                    RestTemplate.class.getClassLoader());
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 获取spring容器中带某个注解的类
     */
    public void getBean() {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(Reference.class);
    }


}
