package com.xinchao.tech.xinchaoad.common.util.ffmpeg;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: luhanyu
 * @Date: 2019/9/20 10:15
 * @Description:
 */
@Data
@NoArgsConstructor
public class FfSpellAudioReq4url {

    private String videoUrl;
    private String audioUrl;
    /**
     * 毫秒数
     */
    private Integer startTime;
    private Long templateId;
    private String creativeAddress;
    private String type;

    public FfSpellAudioReq4url(String videoUrl, String audioUrl, Integer startTime, Long templateId, String creativeAddress, String type) {
        this.videoUrl = videoUrl;
        this.audioUrl = audioUrl;
        this.startTime = startTime;
        this.templateId = templateId;
        this.creativeAddress = creativeAddress;
        this.type = type;
    }
}
