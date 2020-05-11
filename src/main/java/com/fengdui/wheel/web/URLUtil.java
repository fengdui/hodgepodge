package com.fengdui.wheel.web;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringUtils;


public class URLUtil {

	/**
	 * 创建URL对象
	 * 
	 * @param url URL
	 * @return URL对象
	 */
	public static URL url(String url) throws MalformedURLException {
		return new URL(url);
	}

	/**
	 * 获得URL
	 * 
	 * @param pathBaseClassLoader 相对路径（相对于classes）
	 * @return URL
	 */
	public static URL getURL(String pathBaseClassLoader) {
		return Thread.currentThread().getContextClassLoader().getResource(pathBaseClassLoader);
	}

	/**
	 * 获得URL
	 * 
	 * @param path 相对给定 class所在的路径
	 * @param clazz 指定class
	 * @return URL
	 */
	public static URL getURL(String path, Class<?> clazz) {
		return clazz.getResource(path);
	}

	/**
	 * 获得URL，常用于使用绝对路径时的情况
	 * 
	 * @param configFile URL对应的文件对象
	 * @return URL
	 */
	public static URL getURL(File configFile) throws MalformedURLException {
		return configFile.toURI().toURL();
	}

	/**
	 * 格式化URL链接
	 * 
	 * @param url 需要格式化的URL
	 * @return 格式化后的URL，如果提供了null或者空串，返回null
	 */
	public static String formatUrl(String url) {
		if (StringUtils.isBlank(url)) {
			return null;
		}
		if (url.startsWith("http://") || url.startsWith("https://")) {
			return url;
		}
		return "http://" + url;
	}

	/**
	 * 补全相对路径
	 * 
	 * @param baseUrl 基准URL
	 * @param relativePath 相对URL
	 * @return 相对路径
	 */
	public static String complateUrl(String baseUrl, String relativePath) throws MalformedURLException {
		baseUrl = formatUrl(baseUrl);
		if (StringUtils.isBlank(baseUrl)) {
			return null;
		}

		final URL absoluteUrl = new URL(baseUrl);
		final URL parseUrl = new URL(absoluteUrl, relativePath);
		return parseUrl.toString();
	}

	/**
	 * 编码URL
	 * 
	 * @param url URL
	 * @param charset 编码
	 * @return 编码后的URL
	 */
	public static String encode(String url, String charset) throws UnsupportedEncodingException {
		return URLEncoder.encode(url, charset);
	}

	/**
	 * 解码URL
	 * 
	 * @param url URL
	 * @param charset 编码
	 * @return 解码后的URL
	 */
	public static String decode(String url, String charset) throws UnsupportedEncodingException {
		return URLDecoder.decode(url, charset);
	}

	/**
	 * 获得path部分<br>
	 * URI -> http://www.aaa.bbb/search?scope=ccc&q=ddd PATH -> /search
	 * 
	 * @param uriStr URI路径
	 * @return path
	 */
	public static String getPath(String uriStr) throws URISyntaxException {
		URI uri = new URI(uriStr);
		return uri == null ? null : uri.getPath();
	}

	public static void main(String[] args) throws URISyntaxException {
		System.out.println(getPath("http://www.oschina.net/search?scope=blog&q=netty"));
	}

}
