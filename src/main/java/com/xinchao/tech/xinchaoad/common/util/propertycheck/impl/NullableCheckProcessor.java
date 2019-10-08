package com.xinchao.tech.xinchaoad.common.util.propertycheck.impl;

import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;
import com.xinchao.tech.xinchaoad.common.util.PropertyCheck;
import com.xinchao.tech.xinchaoad.common.util.propertycheck.PropertyCheckProcessor;

public class NullableCheckProcessor implements PropertyCheckProcessor {

    @Override
    public boolean shouldDo(PropertyCheck propertyCheck) {
        return propertyCheck.getNullable()!=null;
    }

    @Override
    public BaseException check(PropertyCheck propertyCheck, Object value) {
        if(!propertyCheck.getNullable()&&value==null){
            return   new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(),String.format("[%s]不能为空",propertyCheck.getDescriptor()));
        }

        return null;
    }
}
