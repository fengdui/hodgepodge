package com.hodgepodge.framework.dubbo.spi.router;

import com.hodgepodge.framework.dubbo.spi.annotation.Extension;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpiContainer extends ConcurrentHashMap<String, Map<String, Object>>{
    private final static SpiContainer instance = new SpiContainer();

    public static Object get(String spiInterface, String bizCode) {
        Map<String, Object> impls = instance.get(spiInterface);
        if (impls == null) {
            return null;
        }
        return Optional.ofNullable(impls.get(bizCode)).orElse(impls.get(Extension.DEFAULT_BIZ_CODE));
    }

    /**
     * @param spiInterface
     * @param bizCode
     * @param wrapper
     */
    public static void put(String spiInterface, String bizCode, Object wrapper) {
        Map<String, Object> impls = instance.get(spiInterface);
        if (impls == null) {
            impls = instance.computeIfAbsent(spiInterface, key -> new ConcurrentHashMap<>());
        }
        impls.put(bizCode, wrapper);
    }

    /**
     * 删除一个bizCode对应的实现 但是不会删除第一层的map
     *
     * @param spiInterface
     * @param bizCode
     */
    public static void remove(String spiInterface, String bizCode) {
        Map<String, Object> impls = instance.get(spiInterface);
        if (impls == null) {
            return;
        }
        impls.remove(bizCode);
    }
}
