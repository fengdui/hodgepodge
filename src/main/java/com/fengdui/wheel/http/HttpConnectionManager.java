
package com.fengdui.wheel.http;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class HttpConnectionManager {

	private static RestTemplate template;

	static{
		int poolSize = 4;
		PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();
		connMgr.setMaxTotal(poolSize + 1);
		connMgr.setDefaultMaxPerRoute(poolSize);
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();

		template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		template.setMessageConverters(converters);
	}

	public static RestTemplate getRestTemplate(){
		return template;
	}
}