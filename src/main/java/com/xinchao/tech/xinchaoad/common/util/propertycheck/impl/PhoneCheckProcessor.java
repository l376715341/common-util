package com.xinchao.tech.xinchaoad.common.util.propertycheck.impl;

import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;
import com.xinchao.tech.xinchaoad.common.util.PropertyCheck;
import com.xinchao.tech.xinchaoad.common.util.propertycheck.PropertyCheckProcessor;
import org.apache.commons.lang3.StringUtils;

public class PhoneCheckProcessor implements PropertyCheckProcessor {

    @Override
    public boolean shouldDo(PropertyCheck propertyCheck) {
        return propertyCheck.getPhone()!=null&&propertyCheck.getPhone();
    }

    @Override
    public BaseException check(PropertyCheck propertyCheck, Object value) {
        int length=((String) value).length();
        if(length!=11){
            return new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(),String.format("[%s]长度不符合规范,当前长度：%d",propertyCheck.getDescriptor(),length));
        }
        String phone=value.toString();
        if(!StringUtils.isNumeric(phone)||!phone.startsWith("1")){
            return new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(),String.format("[%s]不符手机号规范:%s",propertyCheck.getDescriptor(),phone));
        }

        return null;
    }
}
