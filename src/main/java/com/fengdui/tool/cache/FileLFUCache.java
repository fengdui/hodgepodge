package com.fengdui.tool.cache;

import java.io.File;

public class FileLFUCache {

	/** LFU缓存 */
	protected final LFUCache<File, byte[]> cache;
	/** 容量 */
	protected final int capacity;
	/** 缓存的最大文件大小，文件大于此大小时将不被缓存 */
	protected final int maxFileSize;
	/** 已使用缓存空间 */
	protected int usedSize;

	/**
	 * 构造<br>
	 * 最大文件大小为缓存容量的一半<br>
	 * 默认无超时
	 * 
	 * @param capacity 缓存容量
	 */
	public FileLFUCache(int capacity) {
		this(capacity, capacity / 2, 0);
	}

	/**
	 * 构造<br>
	 * 默认无超时
	 * 
	 * @param capacity 缓存容量
	 * @param maxFileSize 最大文件大小
	 */
	public FileLFUCache(int capacity, int maxFileSize) {
		this(capacity, maxFileSize, 0);
	}

	/**
	 * 构造
	 * 
	 * @param capacity 缓存容量
	 * @param maxFileSize 文件最大大小
	 * @param timeout 默认超时时间，0表示无默认超时
	 */
	public FileLFUCache(int capacity, int maxFileSize, long timeout) {
		this.cache = new LFUCache<File, byte[]>(0, timeout) {
			@Override
			public boolean isFull() {
				return usedSize > this.capacity;
			}

			@Override
			protected void onRemove(File key, byte[] cachedObject) {
				usedSize -= cachedObject.length;
			}
		};
		this.capacity = capacity;
		this.maxFileSize = maxFileSize;
	}

	/** 缓存容量（byte数） */
	public int capacity() {
		return capacity;
	}

	/** 已使用空间大小（byte数） */
	public int getUsedSize() {
		return usedSize;
	}

	/** 允许被缓存文件的最大byte数 */
	public int maxFileSize() {
		return maxFileSize;
	}

	/** 缓存的文件数 */
	public int getCachedFilesCount() {
		return cache.size();
	}

	/** 超时时间 */
	public long timeout() {
		return cache.timeout;
	}

	/** 清空缓存 */
	public void clear() {
		cache.clear();
		usedSize = 0;
	}

}
