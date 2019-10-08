package com.xinchao.tech.xinchaoad.common.util;


import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by warden on 2015/7/17.
 */
@Slf4j
public class CacheUtil {

    final static int                CLEAN_FREQUENCY         =1000;
    final static int                CAPACITY                =10000*50;

    public static Map<String,CacheItem>    cacheMap                =new ConcurrentHashMap<String, CacheItem>(CAPACITY);
    static ScheduledExecutorService executorService         = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("cache-util-cleaner"));

    static{
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> waitDelItem=new ArrayList<String>();
                    Date now=new Date();
                    for(Map.Entry<String,CacheItem> item:cacheMap.entrySet()){
                        if(item.getValue().getExpireAt().before(now)){
                            waitDelItem.add(item.getKey());
                        }
                    }
                    for(String item:waitDelItem){
                        try {
                            cacheMap.remove(item);
                        }catch (Exception ex){
                            log.error("clear cache err,key:"+item);
                        }
                    }
                }catch (Exception ex){
                    log.error("clear cache err",ex);
                }
            }
        },CLEAN_FREQUENCY,CLEAN_FREQUENCY, TimeUnit.MILLISECONDS);
    }

    public static boolean put(String key,Object value,int expireSecond){
        try {
            if(cacheMap.size()>CAPACITY-10000){
                log.warn("CACHE-UTIL OOM");
                return false;
            }
            CacheItem item=new CacheItem(key,value,new Date(System.currentTimeMillis()+expireSecond*1000));
            cacheMap.put(key,item);
            return true;
        }catch (Exception ex){
            return false;
        }
    }

    public static boolean put(String key,Object value){
        return put(key,value,60);
    }

    public static <T> T get(String key){
        CacheItem cacheItem=cacheMap.get(key);
        if(cacheItem==null||cacheItem.getValue()==null){
            return null;
        }
        try {
            return (T)cacheItem.getValue();
        }catch (Exception ex){
            return null;
        }
    }

    public static void del(String key){
        try {
            cacheMap.remove(key);
        }catch (Exception ex){
            log.error("del key err,key:"+key);
        }
    }

    public static  <T> T getObjectWithCache(String key,int expireSecond,Fetcher<T> fetcher){
        T result=get(key);
        if(result==null){
            result=fetcher.get();
            if(result!=null){
                put(key,result,expireSecond);
            }
        }
        return result;
    }

    public interface Fetcher<T>{
        T get();
    }
}
