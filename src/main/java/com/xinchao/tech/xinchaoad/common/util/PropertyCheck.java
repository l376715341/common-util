package com.xinchao.tech.xinchaoad.common.util;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

@Slf4j
public class PropertyCheck {

    protected static final Pattern      PATTERN_MAIL                    =Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
    protected static final Pattern      PATTERN_PHONE                   =Pattern.compile("1\\d{10}");

    @Getter @Setter String              fieldName;
    @Getter @Setter String              descriptor;

    @Getter @Setter Boolean             nullable;
    @Getter @Setter Integer             maxLength;
    @Getter @Setter Integer             minLength;

    @Getter @Setter String              range;

    @Getter @Setter Boolean             mail;
    @Getter @Setter Boolean             phone;

}
