package com.fengdui.framework.web;

import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Escape编码
 */
public class EscapeUtil {

    /**
     * Escape编码（Unicode）
     *
     * @param content
     * @return 编码后的字符串
     */
    public static String escape(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }

        int i;
        char j;
        StringBuilder tmp = new StringBuilder();
        tmp.ensureCapacity(content.length() * 6);

        for (i = 0; i < content.length(); i++) {

            j = content.charAt(i);

            if (Character.isDigit(j) || Character.isLowerCase(j) || Character.isUpperCase(j))
                tmp.append(j);
            else if (j < 256) {
                tmp.append("%");
                if (j < 16)
                    tmp.append("0");
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }

    /**
     * Escape解码
     *
     * @param content
     * @return 解码后的字符串
     */
    public static String unescape(String content) {
        if (StringUtils.isBlank(content)) {
            return content;
        }

        StringBuilder tmp = new StringBuilder(content.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < content.length()) {
            pos = content.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (content.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(content.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(content.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(content.substring(lastPos));
                    lastPos = content.length();
                } else {
                    tmp.append(content.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String url = "<s:property value=\"webPath\"/>/ShowMoblieQRCode.servlet?name=我是cm";
        System.out.println(URLEncoder.encode(url, "UTF-8"));
        System.out.println(escape("你好"));
        System.out.println(new URLCodec().encode(url, "utf-8"));
        System.out.println(StringEscapeUtils.escapeHtml4("你好"));
    }
}
