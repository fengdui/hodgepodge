package com.hodgepodge.framework.cache;

import java.util.Iterator;

public class CacheValuesIterator<V> implements Iterator<V> {

    private Iterator<? extends CacheObj<?, V>> iterator;
    private CacheObj<?, V> nextValue;

    CacheValuesIterator(AbstractCache<?, V> abstractCacheMap) {
        iterator = abstractCacheMap.cacheMap.values().iterator();
        nextValue();
    }

    /**
     * 下一个值，当不存在则下一个值为null
     */
    private void nextValue() {
        while (iterator.hasNext()) {
            nextValue = iterator.next();
            if (nextValue.isExpired() == false) {
                return;
            }
        }
        nextValue = null;
    }

    /**
     * 是否有下一个值
     */
    @Override
    public boolean hasNext() {
        return nextValue != null;
    }

    /**
     * 下一个值
     */
    @Override
    public V next() {
        final V cachedObject = nextValue.obj;
        nextValue();
        return cachedObject;
    }

    /**
     * 从缓存中移除没有过期的当前值
     */
    @Override
    public void remove() {
        iterator.remove();
    }

}
