package com.xinchao.tech.xinchaoad.common.util;

import java.util.List;

public interface EnumInterface<T> {

    int                         getCode();
    String                      getDesc();
    List<T>                     getAllValues();

}
