package com.fengdui.tool;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.*;
import java.util.*;

/**
 * Created by fd on 2016/7/7.
 */
public class WordPressToCSDN {


    public static String getToken() {
        HttpGet get = new HttpGet("http://api.csdn.net/blog/getinfo?access_token=696803acfb924d7a80f972b767f63c56");
        Map<String, String> map = new HashMap<>();
        map.put("access_token", "696803acfb924d7a80f972b767f63c56");
        try {
//            String json = objectMapper.writeValueAsString(map);
//            System.out.println(json);
//            StringEntity entity = new StringEntity(json, "UTF-8");
//            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
//            post.setEntity(entity);

            HttpResponse response = HttpClients.createDefault().execute(get);
            String ans = EntityUtils.toString(response.getEntity());
            System.out.println(ans);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void comit(String title, String content, String type) {

//        try {
//            title = URLEncoder.encode(title, "UTF-8");
//            content = URLEncoder.encode(content, "UTF-8");
//            type = URLEncoder.encode(type, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        String url = "http://api.csdn.net/blog/savearticle?access_token=XXX&title="+title+"&content=fd&categories="+type;
        System.out.println(url);
        HttpPost post = new HttpPost("http://api.csdn.net/blog/savearticle");
        Map<String, String> map = new HashMap<>();
        List<NameValuePair> list = new ArrayList<>();

        map.put("access_token", "");
        map.put("title", title);
        map.put("content", content);
        map.put("categories", type);
        Set<String> keySet = map.keySet();
        for(String key : keySet) {
            list.add(new BasicNameValuePair(key, map.get(key)));
        }

        try {
//            String json = objectMapper.writeValueAsString(map);
//            System.out.println(json);
//            StringEntity entity = new StringEntity(json, "UTF-8");
//            entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json;charset=UTF-8"));
            post.setEntity(new UrlEncodedFormEntity(list, HTTP.UTF_8));

            HttpResponse response = HttpClients.createDefault().execute(post);
            String ans = EntityUtils.toString(response.getEntity());
            System.out.println(ans);
        }  catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

//        getToken();


        int count = 0;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "";
            String userName = "";
            String password = "";
            Connection connection = DriverManager.getConnection(url, userName, password);
            String cmd = "select * from wp_posts where post_status = 'publish' and id > 41 && id < 100 order by post_date_gmt";
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery(cmd);

            String cmd2 = "select wp_terms.name as Ctype FROM wp_terms, wp_term_relationships, wp_term_taxonomy " +
                    "where wp_terms.term_id = wp_term_taxonomy.term_id " +
                    "and wp_term_taxonomy.term_taxonomy_id = wp_term_relationships.term_taxonomy_id " +
                    "and wp_term_relationships.object_id = ?";
            PreparedStatement statement1 = connection.prepareStatement(cmd2);
            while (set.next()) {
                String title = set.getString("post_title");
                String content = set.getString("post_content");
                long id = set.getLong("ID");
                statement1.setLong(1, id);
                ResultSet resultSet = statement1.executeQuery();
                resultSet.next();
                String type = resultSet.getString("Ctype");
                if (title == "" || content == "") {
//                    System.out.println("XXX");
                }
                else {
                    count++;
                    System.out.println(title+"   "+type);
                    comit(title, content, type);
//                    break;
                }
            }
            System.out.println("总共"+count);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}