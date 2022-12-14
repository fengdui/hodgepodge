/**
 * Copyright (C), 2018-2019, 微脉
 * FileName: IdUtils
 * Author:   xuanhusuo
 * Date:     2019/1/10 0010 17:45
 * Description:
 */
package com.hodgepodge.framework.id;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ResourceBundle;

public class SnowflakeIdUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnowflakeIdUtil.class);
    /**
     * zookeeper地址
     * 可修改(不设置成常量)
     */
    public static String zkAddress;

    public synchronized static long nextId() {
        // 实例没有做成常量，防止zookeeper地址动态变更
        return SimpleIdWorkerFactory.getInstance(getZkAddress()).nextId();
    }

    /**
     * 获取zookeeper地址
     *
     * @return
     */
    private static String getZkAddress() {
        // 1、默认从变量获取
        if (null != zkAddress) {
            LOGGER.info("【snowflake】IdUtil静态变量设置的zk地址={}", zkAddress);
            return zkAddress;
        }
        // 2、从阿里的dubbo配置类获取
        zkAddress = getZkAddressFromBean("org.apache.dubbo.config.RegistryConfig", "address");
        if (null != zkAddress) {
            LOGGER.info("【snowflake】apache dubbo获取zk地址={}", zkAddress);
            return zkAddress;
        }
        // 3、从apache的dubbo配置类获取
        zkAddress = getZkAddressFromBean("com.alibaba.dubbo.config.RegistryConfig", "address");
        if (null != zkAddress) {
            LOGGER.info("【snowflake】alibaba dubbo获取zk地址={}", zkAddress);
            return zkAddress;
        }

        // 4、根据环境从配置文件获取（目前针对中台配置默认ip和端口，若变更则不可用）
        ResourceBundle resourceBundle = ResourceBundle.getBundle("snowflake_zookeeper_address");
        String env = System.getProperty("env");
        if (null == env) {
            LOGGER.warn("【snowflake】未配置env参数");
            return zkAddress;
        }
        zkAddress = resourceBundle.getString(env.toLowerCase() + ".zkAddress");
        if (null != zkAddress) {
            LOGGER.info("【snowflake】当前env={}，获取到默认zk地址={}", env, zkAddress);
            return zkAddress;
        }
        LOGGER.warn("【snowflake】当前env={}，未获取到默认zk地址", env);
        return zkAddress;
    }

    /**
     * 从dubbo配置对象中获取zookeeper地址
     *
     * @return
     */
    private static String getZkAddressFromBean(String classPath, String fieldName) {
        Class clazz = null;
        try {
            clazz = Class.forName(classPath);
        } catch (ClassNotFoundException var4) {
            LOGGER.warn("【snowflake】找不到{}", classPath);
        }
        if (null == clazz) {
            return null;
        }
        try {
            Object obj = FactoryPostProcessor.getBean(clazz);
            if (null != obj) {
                Object value = getValueByKey(obj, fieldName);
                if (null != value) {
                    return value.toString();
                }
            }
        } catch (Exception e) {
            LOGGER.warn("【snowflake】非spring服务，不能从容器获取bean");
        }
        return null;
    }

    /**
     * 单个对象的某个键的值
     *
     * @param obj 对象
     * @param key 键
     * @return Object 键在对象中所对应得值 没有查到时返回null
     */
    public static Object getValueByKey(Object obj, String key) {
        // 得到类对象
        Class userCla = obj.getClass();
        // 得到类中的所有属性集合
        Field[] fs = userCla.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            Field f = fs[i];
            // 设置些属性是可以访问的
            f.setAccessible(true);
            try {

                if (f.getName().endsWith(key)) {
                    LOGGER.warn("【snowflake】单个对象的某个键的值==反射==" + f.get(obj));
                    return f.get(obj);
                }
            } catch (IllegalArgumentException e) {
                LOGGER.warn("【snowflake】IllegalArgumentException", e);
            } catch (IllegalAccessException e) {
                LOGGER.warn("【snowflake】IllegalAccessException", e);
            }
        }
        return null;
    }

}
