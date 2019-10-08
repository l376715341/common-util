package com.xinchao.tech.xinchaoad.common.util.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LifeUrlUtils {

    /**
     * 通过图片url返回图片Bitmap
     *
     * @param path
     * @return
     */
    public static InputStream getInputStream(String path) {
        URL url = null;
        InputStream is = null;
        try {
            url = new URL(path);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();//利用HttpURLConnection对象,我们可以从网络中获取网页数据.
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream();    //得到网络返回的输入流

        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }

    public static String getName(String url) {
        String[] str = url.split("/");
        if (str.length > 0) {
            return str[str.length - 1];
        }
        return null;
    }

    /**
     * 将InputStream内的内容全部读取，作为bytes返回
     *
     * @param path
     * @return
     * @throws IOException @see InputStream.read()
     */
    public static byte[] getByteArrayByPath(String path) throws IOException {
        InputStream is = getInputStream(path);
        byte[] b = new byte[1024];
        // 定义一个输出流存储接收到的数据
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 开始接收数据
        int len = 0;
        while (true) {
            len = is.read(b);
            if (len == -1) {
                // 数据读完
                break;
            }
            byteArrayOutputStream.write(b, 0, len);
        }
        return byteArrayOutputStream.toByteArray();
    }

}