package com.fengdui.wheel.string;

import org.apache.commons.text.StringEscapeUtils;


public final class MyStringUtil {

    public void escapeHtml4() {
        String str = "</p>\n" +
                "<p><code>\"bread\" &amp; \"butter\"</code></p>";
        System.out.println(StringEscapeUtils.escapeHtml4(str));
        System.out.println(StringEscapeUtils.unescapeHtml4(StringEscapeUtils.escapeHtml4(str)));
    }
}
