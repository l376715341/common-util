package com.xinchao.tech.xinchaoad.common.util;


import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * @author: luhanyu
 * @Date: 2018/7/9 15:12
 * @Description:
 */
public class MD5Util {
    private final static String HEX_NUMS_STR = "0123456789ABCDEF";
    private final static Integer SALT_LENGTH = 12;

    /**
     * 将16进制字符串转换成数组
     *
     * @return byte[]
     * @author jacob
     * */
    public static byte[] hexStringToByte(String hex) {
        /* len为什么是hex.length() / 2 ?
         * 首先，hex是一个字符串，里面的内容是像16进制那样的char数组
         * 用2个16进制数字可以表示1个byte，所以要求得这些char[]可以转化成什么样的byte[]，首先可以确定的就是长度为这个char[]的一半
         */
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] hexChars = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (HEX_NUMS_STR.indexOf(hexChars[pos]) << 4 | HEX_NUMS_STR
                    .indexOf(hexChars[pos + 1]));
        }
        return result;
    }

    /**
     * 将数组转换成16进制字符串
     *
     * @return String
     * @author jacob
     *
     * */
    public static String byteToHexString(byte[] salt){
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < salt.length; i++) {
            String hex = Integer.toHexString(salt[i] & 0xFF);
            if(hex.length() == 1){
                hex = '0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }

    /**
     * 密码验证
     * @param passwd 用户输入密码
     * @param dbPasswd 数据库保存的密码
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static boolean validPasswd(String passwd, String dbPasswd) {
        try {
            byte[] pwIndb = hexStringToByte(dbPasswd);
            //定义salt
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(pwIndb, 0, salt, 0, SALT_LENGTH);
            //创建消息摘要对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            //将盐数据传入消息摘要对象
            md.update(salt);
            md.update(passwd.getBytes("UTF-8"));
            byte[] digest = md.digest();
            //声明一个对象接收数据库中的口令消息摘要
            byte[] digestIndb = new byte[pwIndb.length - SALT_LENGTH];
            //获得数据库中口令的摘要
            System.arraycopy(pwIndb, SALT_LENGTH, digestIndb, 0, digestIndb.length);
            //比较根据输入口令生成的消息摘要和数据库中的口令摘要是否相同
            if (Arrays.equals(digest, digestIndb)) {
                //口令匹配相同
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            throw new BaseException(ResultCode.FAIL_UNKNOWN.getCode(),"md5 错误："+passwd,e);
        }
    }

    /**
     * 获得md5之后的16进制字符
     * @param passwd 用户输入密码字符
     * @return String md5加密后密码字符
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String getEncryptedPwd(String passwd) {
        try {
            //拿到一个随机数组，作为盐
            byte[] pwd = null;
            SecureRandom sc = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            sc.nextBytes(salt);

            //声明摘要对象，并生成
            MessageDigest md = MessageDigest.getInstance("MD5");
            //计算MD5函数
            md.update(salt);
            //passwd.getBytes("UTF-8")将输入密码变成byte数组，即将某个数装换成一个16进制数
            md.update(passwd.getBytes("UTF-8"));
            //计算后获得字节数组,这就是那128位了即16个元素
            byte[] digest = md.digest();
            pwd = new byte[salt.length + digest.length];
            System.arraycopy(salt, 0, pwd, 0, SALT_LENGTH);
            System.arraycopy(digest, 0, pwd, SALT_LENGTH, digest.length);
            return byteToHexString(pwd);
        }catch (Exception e){
            throw new BaseException(ResultCode.FAIL_UNKNOWN.getCode(),"md5 错误："+passwd,e);
        }
    }

    public static String toStandardMD5(String value){
        return DigestUtils.md5Hex(value);
    }

    public static String inputStream2Md5(InputStream inputStream) throws  Exception {
        MessageDigest messageDigest = null;
        messageDigest = MessageDigest.getInstance("md5");
        byte[] buffer = new byte[8192];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            messageDigest.update(buffer, 0, length);
        }
        return Hex.encodeHexString(messageDigest.digest());
    }

    public static void main(String[] args) {
        System.out.println(validPasswd("123456","CE50C5A37A0113A70B917DB29297878B91DC55DD1F090790939E7A76"));
    }
}
