
package com.fengdui.wheel.http;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HttpConnectionManager {
	private static HttpClient httpClient;

	private HttpConnectionManager() {

	}

	/** 适合多线程的HttpClient,用httpClient4.2.1实现 */
	public static HttpClient getHttpClient() {
		if (null == httpClient) {
			// 设置组件参数, HTTP协议的版本,1.1/1.0/0.9
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, "utf-8");
			HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
			HttpProtocolParams.setUseExpectContinue(params, true);

			// 设置连接超时时间
			int REQUEST_TIMEOUT = 30 * 1000; // 设置请求超时30秒钟
			int SO_TIMEOUT = 60 * 1000; // 设置等待数据超时时间60秒钟
			params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
					REQUEST_TIMEOUT);
			params.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);

			// 设置访问协议
			SchemeRegistry schreg = new SchemeRegistry();
			schreg.register(new Scheme("http", 80, PlainSocketFactory
					.getSocketFactory()));
			schreg.register(new Scheme("https", 443, SSLSocketFactory
					.getSocketFactory()));

			// 多连接的线程安全的管理器
			PoolingClientConnectionManager pccm = new PoolingClientConnectionManager(
					schreg);
			pccm.setDefaultMaxPerRoute(20);// 每个主机的最大并行链接数
			pccm.setMaxTotal(100);// 客户端总并行链接最大数

			httpClient = new DefaultHttpClient(pccm, params);
		}
		return httpClient;
	}
		private static RestTemplate template;

		static{
			int poolSize = 4;
			PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();
			connMgr.setMaxTotal(poolSize + 1);
			connMgr.setDefaultMaxPerRoute(poolSize);
			CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connMgr).build();

			template = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
			List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
//			FastJsonHttpMessageConverter fastjson = new FastJsonHttpMessageConverter();
//
//			FastJsonConfig config = new FastJsonConfig();
//			config.setSerializerFeatures(SerializerFeature.WriteClassName, SerializerFeature.BrowserCompatible, SerializerFeature.DisableCircularReferenceDetect);
//			fastjson.setFastJsonConfig(config);
//
//			fastjson.setFeatures(SerializerFeature.WriteClassName, SerializerFeature.BrowserCompatible, SerializerFeature.DisableCircularReferenceDetect);
//			fastjson.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
//			ParserConfig.getGlobalInstance().addAccept("cn.com.bsfit.");
//			converters.add(fastjson);
			template.setMessageConverters(converters);
		}

		public static RestTemplate getRestTemplate(){
			return template;
		}
}