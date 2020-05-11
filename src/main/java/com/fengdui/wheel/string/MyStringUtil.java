package com.fengdui.wheel.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class MyStringUtil {

    public static String clearHTMLTag(String inputString, int stringLenth) {
        if (inputString == null) {
            return null;
        }

        Pattern p_script;
        Matcher m_script;
        Pattern p_style;
        Matcher m_style;
        Pattern p_html;
        Matcher m_html;
        try {
            //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?/[\\s]*?script[\\s]*?>";
            //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?/[\\s]*?style[\\s]*?>";
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(inputString);
            inputString = m_script.replaceAll(""); // 过滤script标签
            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(inputString);
            inputString = m_style.replaceAll(""); // 过滤style标签
            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(inputString);
            inputString = m_html.replaceAll(""); // 过滤html标签
        } catch (Exception e) {
        }
        if (inputString.length() > stringLenth) {
            return inputString.substring(0, stringLenth);// 返回文本字符串
        } else {
            return inputString;// 返回文本字符串
        }
    }

}
