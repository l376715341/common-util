package com.xinchao.tech.xinchaoad.common.util;

import java.util.UUID;

/**
 * @Author Li Hui
 * @Date 2018/9/25 19:16
 */


public class UUIDUtil {

    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
