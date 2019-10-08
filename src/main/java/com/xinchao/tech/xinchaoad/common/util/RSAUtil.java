package com.xinchao.tech.xinchaoad.common.util;

import javax.crypto.Cipher;
import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class RSAUtil {
    //生成秘钥对
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    //获取公钥(Base64编码)
    public static String getPublicKey(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return byte2Base64(bytes);
    }

    //获取私钥(Base64编码)
    public static String getPrivateKey(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return byte2Base64(bytes);
    }

    //将Base64编码后的公钥转换成PublicKey对象
    public static PublicKey string2PublicKey(String pubStr) throws Exception {
        byte[] keyBytes = base642Byte(pubStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    //将Base64编码后的私钥转换成PrivateKey对象
    public static PrivateKey string2PrivateKey(String priStr) throws Exception {
        byte[] keyBytes = base642Byte(priStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }


    //公钥加密
    public static byte[] publicEncrypt(byte[] content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytes = cipher.doFinal(content);
        return bytes;
    }

    //私钥解密
    public static byte[] privateDecrypt(byte[] content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] bytes = cipher.doFinal(content);
        return bytes;
    }

    //字节数组转Base64编码
    public static String byte2Base64(byte[] bytes) {
//        sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
//        return encoder.encode(bytes).replaceAll("\r|\n", "");
        // 从JKD 9开始rt.jar包已废除，从JDK 1.8开始使用java.util.Base64.Encoder
        Base64.Encoder encoder = Base64.getEncoder();
        String encode = encoder.encodeToString(bytes).replaceAll("\r|\n", "");
        return encode;
    }


    //Base64编码转字节数组
    public static byte[] base642Byte(String base64Key) throws IOException {
//        sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
//        base64Key = base64Key.replaceAll("\r|\n", "");
//        return decoder.decodeBuffer(base64Key);
        // 从JKD 9开始rt.jar包已废除，从JDK 1.8开始使用java.util.Base64.Decoder
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] buffer = decoder.decode(base64Key.replaceAll("\r|\n", ""));
        return buffer;
    }

    public static String privateDecrypt2String(String content, KeyPair keyPair) throws Exception {
        byte[] contentByte = base642Byte(content);
        byte[] result = privateDecrypt(contentByte, keyPair.getPrivate());
        return new String(result);
    }

    public static void main(String[] args) throws Exception {
        String content = "xinchao6868";
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiUKGOH2ezdGwZyfzQy30bccJ67ajK1omzy+NTkjtlBejGMQIYzNq4erG6YktS24lQq2vE7p40doaanyB3x/xFSPAa4GKCau3icg3DzAd9MiJs/cyWWkm3oGepilYiTsMfb3yQcEcUEJP85Oy0QEMf7ogNJKb8dJw60Rr5IycJoyeIe9KGhFa95sLvn3sO3M1Jt2Tpnr3rujqIiZG8Is44K6I/esg8ycLLoHEsBJ+Z+vSbZ/jj2oQ2BaoyRzNXbGZJ7YpDAAYDTm+nLMMlaVihifvmYCkVKJPL7fi5rnDNozQqTUqL0pYmrSck5Gmjvx9lYg6r1zohlGMFDg8m/ORoQIDAQAB";
//        KeyPair keyPair = getKeyPair();
//        publicKey =getPublicKey(keyPair);
//        System.out.println(publicKey);
        PublicKey publicKey1 = string2PublicKey(publicKey);
        System.out.println(publicKey1);
        String mw = byte2Base64(publicEncrypt(content.getBytes(), publicKey1));
        System.out.println(mw);
//        String ww = new String(privateDecrypt(base642Byte(mw), string2PrivateKey(getPrivateKey(keyPair))));
//        System.out.println(ww);

    }
}
