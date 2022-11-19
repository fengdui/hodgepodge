package com.fengdui.framework.cache;

import java.util.HashMap;
import java.util.Iterator;

public class LFUCache<K, V> extends AbstractCache<K, V> {

    /**
     * 构造
     *
     * @param capacity 容量
     */
    public LFUCache(int capacity) {
        this(capacity, 0);
    }

    /**
     * 构造
     *
     * @param capacity 容量
     * @param timeout  过期时长
     */
    public LFUCache(int capacity, long timeout) {
        this.capacity = capacity;
        this.timeout = timeout;
        cacheMap = new HashMap<K, CacheObj<K, V>>(capacity + 1);
    }

    /**
     * 清理过期对象。<br>
     * 清理后依旧满的情况下清除最少访问（访问计数最小）的对象并将其他对象的访问数减去这个最小访问数，以便新对象进入后可以公平计数。
     *
     * @return 清理个数
     */
    @Override
    protected int pruneCache() {
        int count = 0;
        CacheObj<K, V> comin = null;

        // 清理过期对象并找出访问最少的对象
        Iterator<CacheObj<K, V>> values = cacheMap.values().iterator();
        CacheObj<K, V> co;
        while (values.hasNext()) {
            co = values.next();
            if (co.isExpired() == true) {
                values.remove();
                onRemove(co.key, co.obj);
                count++;
                continue;
            }

            // 找出访问最少的对象
            if (comin == null || co.accessCount < comin.accessCount) {
                comin = co;
            }
        }

        // 减少所有对象访问量，并清除减少后为0的访问对象
        if (isFull() && comin != null) {
            long minAccessCount = comin.accessCount;

            values = cacheMap.values().iterator();
            CacheObj<K, V> co1;
            while (values.hasNext()) {
                co1 = values.next();
                co1.accessCount -= minAccessCount;
                if (co1.accessCount <= 0) {
                    values.remove();
                    onRemove(co1.key, co1.obj);
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * 对象移除回调。默认无动作
     *
     * @param key          键
     * @param cachedObject 被缓存的对象
     */
    protected void onRemove(K key, V cachedObject) {

    }

}
