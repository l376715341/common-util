package com.xinchao.tech.xinchaoad.common.util.propertycheck;

import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.util.PropertyCheck;

public interface PropertyCheckProcessor {

    boolean                     shouldDo(PropertyCheck propertyCheck);
    BaseException               check(PropertyCheck propertyCheck, Object value);

}
