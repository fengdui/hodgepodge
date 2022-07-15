package com.fengdui.wheel.string;

import org.apache.commons.text.StringEscapeUtils;

import java.io.UnsupportedEncodingException;


public final class MyStringUtil {

    public void escapeHtml4() {
        String str = "</p>\n" +
                "<p><code>\"bread\" &amp; \"butter\"</code></p>";
        System.out.println(StringEscapeUtils.escapeHtml4(str));
        System.out.println(StringEscapeUtils.unescapeHtml4(StringEscapeUtils.escapeHtml4(str)));
    }
    /**
     * Convert input string to UTF-8, copies into buffer (at given offset).
     * Returns number of bytes in the string.
     *
     * Java's internal UTF8 conversion is very, very slow.
     * This is, rather amazingly, 8x faster than the to-string method.
     * Returns the number of bytes this translated into.
     */
    public static int stringToUtf8(String s, byte[] buf, int offset) {
        if (s == null) {
            return 0;
        }
        int length = s.length();
        int startOffset = offset;

        for (int i = 0; i < length; i++) {
            int c = s.charAt(i);
            if (c < 0x80) {
                buf[offset++] = (byte) c;
            }
            else if (c < 0x800) {
                buf[offset++] = (byte)(0xc0 | ((c >> 6)));
                buf[offset++] = (byte)(0x80 | (c & 0x3f));
            }
            else {
                // Encountered a different encoding other than 2-byte UTF8. Let java handle it.
                try {
                    byte[] value = s.getBytes("UTF8");
                    System.arraycopy(value, 0, buf, startOffset, value.length);
                    return value.length;
                }
                catch (UnsupportedEncodingException uee) {
                    throw new RuntimeException("UTF8 encoding is not supported.");
                }
            }
        }
        return offset - startOffset;
    }
}
