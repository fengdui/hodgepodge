package com.fengdui.wheel.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Enumeration;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;


/**
 * 使用commons-httpclient包
 * Http 请求工具
 * 太旧了
 */
@Slf4j
public class HttpTools {

	public static final okhttp3.MediaType JSONTYPE = okhttp3.MediaType.parse("application/json; charset=utf-8");

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
	@Data
	public static class RouteReq implements Serializable {
		private static final long serialVersionUID = 7536493182183690521L;

		@NotNull(message = "业务body参数不能为空")
		private String body;
	}

	@PostMapping("/route")
	public String doRoute(@Valid @RequestBody RouteReq routeReq, HttpServletRequest req) throws Exception {

		String apiUrl = req.getHeader("apiUrl");
		if(StringUtils.isBlank(apiUrl)){
			throw new RuntimeException("apiUrl不能为空!");
		}
		//校验body数据的合法性  TODO
		return this.forwardPost(req,apiUrl,routeReq.getBody());
	}

	/** @param req
     * @param forwardUrl
     * @param paramJsonStr
     * @return
	 **/
	private String forwardPost(HttpServletRequest req,String forwardUrl,String paramJsonStr) throws Exception {

		okhttp3.RequestBody paramBody = okhttp3.RequestBody.create(JSONTYPE, paramJsonStr);
		OkHttpClient okHttpClient = new OkHttpClient();
		Request.Builder builder = new Request.Builder();
		builder.url(forwardUrl);
		builder.post(paramBody);
		Enumeration e = req.getHeaderNames();
		while(e.hasMoreElements()){
			String name = (String) e.nextElement();
			String value = req.getHeader(name);
			System.out.println(name+" = "+value);
			builder.addHeader(name, value);

		}
		Request request = builder.build();
		log.info("【通道接口】forwardUrl:" + forwardUrl + "; header:" + JSON.toJSONString(request.headers()) + "; dto:" + paramJsonStr);
		Response response = okHttpClient.newCall(request).execute();
		if (response.isSuccessful()) {
			return response.body().string();
		} else {
			throw new IOException("Unexpected code " + response);
		}
	}
}
