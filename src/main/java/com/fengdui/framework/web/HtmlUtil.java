package com.fengdui.framework.web;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtil {

    public static final String RE_HTML_MARK = "(<[^<]*?>)|(<[\\s]*?/[^<]*?>)|(<[^<]*?/[\\s]*?>)";
    public static final String RE_SCRIPT = "<[\\s]*?script[^>]*?>.*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";

    /**
     * 清除所有HTML标签
     *
     * @param content 文本
     * @return 清除标签后的文本
     */
    public static String cleanHtmlTag(String content) {
        return content.replaceAll(RE_HTML_MARK, "");
    }

    /**
     * 清除指定HTML标签和被标签包围的内容<br>
     * 不区分大小写
     *
     * @param content  文本
     * @param tagNames 要清除的标签
     * @return 去除标签后的文本
     */
    public static String removeHtmlTag(String content, String... tagNames) {
        return removeHtmlTag(content, true, tagNames);
    }

    /**
     * 清除指定HTML标签，不包括内容<br>
     * 不区分大小写
     *
     * @param content  文本
     * @param tagNames 要清除的标签
     * @return 去除标签后的文本
     */
    public static String unwrapHtmlTag(String content, String... tagNames) {
        return removeHtmlTag(content, false, tagNames);
    }

    /**
     * 清除指定HTML标签<br>
     * 不区分大小写
     *
     * @param content        文本
     * @param withTagContent 是否去掉被包含在标签中的内容
     * @param tagNames       要清除的标签
     * @return 去除标签后的文本
     */
    public static String removeHtmlTag(String content, boolean withTagContent, String... tagNames) {
        String regex1 = null;
        String regex2 = null;
        for (String tagName : tagNames) {
            if (StringUtils.isBlank(tagName)) {
                continue;
            }
            tagName = tagName.trim();
            // (?i)表示其后面的表达式忽略大小写
            regex1 = String.format("(?i)<{}\\s?[^>]*?/>", tagName);
            if (withTagContent) {
                // 标签及其包含内容
                regex2 = String.format("(?i)(?s)<{}\\s*?[^>]*?>.*?</{}>", tagName, tagName);
            } else {
                // 标签不包含内容
                regex2 = String.format("(?i)<{}\\s*?[^>]*?>|</{}>", tagName, tagName);
            }

            // 自闭标签小写 非自闭标签小写
            content = content.replaceAll(regex1, StringUtils.EMPTY).replaceAll(regex2, StringUtils.EMPTY);
        }
        return content;
    }

    /**
     * 去除HTML标签中的属性
     *
     * @param content 文本
     * @param attrs   属性名（不区分大小写）
     * @return 处理后的文本
     */
    public static String removeHtmlAttr(String content, String... attrs) {
        String regex = null;
        for (String attr : attrs) {
            regex = String.format("(?i)\\s*{}=([\"']).*?\\1", attr);
            content = content.replaceAll(regex, StringUtils.EMPTY);
        }
        return content;
    }

    /**
     * 去除指定标签的所有属性
     *
     * @param content  内容
     * @param tagNames 指定标签
     * @return 处理后的文本
     */
    public static String removeAllHtmlAttr(String content, String... tagNames) {
        String regex = null;
        for (String tagName : tagNames) {
            regex = String.format("(?i)<{}[^>]*?>", tagName);
            content.replaceAll(regex, String.format("<{}>", tagName));
        }
        return content;
    }

    /**
     * 清楚html标签
     *
     * @param inputString
     * @param stringLenth
     * @return
     */
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
