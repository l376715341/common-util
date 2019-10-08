package com.xinchao.tech.xinchaoad.common.util.oss;
/**
 * @Auther: xc
 * @Date: 2018/6/21 09:58
 * @Description: 文件路径工具类
 */
public class FilePathHelper {

    public static final String HTTP_PREFIX = "http://";
    public static final String HTTPS_PREFIX = "https://";
    public static final String DIR_SPLITER = "/";

    public static String parseFileExtension(String filePath){
        if(filePath.contains("/")){
            filePath = filePath.substring(filePath.lastIndexOf("/"));
        }
        filePath = filePath.split("\\?")[0];
        if(filePath.contains(".")){
            return filePath.substring(filePath.lastIndexOf(".") + 1);
        }
        return null;
    }

    public static String  parseFileName(String filePath){
        filePath = filePath.split("\\?")[0];
        int index = filePath.lastIndexOf("/") + 1;
        if(index > 0){
            return filePath.substring(index);
        }
        return filePath;
    }

    public static String getFullPath(String file,String urlprefix) {
        if(file.startsWith(HTTP_PREFIX) || file.startsWith(HTTPS_PREFIX)){
            return file;
        }
        return urlprefix + file;
    }
}
