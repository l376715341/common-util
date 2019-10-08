package com.xinchao.tech.xinchaoad.common.util;

/**
 * @Author Li Hui
 * @Date 2018/12/25 15:24
 **/


public class WxPayResult {
    public static String success() {
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
    }

    public static String fail() {
        return "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[ERROR]]></return_msg></xml>";
    }
}
