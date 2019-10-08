package com.xinchao.tech.xinchaoad.common.util.images;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FontText {
    private String text;

    private int wmTextPos;

    private String wmTextColor;

    private Integer wmTextSize;

    private String wmTextFont;//字体  “黑体，Arial”

    public FontText(String text, int wmTextPos, String wmTextColor,
                    Integer wmTextSize, String wmTextFont) {
        super();
        this.text = text;
        this.wmTextPos = wmTextPos;
        this.wmTextColor = wmTextColor;
        this.wmTextSize = wmTextSize;
        this.wmTextFont = wmTextFont;
    }

    public FontText() {
    }
}

