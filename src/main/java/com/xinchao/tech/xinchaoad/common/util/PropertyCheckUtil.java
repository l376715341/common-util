package com.xinchao.tech.xinchaoad.common.util;

import com.alibaba.fastjson.JSON;
import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.util.propertycheck.PropertyCheckProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class PropertyCheckUtil {

    public static final String          CHECK_TYPE_ADD                      = "add";
    public static final String          CHECK_TYPE_UPDATE                   = "update";
    public static final String          CHECK_TYPE_QUERY                    = "query";
    public static final String          CHECK_TYPE_ALL                      = "all";

    static final String                 INTERNAL_DIRECTORY                  = "classpath:META-INF/PropertyCheck/";

    //key=classReference + "_" + profile
    protected static ConcurrentHashMap<String,PropertyCheckStrategy>
                                        cache                               = new ConcurrentHashMap<>();
    protected static final PropertyCheckStrategy
            PLACE_HOLDER = new PropertyCheckStrategy();
    protected static List<PropertyCheckProcessor>
                                        propertyCheckProcessorList          = new ArrayList<>();

    static {
        ServiceLoader<PropertyCheckProcessor> loader = ServiceLoader.load(PropertyCheckProcessor.class);
        Iterator<PropertyCheckProcessor> processorIterable=loader.iterator();
        while (processorIterable.hasNext()){
            PropertyCheckProcessor processor=processorIterable.next();
            propertyCheckProcessorList.add(processor);
        }
    }

    public static BaseException checkProperties(Object o,String profile){
        if(o==null){
            return null;
        }
        if(StringUtils.isBlank(profile)){
            return null;
        }

        Class<?> clazz=o.getClass();
        PropertyCheckStrategy checkStrategy=getCheckStrategy(clazz,profile);
        if(checkStrategy==null){
            return null;
        }
        if(checkStrategy.getCheckStrategy()!=null&&checkStrategy.getCheckStrategy().size()>0){
            for (PropertyCheck item:checkStrategy.getCheckStrategy()){
                Field field=ReflectionUtils.findField(clazz,item.getFieldName());
                if(field!=null){
                    field.setAccessible(true);
                    Object value=ReflectionUtils.getField(field,o);
                    for(PropertyCheckProcessor processor:propertyCheckProcessorList){
                        if(processor.shouldDo(item)){
                            BaseException baseException=processor.check(item,value);
                            if(baseException!=null){
                                return baseException;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    protected static PropertyCheckStrategy getCheckStrategy(Class<?> clazz,String profile){
        String key=RedisCacheUtil.buildKey(clazz.getName(),profile);
        PropertyCheckStrategy result=cache.get(key);
        if(result==null){
            synchronized (PropertyCheckUtil.class){
                if(result==null){
                    List<PropertyCheckStrategy> checkStrategyList=load(clazz);
                    if(checkStrategyList!=null&&checkStrategyList.size()>0){
                        checkStrategyList.forEach(item->{
                            cache.put(RedisCacheUtil.buildKey(item.getClassName(),item.getTag()),item);
                        });
                    }
                    result=cache.get(key);
                    if(result==null){
                        result= PLACE_HOLDER;
                        cache.put(key,result);
                    }
                }
            }
        }
        if(PLACE_HOLDER.equals(result)){
            return null;
        }
        return result;
    }

    protected static List<PropertyCheckStrategy> load(Class<?> clazz){
        String path=INTERNAL_DIRECTORY+clazz.getName();
        String info=PropertiesLoadUtil.loadAsString(path);
        List<PropertyCheckStrategy> result=new ArrayList<>();
        if(StringUtils.isNoneBlank(info)){
            List<PropertyCheckStrategy> propertyCheckStrategyList=JSON.parseArray(info,PropertyCheckStrategy.class);
            if(propertyCheckStrategyList!=null&&propertyCheckStrategyList.size()>0){
                result.addAll(propertyCheckStrategyList);
            }
        }
        return result;
    }
}
