package com.xinchao.tech.xinchaoad.common.util.propertycheck.impl;

import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;
import com.xinchao.tech.xinchaoad.common.util.PropertyCheck;
import com.xinchao.tech.xinchaoad.common.util.propertycheck.PropertyCheckProcessor;

public class MaxLengthCheckProcessor implements PropertyCheckProcessor {

    @Override
    public boolean shouldDo(PropertyCheck propertyCheck) {
        return propertyCheck.getMaxLength()!=null&&propertyCheck.getMaxLength()>0;
    }

    @Override
    public BaseException check(PropertyCheck propertyCheck, Object value) {
        if(value==null||!(value instanceof String)){
            return null;
        }
        int length=((String) value).length();
        if(length>propertyCheck.getMaxLength()){
            return new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(),String.format("[%s]超过最大长度：%d,当前长度：%d",propertyCheck.getDescriptor(),propertyCheck.getMaxLength(),length));
        }

        return null;
    }
}
