package com.hodgepodge.framework.dubbo.spi.router;

import com.hodgepodge.framework.dubbo.spi.call.WrapperGeneratorComposite;
import com.hodgepodge.framework.dubbo.spi.common.SpiConfigDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class SpiRegistry {

    @Getter
    private final static SpiRegistry instance = new SpiRegistry();

    private WrapperGeneratorComposite composite = new WrapperGeneratorComposite();

    private Set<String> initedSpi = new HashSet<>();

    /**
     *
     * 根据admin 配置中心的应该配置 建立一个应用放到SpiContainer中
     * @param configDTO
     */
    public static void register(SpiConfigDTO configDTO) {
        if (Objects.isNull(configDTO)) {
            return;
        }
        Object wrapper = instance.composite.genericWrapper(configDTO);
        SpiContainer.put(configDTO.getSpiInterface(), configDTO.getBizCode(), wrapper);
    }

    /**
     * 销毁一个扩展点引用
     * @param configDTO
     */
    public static void unRegister(SpiConfigDTO configDTO) {
        if (Objects.isNull(configDTO)) {
            return;
        }
        instance.composite.destroyWrapper(configDTO);
        SpiContainer.remove(configDTO.getSpiInterface(), configDTO.getBizCode());
    }
}
