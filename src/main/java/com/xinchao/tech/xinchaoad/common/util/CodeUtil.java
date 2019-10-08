package com.xinchao.tech.xinchaoad.common.util;

import java.util.Random;

/**
 * @author: luhanyu
 * @Date: 2018/7/6 14:39
 * @Description:
 */
public class CodeUtil {

    public static String getSMSCode(){
        StringBuffer result= new StringBuffer();
        Random random = new Random();

        for (int i=0;i<6;i++)
        {
           result.append(random.nextInt(10));
        }
        System.out.println(result);
        return result.toString();
    }


}
