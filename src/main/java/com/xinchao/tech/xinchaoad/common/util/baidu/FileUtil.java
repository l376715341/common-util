package com.xinchao.tech.xinchaoad.common.util.baidu;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: RobertSean
 * @Date: 2019/9/20 16:08
 */
public class FileUtil {
    /**
     * 将InputStream写入本地文件
     * @param destination 写入本地目录
     * @param input	输入流
     * @throws IOException
     */
    public static void writeToLocal(String destination, InputStream input) throws IOException {
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream downloadFile = new FileOutputStream(destination);
        while ((index = input.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
            downloadFile.flush();
        }
        downloadFile.close();
        input.close();
    }
}
