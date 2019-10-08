package com.xinchao.tech.xinchaoad.common.util;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @author jianghaiqiang
 * @date 2019/5/27
 */
public class MathUtils {

    public static boolean isNumeric(String str){
        Pattern pattern = compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static boolean isLetterOrNumeric(String str){
        Pattern pattern = compile("^[A-Za-z0-9]+$");
        return pattern.matcher(str).matches();
    }
}
