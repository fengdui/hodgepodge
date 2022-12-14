package com.hodgepodge.framework.cache;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class LRUCache<K, V> extends AbstractCache<K, V> {

    /**
     * 构造<br>
     * 默认无超时
     *
     * @param capacity 容量
     */
    public LRUCache(int capacity) {
        this(capacity, 0);
    }

    /**
     * 构造
     *
     * @param capacity 容量
     * @param timeout  默认超时时间
     */
    public LRUCache(int capacity, long timeout) {
        this.capacity = capacity;
        this.timeout = timeout;

        // 链表key按照访问顺序排序，调用get方法后，会将这次访问的元素移至头部
        cacheMap = new LinkedHashMap<K, CacheObj<K, V>>(capacity + 1, 1.0f, true) {
            private static final long serialVersionUID = -1806954614512571136L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheObj<K, V>> eldest) {
                if (LRUCache.this.capacity == 0) {
                    return false;
                }
                //当链表元素大于容量时，移除最老（最久未被使用）的元素
                return size() > LRUCache.this.capacity;
            }
        };
    }

    /**
     * 只清理超时对象，LRU的实现会交给<code>LinkedHashMap</code>
     */
    @Override
    protected int pruneCache() {
        if (isPruneExpiredActive() == false) {
            return 0;
        }
        int count = 0;
        Iterator<CacheObj<K, V>> values = cacheMap.values().iterator();
        CacheObj<K, V> co;
        while (values.hasNext()) {
            co = values.next();
            if (co.isExpired()) {
                values.remove();
                count++;
            }
        }
        return count;
    }

}
