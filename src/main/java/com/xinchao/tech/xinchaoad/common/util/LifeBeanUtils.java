package com.xinchao.tech.xinchaoad.common.util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.MessageDigest;
import java.util.*;

/**
 * @Author Li Hui
 * @Date 2018/12/10 10:36
 **/

@Slf4j
public class LifeBeanUtils {

    public static Map<String, Object> obj2Map(Object object, boolean includeSuper) {
        Map<String, Object> objMap = new TreeMap<>();
        if (null == object) {
            return objMap;
        }
        Class clazz = object.getClass();
        List<Field> fieldList = new ArrayList<>();
        fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
        if (includeSuper) {
            Class superClazz = clazz.getSuperclass();
            while (null != superClazz && !"java.lang.object".equalsIgnoreCase(superClazz.getName())) {
                fieldList.addAll(Arrays.asList(superClazz.getDeclaredFields()));
                superClazz = superClazz.getSuperclass();
            }
        }
        fieldList.forEach(field -> {
            field.setAccessible(true);
            try {
                if (Modifier.isFinal(field.getModifiers())) {
                    return;
                }
                PropertyDescriptor pd = new PropertyDescriptor(field.getName(), clazz);
                Method getMethod = pd.getReadMethod();//获得get方法
                if (null == getMethod) {
                    return;
                }
                Object fieldValue = getMethod.invoke(object);//执行get方法返回一个Object

                if (null == fieldValue) {
                    return;
                }
                boolean hasAlias = field.isAnnotationPresent(JsonAlias.class);
                if (hasAlias) {
                    String alias = field.getAnnotation(JsonAlias.class).value()[0];
                    objMap.put(alias, fieldValue);
                } else {
                    objMap.put(field.getName(), fieldValue);
                }
                field.setAccessible(false);
            } catch (Exception ex) {
                log.error("exception:", ex);
            } finally {
                field.setAccessible(false);
            }
        });

        return objMap;
    }

    public static String generateReqId() {
        String source = "0123456789abcdefghijklmnopqrstuvwxyz";
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(DateUtils.formatDateToString(new Date(), "ddHHmmssSSS"));
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            stringBuilder.append(source.charAt(random.nextInt(35)));
        }
        return stringBuilder.toString();
    }

    public static String sign(Map<String, Object> objMap, String signType, String secretKey) {
        StringBuilder sb = new StringBuilder();
        objMap.forEach((key, value) -> {
                    if (value instanceof Map || value instanceof List) {
                        sb.append(key.toLowerCase()).append("=").append(JSON.toJSONString(value)).append("&");
                    } else {
                        sb.append(key.toLowerCase()).append("=").append(value).append("&");
                    }
                }
        );
        String signSource = sb.substring(0, sb.length() - 1) + secretKey;
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(signType);
        } catch (Exception ex) {
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), ex.getMessage());
        }
        String sign = Hex.encodeHexString(messageDigest.digest(signSource.getBytes()));
        return sign;

    }

    public static Method getGetMethod(Class clazz, String fieldName) {
        StringBuffer sb = new StringBuffer();
        sb.append("get");
        sb.append(fieldName.substring(0, 1).toUpperCase());
        sb.append(fieldName.substring(1));
        try {
            return clazz.getMethod(sb.toString());
        } catch (Exception e) {
        }
        return null;
    }
}