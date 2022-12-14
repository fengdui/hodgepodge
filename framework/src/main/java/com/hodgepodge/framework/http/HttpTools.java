package com.hodgepodge.framework.http;

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
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;


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
        if (StringUtils.isBlank(apiUrl)) {
            throw new RuntimeException("apiUrl不能为空!");
        }
        //校验body数据的合法性  TODO
        return this.forwardPost(req, apiUrl, routeReq.getBody());
    }

    /**
     * @param req
     * @param forwardUrl
     * @param paramJsonStr
     * @return
     **/
    private String forwardPost(HttpServletRequest req, String forwardUrl, String paramJsonStr) throws Exception {

        okhttp3.RequestBody paramBody = okhttp3.RequestBody.create(JSONTYPE, paramJsonStr);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder builder = new Request.Builder();
        builder.url(forwardUrl);
        builder.post(paramBody);
        Enumeration e = req.getHeaderNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            String value = req.getHeader(name);
            System.out.println(name + " = " + value);
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

    private static CloseableHttpClient httpClient = null;

    static {
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(50000)
                .setConnectTimeout(50000)
                .setConnectionRequestTimeout(50000).build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();
        connManager.setDefaultConnectionConfig(ConnectionConfig.custom().setCharset(Consts.UTF_8).build());
        connManager.setDefaultMaxPerRoute(10); // 设置最大路由
        connManager.setMaxTotal(50);          // 设置最大链接数

        httpClient = HttpClients.custom()
                .setConnectionManager(connManager)
                .setDefaultRequestConfig(requestConfig).build();
//        httpClient = HttpClients.createDefault(); // 底层默认使用 PoolingHttpClientConnectionManager
    }

    public static String postForm(String url, Map<String, String> params, String charsetName) throws IOException {
        return HttpTools.postForm(httpClient, url, params, charsetName);
    }

    public static String postForm(CloseableHttpClient httpClient, String url, Map<String, String> params, String charsetName) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            List<NameValuePair> formParams = new ArrayList<NameValuePair>();
            for (String name : params.keySet()) {
                formParams.add(new BasicNameValuePair(name, params.get(name)));
            }
            if (charsetName == null) {
                charsetName = "UTF-8";
            }
            Charset charset = Charset.forName(charsetName);
            HttpEntity reqEntity = new UrlEncodedFormEntity(formParams, charset);
            httpPost.setEntity(reqEntity);
            response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), charset);
        } catch (Exception e) {
            throw e;
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
    }

    public static String postJson(String url, String json) throws IOException {
        return HttpTools.postJson(httpClient, url, json);
    }

    public static String postJson(HttpPost httpPost, String json) throws IOException {
        CloseableHttpResponse response = null;
        try {
            StringEntity reqEntity = new StringEntity(json, Consts.UTF_8);
            reqEntity.setContentType("application/json; charset=UTF-8"); // 设置为 json 数据
            httpPost.setEntity(reqEntity);
            response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), Consts.UTF_8);
        } catch (Exception e) {
            throw e;
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
    }

    public static String postJson(CloseableHttpClient httpClient, String url, String json) throws IOException {
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            StringEntity reqEntity = new StringEntity(json, Consts.UTF_8);
            reqEntity.setContentType("application/json; charset=UTF-8"); // 设置为 json 数据
            httpPost.setEntity(reqEntity);

            response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity(), Consts.UTF_8);
        } catch (Exception e) {
            throw e;
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
    }


    public static String get(String url, String json) {
        return HttpTools.get(url, json);
    }

    public static String get(CloseableHttpClient httpClient, String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpGet);
            return EntityUtils.toString(response.getEntity(), Consts.UTF_8);
        } catch (Exception e) {
            throw e;
        } finally {
            HttpClientUtils.closeQuietly(response);
        }
    }
}
