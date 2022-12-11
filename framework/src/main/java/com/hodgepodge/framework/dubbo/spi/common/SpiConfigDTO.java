package main.java.com.hodgepodge.framework.dubbo.spi.common;

import lombok.Data;
import org.springframework.util.StringUtils;


@Data
public class SpiConfigDTO {
    private static final String DEFAULT_SPILITOR="_";
    /**
     * spi接口
     */
    private String spiInterface;
    /**
     * 业务code
     */
    private String bizCode;
    /**
     * 调用方式
     * 目前支持 本地、dubbo
     */
    private String invokeMethod;
    /**
     * 应用id
     */
    private String appId;

    /**
     * 超时事件 (ms)
     */
    private Integer expireTime;

    private String comment;

    public static SpiConfigDTO buildConfigDTO(String key,String value,String appId){
        SpiConfigDTO dto=new SpiConfigDTO();
        if(StringUtils.isEmpty(key)||StringUtils.isEmpty(value)){
            return null;
        }
        String[] keySplits=key.split(DEFAULT_SPILITOR);
        String[] valueSplits=value.split(DEFAULT_SPILITOR);
        if (keySplits.length!=2||valueSplits.length!=2){
            return null;
        }
        dto.setAppId(appId);
        dto.setBizCode(keySplits[0]);
        dto.setSpiInterface(keySplits[1]);
        dto.setInvokeMethod(valueSplits[0]);
        dto.setExpireTime(Integer.valueOf(valueSplits[1]));
        return dto;
    }

    /**
     * 根据参数构建key
     *
     * @param
     * @return key格式： bizcode_spiInterface
     */
    public String buildKey() {
        return this.bizCode + DEFAULT_SPILITOR + this.spiInterface;
    }

    /**
     * 根据参数构建value
     *
     * @param
     * @return value 格式：invokeMethod_expireTime
     */
    public String buildValue() {
        return this.invokeMethod + DEFAULT_SPILITOR + this.expireTime;
    }
}
