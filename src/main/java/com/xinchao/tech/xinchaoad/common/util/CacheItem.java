package com.xinchao.tech.xinchaoad.common.util;

import lombok.Getter;

import java.util.Date;

public class CacheItem {
    String          key;
    @Getter
    Object          value;
    @Getter
    Date expireAt;

    public CacheItem(String key,Object value,Date expireAt){
        this.key=key;
        this.value=value;
        this.expireAt=expireAt;
    }
}