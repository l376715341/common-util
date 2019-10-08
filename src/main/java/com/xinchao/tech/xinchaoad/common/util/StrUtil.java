package com.xinchao.tech.xinchaoad.common.util;

/**
 * @description: 本地String处理类
 * @author: Yang Yanzhao
 * @create: 2019-04-09 16:51
 **/
public class StrUtil {
    /**
     * 定义所有常量
     */
    public static final String EMPTY = "";
    public static final int ZERO = 0;

    /**
     * 判断str是否为null，或者为空
     *
     * @param str
     * @return 为空，则返回true，否则，返回false
     */
    public static boolean isEmpty(Object str) {
        return str == null || EMPTY.equals(str);
    }


    public static boolean hasLength(CharSequence str) {
        return str != null && str.length() > 0;
    }

    public static boolean hasLength(String str) {
        return str != null && !str.isEmpty();
    }

    public static boolean hasText(CharSequence str) {
        return str != null && str.length() > 0 && containsText(str);
    }

    public static boolean hasText(String str) {
        return str != null && !str.isEmpty() && containsText(str);
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();

        for (int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param str
     * @param len
     * @return java.lang.String
     * @Description 字符串向左截取
     * @author ShengLiu
     * @date 2018/7/4
     */
    public static String left(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < ZERO) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(ZERO, len);

    }

    /**
     * @param str
     * @param len
     * @return java.lang.String
     * @Description 字符串向右截取
     * @author ShengLiu
     * @date 2018/7/4
     */
    public static String right(String str, int len) {
        if (str == null) {
            return null;
        }
        if (len < ZERO) {
            return EMPTY;
        }
        if (str.length() <= len) {
            return str;
        }
        return str.substring(str.length() - len);

    }
}
