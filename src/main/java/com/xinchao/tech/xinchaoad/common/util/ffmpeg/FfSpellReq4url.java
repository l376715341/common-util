package com.xinchao.tech.xinchaoad.common.util.ffmpeg;

import lombok.Data;

/**
 * @author: luhanyu
 * @Date: 2019/9/20 10:15
 * @Description:
 */
@Data
public class FfSpellReq4url {

    private String upUrl;
    private String downUrl;
    private Integer upHigh;
    private Integer downHigh;
    private Integer upWidth;
    private Integer downWidth;
    private Integer duration;
    private Long templateId;
    private String creativeAddress;
    private String type;

    public String getUpSize() {
        return upWidth + "*" + upHigh;
    }

    public String getDownSize() {
        return downWidth + "*" + downHigh;
    }
}
