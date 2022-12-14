package com.hodgepodge.framework.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherUtil {

    private static final Logger logger = LoggerFactory.getLogger(WeatherUtil.class);

    public static final String HTTP_URL = "http://apis.baidu.com/heweather/weather/free";
    public static final String PARAM_CITY = "city";
    public static final String PARAM_CITYIP = "cityip";
    public static final String VAL_CITY_DEFAULT = "杭州";
    public static final String API_KEY = "";

    private static final String[] IP_HEADERS_TO_TRY = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR", "X-Real-IP"};

    /**
     * 获取远程客户端 IP
     */
    public static String getIPFromClient(HttpServletRequest request) {
        for (String header : IP_HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    /**
     * 获取天气信息
     */
    public static String getWeatherInfo(HttpServletRequest request) {
        BufferedReader reader = null;
        String result = null;
        StringBuffer sbf = new StringBuffer();
        String ip = getIPFromClient(request);
        String httpUrl = HTTP_URL + "?" + (ip.indexOf("0:0:0:0") >= 0 ? (PARAM_CITY + "=" + VAL_CITY_DEFAULT) : (PARAM_CITYIP + "=" + ip));
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("apikey", API_KEY);
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            logger.error("getWeatherInfo error : " + e);
        }
        return result;
    }

    public static String getWeatherObj(HttpServletRequest request) {
        return getWeatherInfo(request);
    }

}
