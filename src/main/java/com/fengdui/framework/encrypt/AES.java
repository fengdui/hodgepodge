package com.fengdui.framework.encrypt;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class AES {

    private static final Logger log = LoggerFactory.getLogger(AES.class);

    public static byte[] encrypt(byte[] data, byte[] key) {
        Assert.notNull(data, "data");
        Assert.notNull(key, "key");
        if (key.length != 16)
            throw new RuntimeException("Invalid AES key length (must be 16 bytes)");
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(1, seckey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("encrypt fail!", e);
        }
    }

    public static byte[] decrypt(byte[] data, byte[] key) {
        Assert.notNull(data, "data");
        Assert.notNull(key, "key");
        if (key.length != 16)
            throw new RuntimeException("Invalid AES key length (must be 16 bytes)");
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(2, seckey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("decrypt fail!", e);
        }
    }


    public static String encryptToBase64(String data, String key) {
        try {
            byte[] valueByte = encrypt(data.getBytes("UTF-8"), key.getBytes("UTF-8"));
            return CommonsCodec.base64Encode(valueByte);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("encrypt fail!", e);
        }
    }

    public static String decryptFromBase64(String data, String key) {
        try {
            byte[] originalData = CommonsCodec.base64Decode(data.getBytes());
            byte[] valueByte = decrypt(originalData, key.getBytes("UTF-8"));
            return new String(valueByte, "UTF-8");
        } catch (Exception e) {
            log.error("解密出错", e);
            throw new RuntimeException("解密出错");
        }
    }

    public static byte[] genarateRandomKey() {
        KeyGenerator keygen = null;
        try {
            keygen = KeyGenerator.getInstance("AES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(" genarateRandomKey fail!", e);
        }
        SecureRandom random = new SecureRandom();
        keygen.init(random);
        Key key = keygen.generateKey();
        return key.getEncoded();
    }

    public static String genarateRandomKeyWithBase64() throws UnsupportedEncodingException {
        return CommonsCodec.base64Encode(genarateRandomKey());
    }

    public static void main(String[] args) {
        String s1 = "{\"phoneNumber\":\"18805716141\"}";
        String s2 = encryptToBase64(s1, "OrFwiUofe8kTQmH2C");
        String s3 = decryptFromBase64(s2, "OrFwiUofe8kTQmH2C");
        String s4 = "CQ5Q0LYSQNZuGomIO2VizntBxLbCkqsrhMzfwbiqgt4NbP/T50Irx6jQauSNfVvV1RUWEJkhwkuIM1X2nb8z2VgNlak5o2n2guREFsGKOBuWATgod40jnRBq/4zYn7Z0XljPpKOUVNtRQN+Q7dCi/A==";
        System.out.println(s1);
        System.out.println(s2);
        System.out.println(s3);
        System.out.println(decryptFromBase64(s4, "OrFwiUofe8kTQmH2C"));

    }
}