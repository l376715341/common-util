package com.xinchao.tech.xinchaoad.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

public class RedisCacheUtil {

    public static final String SPLIT = "_";
    public static final long DEFAULT_EXPIRE_TIME = 3600 * 24 * 1 * 30;  //1月

    public static String buildKey(String prefix, String... names) {
        if (StringUtils.isBlank(prefix)) {
            throw new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(), "prefix cant be blank");
        }
        if (names == null || names.length == 0) {
            return prefix;
        }
        StringJoiner joiner = new StringJoiner(SPLIT);
        joiner.add(prefix);
        Arrays.stream(names).forEach(item -> joiner.add(item));
        return joiner.toString();
    }

    /**
     * 注意：getCacheObject只适合用来保存单对象或者简单对象，因为集合类或者复杂对象可能会反序列化失败
     *
     * @param redisTemplate
     * @param key
     * @param clazz
     * @param expire
     * @param fetcher
     * @param <T>
     * @return
     */
    public static <T> T getCacheObject(RedisTemplate<String, String> redisTemplate, String key, Class<T> clazz, long expire, Fetcher<T> fetcher) {
        if (redisTemplate == null) {
            throw new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(), "redis cant be null");
        }
        T result = null;
        String cacheValue = redisTemplate.opsForValue().get(key);
        if (cacheValue != null) {
            result = JSON.parseObject(cacheValue, clazz, Feature.OrderedField);
        } else {
            result = fetcher.doFetch();
            if (result != null) {
                redisTemplate.opsForValue().set(key, JSON.toJSONString(result), expire, TimeUnit.SECONDS);
            }
        }
        return result;
    }

    /**
     * @param redisTemplate
     * @param key
     * @param clazz         list 泛型
     * @param expire
     * @param fetcher
     * @return
     */
    public static List getList(RedisTemplate<String, String> redisTemplate, String key, Class clazz, long expire, Fetcher<List> fetcher) {
        if (redisTemplate == null) {
            throw new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(), "redis cant be null");
        }
        List result = null;
        String cacheValue = redisTemplate.opsForValue().get(key);
        if (cacheValue != null) {
            result = JSON.parseArray(cacheValue, clazz);
        } else {
            result = fetcher.doFetch();
            if (result != null && result.size() > 0) {
                redisTemplate.opsForValue().set(key, JSON.toJSONString(result), expire, TimeUnit.SECONDS);
            }
        }
        return result;
    }

    public interface Fetcher<T> {
        T doFetch();
    }
}
