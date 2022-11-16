package com.fengdui.tool.encrypt;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * 使用commons-codec包加解密
 */
public class CommonsCodec {

    /**
     * 签名字符串
     *
     * @param text          需要签名的字符串
     * @return 签名结果
     */
    public static String sign(String text) {
        return DigestUtils.md5Hex(text);
    }

    public static String base64Encode(byte[] bytes) throws UnsupportedEncodingException {
        String rs1 = new String(Base64.getEncoder().encode(bytes), "utf-8");
        return rs1;
    }
    public static byte[] base64Decode(byte[] bytes) throws UnsupportedEncodingException {
        return Base64.getDecoder().decode(bytes);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        System.out.println(base64Encode("你好111".getBytes("utf-8")));
    }
}
