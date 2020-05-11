package com.fengdui.wheel.encrypt;

import org.apache.commons.codec.digest.DigestUtils;

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
    public static String sign(String text)
    {
        return DigestUtils.md5Hex(text);
    }
}
