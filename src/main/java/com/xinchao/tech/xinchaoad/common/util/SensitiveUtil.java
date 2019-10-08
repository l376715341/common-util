package com.xinchao.tech.xinchaoad.common.util;

/**
 * @description: 敏感信息处理
 * @author: Yang Yanzhao
 * @create: 2019-04-09 16:50
 **/
public class SensitiveUtil {
    /**
     * 隐藏身份证号码的中间8位
     * @param idcard 身份证号
     * @return 隐藏后的身份证号
     */
    public static String hideIdCard(String idcard){
        if(StrUtil.isEmpty(idcard) || idcard.length() != 18){
            return idcard;
        }
        return idcard.substring(0,6) + "********" + idcard.substring(14);
    }
    /**
     * 隐藏手机号的中间4位
     * @param phone 手机号
     * @return 隐藏后的手机号
     */
    public static String hidePhone(String phone){
        if(StrUtil.isEmpty(phone) || phone.length() != 11){
            return phone;
        }
        return phone.substring(0,3) + "****" + phone.substring(7);
    }
    /**
     * 隐藏姓名
     * @param name 姓名
     * @return 隐藏后的姓名
     */
    public static String hideName(String name){
        if(StrUtil.isEmpty(name) || name.length() < 2){
            return name;
        }
        if(name.length() == 1) {
            return name;
        }else if(name.length() == 2){
            return StrUtil.left(name, 1) + "*" ;
        }else if(name.length() == 3){
            return StrUtil.left(name, 1) + "*" + StrUtil.right(name, 1);
        }else if(name.length() == 4){
            return StrUtil.left(name, 1) + "**" + StrUtil.right(name, 1);
        }
        return name;
    }
}
