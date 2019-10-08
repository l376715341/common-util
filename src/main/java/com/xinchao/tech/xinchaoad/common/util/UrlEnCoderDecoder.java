package com.xinchao.tech.xinchaoad.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * @Author Li Hui
 * @Date 2019/4/22 18:06
 **/

@Slf4j
public class UrlEnCoderDecoder {

    private static String regex = "^([hH][tT]{2}[pP]:/*|[hH][tT]{2}[pP][sS]:/*|[fF][tT][pP]:/*)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+(\\?{0,1}(([A-Za-z0-9-~]+\\={0,1})([A-Za-z0-9-~]*)\\&{0,1})*)$";

    public static String encode(String srcUrl) {
        String dstUrl = srcUrl;
        try {
            dstUrl = URLEncoder.encode(srcUrl, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ex) {
            log.error("exception", ex);
        } finally {

        }

        return dstUrl;
    }

    public static String decode(String srcUrl) {
        String dstUrl = srcUrl;
        try {
            dstUrl = URLDecoder.decode(srcUrl, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException ex) {
            log.error("exception", ex);
        } finally {

        }

        return dstUrl;
    }

    public static boolean urlIsLegal(String url) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(url).matches();
    }
}
