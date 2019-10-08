package com.xinchao.tech.xinchaoad.common.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * @Author: RobertSean
 * @Date: 2019/9/19 14:32
 */
public class Base64Util {
    public static String getImageBinary(String path) {
        byte[] data = null;
        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(path);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        Base64.Encoder encoder = Base64.getEncoder();
        String encode = encoder.encodeToString(data).replaceAll("\r|\n", "");
        return encode;
    }

    public static String getImageBinary(MultipartFile file) {
        byte[] data = null;
        // 读取图片字节数组
        try {
            if (null == file) {
                return null;
            }
            InputStream in = file.getInputStream();
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        Base64.Encoder encoder = Base64.getEncoder();
        String encode = encoder.encodeToString(data).replaceAll("\r|\n", "");
        return encode;
    }
}
