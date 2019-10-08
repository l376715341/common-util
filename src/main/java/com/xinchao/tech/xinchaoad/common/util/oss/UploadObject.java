package com.xinchao.tech.xinchaoad.common.util.oss;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

@Data
public class UploadObject {

    @Getter @Setter String                  fileName;

    @Getter @Setter String                  bucketName;
    @Getter @Setter InputStream             inputStream;

}
