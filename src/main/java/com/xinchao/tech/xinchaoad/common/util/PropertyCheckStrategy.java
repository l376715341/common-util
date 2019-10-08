package com.xinchao.tech.xinchaoad.common.util;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PropertyCheckStrategy {

    @Getter @Setter String                      className;          //例如：com.xinchao.tech.xinchaoad.common.util.PropertyCheckStrategy
    @Getter @Setter String                      tag;                //策略标签，例如是用于前端检查的VO，数据检查的DTO

    @Getter @Setter List<PropertyCheck>         checkStrategy;     //具体每个字段的检查策略

}
