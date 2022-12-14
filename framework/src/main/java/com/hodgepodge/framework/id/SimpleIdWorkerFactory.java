package com.hodgepodge.framework.id;

public class SimpleIdWorkerFactory {
    private static long workId;

    private static class LazyHolder {
        private static final IdWorker INSTANCE = new SnowflakeIdWorker(workId);
    }

    private SimpleIdWorkerFactory() {
    }

    public static final IdWorker getInstance(String zkAddress) {
        if (EmptyUtils.isBlank(zkAddress)) {
            throw new RuntimeException("【snowflake】zookeeper地址不能为空");
        }
        zkAddress = zkAddress.replaceAll("\\s*", "").replace("zookeeper://", "")
                .replace("?backup=", ",");
        SimpleIdWorkerFactory.workId = WorkIdBuilder.getWorkerId(zkAddress);
        return LazyHolder.INSTANCE;
    }
}