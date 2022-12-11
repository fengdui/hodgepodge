package main.java.com.hodgepodge.framework.dubbo.spi.call;

import main.java.com.hodgepodge.framework.dubbo.spi.common.SpiConfigDTO;

public interface WrapperGenerator {

    void preCheck();

    boolean support(SpiConfigDTO configDTO);
    /**
     * 根据协议生成包装类
     * @param configDTO
     * @return
     */
    Object genericWrapper(SpiConfigDTO configDTO);

    void destroyWrapper(SpiConfigDTO spiConfigDTO);
}
