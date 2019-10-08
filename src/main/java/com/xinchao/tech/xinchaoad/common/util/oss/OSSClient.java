package com.xinchao.tech.xinchaoad.common.util.oss;

import java.net.URL;

public interface OSSClient {

    UploadResult upload(UploadObject uploadObject);


    URL getImageWithStyle(String key, String style);

    void deleteObject(String fileName);
}
