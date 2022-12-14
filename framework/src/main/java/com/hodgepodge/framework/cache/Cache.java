package com.hodgepodge.framework.cache;

import java.util.Iterator;

public interface Cache<K, V> {

    /**
     * 返回缓存容量，<code>0</code>表示无大小限制
     */
    int capacity();

    /**
     * 缓存失效时长， <code>0</code> 表示没有设置
     */
    long timeout();

    /**
     * 将对象加入到缓存，使用默认失效时长
     */
    void put(K key, V object);

    /**
     * 将对象加入到缓存，使用指定失效时长<br>
     * 如果缓存空间满了，{@link #prune()} 将被调用以获得空间来存放新对象
     */
    void put(K key, V object, long timeout);

    /**
     * 从缓存中获得对象，当对象不在缓存中或已经过期返回<code>null</code>
     */
    V get(K key);

    /**
     * 返回缓存迭代器
     */
    Iterator<V> iterator();

    /**
     * 从缓存中清理过期对象，清理策略取决于具体实现
     *
     * @return 清理的缓存对象个数
     */
    int prune();

    /**
     * 缓存是否已满，仅用于有空间限制的缓存对象
     */
    boolean isFull();

    /**
     * 从缓存中移除对象
     */
    void remove(K key);

    /**
     * 清空缓存
     */
    void clear();

    /**
     * 缓存的对象数量
     */
    int size();

    /**
     * 缓存是否为空
     */
    boolean isEmpty();

}
