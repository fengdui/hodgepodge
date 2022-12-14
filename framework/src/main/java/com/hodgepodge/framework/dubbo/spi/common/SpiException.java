package com.hodgepodge.framework.dubbo.spi.common;

public class SpiException extends RuntimeException {
    public SpiException(String message) {
        super(message);
    }

    public static SpiException fail(String msg){
        return new SpiException(msg);
    }
}
