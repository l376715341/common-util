package com.xinchao.tech.xinchaoad.common.util.baidu;

import lombok.Data;

/**
 * @Author: RobertSean
 * @Date: 2019/9/20 17:34
 */
@Data
public class OcrResult {
    /**
     * 注册号,统一社会信用代码
     */
    private String regNum;
    /**
     * 名称/个体工商户名称
     */
    private String name;
    /**
     * 注册资本
     */
    private String capital;
    /**
     * 法人代表/经营者姓名
     */
    private String person;
    /**
     * 地址
     */
    private String address;
    /**
     * 经营范围
     */
    private String business;
    /**
     * 类型
     */
    private String type;
    /**
     * 成立日期(有效期起)
     */
    private String periodStart;
    /**
     * 有效期止
     */
    private String periodEnd;
    /**
     * 组成形式
     */
    private String composingForm;
}
