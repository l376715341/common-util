package com.xinchao.tech.xinchaoad.common.util.oss;

import java.util.HashMap;
import java.util.Map;
/**
 * @Auther: xc
 * @Date: 2018/6/21 09:58
 * @Description: 文件mime扩展格式类型
 */
public class MimeTypeFileExtensionConvert {


    private static Map<String,String> maps = new HashMap<String,String>();

    static{
        maps.put("image/jpeg",".jpg");
        maps.put("image/gif",".gif" );
        maps.put("image/png",".png" );
        maps.put("image/bmp",".bmp" );
        maps.put("text/plain",".txt");
        maps.put("application/zip",".zip" );
        maps.put("application/x-zip-compressed",".zip" );
        maps.put("multipart/x-zip",".zip" );
        maps.put("application/x-compressed",".zip" );
        maps.put("audio/mpeg3",".mp3" );
        maps.put("video/avi",".avi" );
        maps.put("audio/wav",".wav" );
        maps.put("application/x-gzip",".gzip" );
        maps.put("application/x-gzip",".gz");
        maps.put("text/html",".html");
        maps.put("application/x-shockwave-flash",".svg");
        maps.put("application/pdf",".pdf" );

    }

    public static String getFileExtension(String mimeType){
        return maps.get(mimeType);
    }

}
