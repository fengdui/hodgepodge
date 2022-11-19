package com.fengdui.framwork.dubbo.spi;

public class BusinessContext {
    private static ThreadLocal<String> bizCode = new ThreadLocal<>();
    private static String globalBizCode;

    public static void setBizCode(String bizCode) {
        BusinessContext.bizCode.set(bizCode);
    }

    public static String getBizCode() {
        if (BusinessContext.bizCode.get() == null || BusinessContext.bizCode.get().isEmpty()) {
            return BusinessContext.globalBizCode;
        } else {
            return BusinessContext.bizCode.get();
        }
    }

    public static void setGlobalBizCode(String bizCode) {
        BusinessContext.globalBizCode = bizCode;
    }
}
