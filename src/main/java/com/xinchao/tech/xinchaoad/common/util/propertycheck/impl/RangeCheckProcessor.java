package com.xinchao.tech.xinchaoad.common.util.propertycheck.impl;

import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;
import com.xinchao.tech.xinchaoad.common.util.PropertyCheck;
import com.xinchao.tech.xinchaoad.common.util.propertycheck.PropertyCheckProcessor;
import org.apache.commons.lang3.StringUtils;

public class RangeCheckProcessor implements PropertyCheckProcessor {

    @Override
    public boolean shouldDo(PropertyCheck propertyCheck) {
        return StringUtils.isNoneBlank(propertyCheck.getRange());
    }

    @Override
    public BaseException check(PropertyCheck propertyCheck, Object value) {
        if(value==null){
            return null;
        }
        if(!(value instanceof Number)){
            return new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(),String.format("[%s]不是数字没法进行比较",value));
        }
        String range=propertyCheck.getRange().trim();
        //是否闭区间
        boolean left=false;
        boolean right=false;
        if(range.startsWith("[")){
            left=true;
        }
        if(range.endsWith("]")){
            right=true;
        }
        String[] array=range.replace(" ","").replace("[","").replace("(","")
                .replace("]","").replace(")","").split(",");
        if(array.length!=2){
            return null;
        }
        if(StringUtils.isBlank(array[0])||StringUtils.isBlank(array[1])||!StringUtils.isNumeric(array[0])||!StringUtils.isNumeric(array[1])){
            return null;
        }
        Double min=Double.parseDouble(array[0]);
        Double max=Double.parseDouble(array[1]);
        if(value instanceof Integer||value instanceof Long||value instanceof Double||value instanceof Float){
            Double check=Double.parseDouble(value.toString());
            boolean failed=false;
            if(left&&check<min){
                if(check<min){
                    failed=true;
                }
            }else if(!left&&check<=min){
                failed=true;
            }
            if(!failed){
                if(right&&check>max){
                    failed=true;
                }else if(!right&&check>=max){
                    failed=true;
                }
            }
            if(failed){
                return new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(),String.format("[%s]超出范围，当前值：%d,允许范围：%s",propertyCheck.getDescriptor(),value,range));
            }
        }

        return null;
    }
}
