package com.hodgepodge.framework.cache;

public class CacheObj<K, V> {

    final K key;
    final V obj;

    /**
     * 上次访问时间
     */
    long lastAccess;
    /**
     * 访问次数
     */
    long accessCount;
    /**
     * 对象存活时长，0表示永久存活
     */
    long ttl;

    CacheObj(K key, V obj, long ttl) {
        this.key = key;
        this.obj = obj;
        this.ttl = ttl;
        this.lastAccess = System.currentTimeMillis();
    }

    /**
     * 是否过期
     */
    boolean isExpired() {
        return (ttl > 0) && (lastAccess + ttl < System.currentTimeMillis());
    }

    /**
     * 获得对象
     */
    V get() {
        lastAccess = System.currentTimeMillis();
        accessCount++;
        return obj;
    }

    @Override
    public String toString() {
        return "CacheObj [key=" + key + ", obj=" + obj + ", lastAccess=" + lastAccess + ", accessCount=" + accessCount + ", ttl=" + ttl + "]";
    }

}
