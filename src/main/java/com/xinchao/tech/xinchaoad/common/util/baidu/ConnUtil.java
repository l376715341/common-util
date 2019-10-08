package com.xinchao.tech.xinchaoad.common.util.baidu;

import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

/**
 * 与连接相关的Util类
 */
public class ConnUtil {
     /**
     * 从HttpURLConnection 获取返回的bytes
     * 注意 HttpURLConnection自身问题， 400类错误，会直接抛出异常。不能获取conn.getInputStream();
     *
     * @param conn
     * @return
     * @throws IOException   http请求错误
     * @throws BaseException http 的状态码不是 200
     */
    public static byte[] getResponseBytes(HttpURLConnection conn) throws IOException, BaseException {
        int responseCode = conn.getResponseCode();
        InputStream inputStream = conn.getInputStream();
        if (responseCode != 200) {
            System.err.println("http 请求返回的状态码错误，期望200， 当前是 " + responseCode);
            if (responseCode == 401) {
                System.err.println("可能是appkey appSecret 填错");
            }
            System.err.println("response headers" + conn.getHeaderFields());
            if (inputStream == null) {
                inputStream = conn.getErrorStream();
            }
            byte[] result = getInputStreamContent(inputStream);
            System.err.println(new String(result));

            throw new BaseException(ResultCode.FAIL_HTTP_REQ.getCode(),"http response code is" + responseCode);
        }

        byte[] result = getInputStreamContent(inputStream);
        return result;
    }

    /**
     * 将InputStream内的内容全部读取，作为bytes返回
     *
     * @param is
     * @return
     * @throws IOException @see InputStream.read()
     */
    public static byte[] getInputStreamContent(InputStream is) throws IOException {
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

    /**
     * <p>
     * 从流中读取 byte[]
     * </p>
     *
     * @param inputStream
     *            输入 流
     * @param bufferSize
     *            缓存 大小
     * @return byte[] 读取到的 字节数组
     * @throws IOException
     *             the exception
     */
    public static byte[] read(InputStream inputStream, int bufferSize) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[bufferSize];
        int num = inputStream.read(buffer);
        while (num != -1) {
            baos.write(buffer, 0, num);
            num = inputStream.read(buffer);
        }
        baos.flush();
        return baos.toByteArray();
    }

    /**
     * <p>
     * 从输入流中读取指定长度的 字节
     * </p>
     *
     * @param inputStream
     *            输 入 流
     * @param length
     *            指定的 长度
     * @param bufferSize
     *            每次读取的 缓存大小
     * @return byte[] 最终读到的 字节数组
     * @throws IOException
     *             the exception
     */
    private static byte[] read(InputStream inputStream, int length, int bufferSize) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[bufferSize];
        int totalNum = 0;
        int num = 0;
        // totalNum += num;

        num = inputStream.read(buffer);
        totalNum += num;
        while (num > 0 && totalNum <= length) {
            baos.write(buffer, 0, num);
            if (totalNum >= length) {
                break;
            }

            num = inputStream.read(buffer);
            totalNum += num;
        }
        baos.flush();
        return baos.toByteArray();
    }

    /**
     * 以bufferSize 大小为单位向 outputStream发送数据
     *
     * @param outputStream
     *            输 出 流
     * @param contentBytes
     *            内容 字节数组
     * @param bufferSize
     *            缓存 大小
     * @throws IOException
     *             the exception
     */
    public static void write(OutputStream outputStream, byte[] contentBytes, int bufferSize) throws IOException {
        int length = contentBytes.length;
        int count = length / bufferSize;

        byte[] buffer = new byte[bufferSize];
        for (int index = 0; index < count; index++) {
            System.arraycopy(contentBytes, index * bufferSize, buffer, 0, bufferSize);
            outputStream.write(buffer);
        }

        if (length % bufferSize != 0) {
            int remaining = length - count * bufferSize;
            System.arraycopy(contentBytes, count * bufferSize, buffer, 0, remaining);
            outputStream.write(buffer, 0, remaining);
        }
        outputStream.flush();
    }

    /**
     * <p>
     * 向输出流中 写入数据
     * </p>
     *
     * @param outputStream
     *            输 出 流
     * @param sendBytes
     *            要 写入 的数据
     * @throws IOException
     *             the exception
     */
    public static void sendLengthValue(OutputStream outputStream, byte[] sendBytes) throws IOException {
        outputStream.write(addLength(sendBytes));
        outputStream.flush();
    }

    /**
     * <p>
     * 从输入流中读取 数据
     * </p>
     *
     * @param inputStream
     *            输 入 流
     * @return byte[] 读取到的 字节数组
     * @throws IOException
     *             the exception
     */
    public static byte[] readLengthValue(InputStream inputStream) throws IOException {
        int receiveLength = readInt(inputStream);
        int bufferSize = receiveLength < 4096 ? receiveLength : 4096;
        return read(inputStream, receiveLength, bufferSize);
    }

    private static byte[] addLength(byte[] bytes) {
        int totalLength = bytes.length + 4;

        byte[] lengthedArray = new byte[totalLength];

        System.arraycopy(intToByteArray(bytes.length), 0, lengthedArray, 0, 4);
        System.arraycopy(bytes, 0, lengthedArray, 4, bytes.length);

        return lengthedArray;
    }

    private static byte[] intToByteArray(int i) {
        byte[] intBytes = new byte[4];
        intBytes[0] = (byte) ((i >> 24) & 0xFF);
        intBytes[1] = (byte) ((i >> 16) & 0xFF);
        intBytes[2] = (byte) ((i >> 8) & 0xFF);
        intBytes[3] = (byte) (i & 0xFF);
        return intBytes;
    }

    private static int readInt(InputStream inputStream) throws IOException {
        int ch1 = inputStream.read();
        int ch2 = inputStream.read();
        int ch3 = inputStream.read();
        int ch4 = inputStream.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new EOFException();
        }
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
    }
}
