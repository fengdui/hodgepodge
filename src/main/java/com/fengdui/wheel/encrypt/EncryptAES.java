package com.fengdui.wheel.encrypt;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class EncryptAES {

    private static final String GLOBAL_KEY= "..";
    private static Cipher cipherEncrypt;
    private static Cipher cipherDecrypt;
    static {
        KeyGenerator kgen = null;
        try {
            kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
            secureRandom.setSeed(GLOBAL_KEY.getBytes());
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            cipherEncrypt = Cipher.getInstance("AES");// 创建密码器
            cipherEncrypt.init(Cipher.ENCRYPT_MODE, key);// 初始化
            cipherDecrypt = Cipher.getInstance("AES");// 创建密码器
            cipherDecrypt.init(Cipher.DECRYPT_MODE, key);// 初始化

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    public static String encrypt(String content) throws Exception{
        byte[] byteContent = content.getBytes("utf-8");
        byte[] result = cipherEncrypt.doFinal(byteContent);
        return parseByte2HexStr(result); // 加密
    }

    public static String decrypt(String str) throws BadPaddingException, IllegalBlockSizeException {
        byte[] content = parseHexStr2Byte(str);
        byte[] result = cipherDecrypt.doFinal(content);
        return new String(result); // 加密
    }
    /**
     * 加密
     *
     * @param content 需要加密的内容
     * @param ekey  加密密码
     * @return
     */
    public static String encrypt(String content, String ekey) throws Exception{
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
            secureRandom.setSeed(ekey.getBytes());
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return parseByte2HexStr(result); // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**解密
     * @param str  待解密内容
     * @param ekey 解密密钥
     * @return
     */
    public static String decrypt(String str, String ekey) {
        try {
            byte[] content = parseHexStr2Byte(str);
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG" );
            secureRandom.setSeed(ekey.getBytes());
            kgen.init(128, secureRandom);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return new String(result); // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
                    16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
    public static void main(String[] args) throws Exception {
        System.out.println(System.currentTimeMillis());
//        String content = "ybtccc123456";
////加密
//        System.out.println("加密前：" + content);
//        String encryptResult = encrypt(content, Passport.key);
//        System.out.println(System.currentTimeMillis());
////解密
//        String decryptResult = decrypt(encryptResult, Passport.key);
//        System.out.println("解密后：" + decryptResult);
//        System.out.println(System.currentTimeMillis());

        String content2 = "ybtccc123456";
//加密
        System.out.println("加密前：" + content2);
        String encryptResult2 = encrypt(content2);
        System.out.println(System.currentTimeMillis());
//解密
        String decryptResult2 = decrypt(encryptResult2);
        System.out.println("解密后：" + decryptResult2);
        System.out.println(System.currentTimeMillis());
    }
}
