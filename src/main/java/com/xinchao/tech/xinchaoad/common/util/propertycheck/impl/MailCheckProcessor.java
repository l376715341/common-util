package com.xinchao.tech.xinchaoad.common.util.propertycheck.impl;

import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;
import com.xinchao.tech.xinchaoad.common.util.PropertyCheck;
import com.xinchao.tech.xinchaoad.common.util.propertycheck.PropertyCheckProcessor;

import java.util.regex.Pattern;

public class MailCheckProcessor implements PropertyCheckProcessor {

    protected final Pattern                     PATTERN_MAIL                    =Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");

    @Override
    public boolean shouldDo(PropertyCheck propertyCheck) {
        return propertyCheck.getPhone()!=null&&propertyCheck.getPhone();
    }

    @Override
    public BaseException check(PropertyCheck propertyCheck, Object value) {
        if(value==null||!(value instanceof String)){
            return new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(),String.format("[%s]不能为空",propertyCheck.getDescriptor()));
        }
        int length=((String) value).length();
        if(length>255){
            return new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(),String.format("[%s]超过邮箱最大长度255,当前长度：%d",propertyCheck.getDescriptor(),length));
        }

        if(!PATTERN_MAIL.matcher((String)value).matches()){
            return new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(),String.format("[%s]不符合邮箱规范",propertyCheck.getDescriptor()));
        }

        return null;
    }
}
