package com.xinchao.tech.xinchaoad.common.util.oss;

import lombok.Getter;
import lombok.Setter;

public class UploadResult {

    @Getter @Setter Boolean                     success;
    @Getter @Setter String                      url;

    @Getter @Setter String                      description;

    public static UploadResult fail(String message){
        UploadResult result=new UploadResult();
        result.success=false;
        result.description=message;
        return result;
    }

    public static UploadResult success(String url){
        UploadResult result=new UploadResult();
        result.success=true;
        result.setUrl(url);
        return result;
    }

}
