package com.fengdui.wheel.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;


/**
 * 使用commons-httpclient包
 * Http 请求工具
 * 太旧了
 */
public class HttpTools {


	/**
	 * @param uri
	 * @param sessionId
	 * @param parts
	 * @return
	 * @throws IOException
	 */
	public static String post(String uri, String sessionId, Part[] parts)
			throws IOException {
		if (StringUtils.isBlank(uri))
			throw new RuntimeException("uri is null.");
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(uri);
		BufferedReader br = null;
		StringBuffer rs = new StringBuffer();
		try {
			method.setRequestEntity(new MultipartRequestEntity(parts, method
					.getParams()));
			if (StringUtils.isNotBlank(sessionId)) {
				method.setRequestHeader("Cookie", "JSESSIONID=" + sessionId);
			}
			client.executeMethod(method);
			if (method.getStatusCode() == HttpStatus.SC_OK) {
				br = new BufferedReader(new InputStreamReader(
						method.getResponseBodyAsStream(),
						"UTF-8"));
				String line;
				while ((line = br.readLine()) != null) {
					rs.append(line);
				}
				return rs.toString();
			} else {
				Header locationHeader = method.getResponseHeader("location");
				String location = null;
				if (locationHeader != null) {
					location = locationHeader.getValue();
					System.out
							.println("The page was redirected to:" + location);
				} else {
					System.err.println("Location field value is null.");
				}
				return method.getStatusText();
			}
		} catch (HttpException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally {
			method.releaseConnection();
			IOUtils.closeQuietly(br);
		}
	}
}
