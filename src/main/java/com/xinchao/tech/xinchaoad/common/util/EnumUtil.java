package com.xinchao.tech.xinchaoad.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class EnumUtil {


    static ConcurrentHashMap<Class,Map<Integer,EnumInterface>>
            enumMapByClassAndCode               =new ConcurrentHashMap<>();

    public static <E extends Enum<?> & EnumInterface> E byCode(Class<E> enumClass, int code) {
        if(!enumMapByClassAndCode.contains(enumClass)){
            synchronized (enumClass){
                if(!enumMapByClassAndCode.contains(enumClass)){
                    Map<Integer,EnumInterface> map=new HashMap<>();
                    E[] enumConstants = enumClass.getEnumConstants();
                    for (E e : enumConstants) {
                        map.put(e.getCode(),e);
                    }
                    enumMapByClassAndCode.putIfAbsent(enumClass,map);
                }
            }
        }
        return (E)enumMapByClassAndCode.get(enumClass).get(code);
    }

}
