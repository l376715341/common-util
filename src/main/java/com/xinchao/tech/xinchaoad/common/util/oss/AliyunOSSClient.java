package com.xinchao.tech.xinchaoad.common.util.oss;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.xinchao.tech.xinchaoad.common.util.PropertiesLoadUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.Date;
import java.util.Properties;

@Slf4j
@Data
@Deprecated
public class AliyunOSSClient implements OSSClient {

    public static final String CONFIG_PATH = "classpath:oss.properties";

    String endPoint;
    String accessKeyId;
    String accessKeySecret;
    public String bucketName;
    String baseUrl;

    boolean inited = false;
    public OSS oss;

    public AliyunOSSClient() {
        Properties properties = PropertiesLoadUtil.loadAsProperties(CONFIG_PATH);
        if (properties != null) {
            endPoint = properties.getProperty("oss.aliyun.endpoint");
            accessKeyId = properties.getProperty("oss.aliyun.accessKeyId");
            accessKeySecret = properties.getProperty("oss.aliyun.accessKeySecret");
            bucketName = properties.getProperty("oss.aliyun.bucket");
            baseUrl = properties.getProperty("oss.aliyun.baseUrl");
            if (StringUtils.isNoneBlank(endPoint) && StringUtils.isNoneBlank(accessKeyId) && StringUtils.isNoneBlank(accessKeySecret) && StringUtils.isNoneBlank(bucketName) && StringUtils.isNoneBlank(baseUrl)) {
                oss = new com.aliyun.oss.OSSClient(endPoint, accessKeyId, accessKeySecret);
                inited = true;
            }
        }
    }

    @Override
    public UploadResult upload(UploadObject uploadObject) {
        if (!inited) {
            return UploadResult.fail("OSS客户端初始化失败");
        }
        if (uploadObject == null || StringUtils.isBlank(uploadObject.getFileName()) || uploadObject.getInputStream() == null) {
            return UploadResult.fail("上传信息残缺");
        }
        if (uploadObject.getFileName().endsWith(".jpg") || uploadObject.getFileName().endsWith(".JPG")) {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType("image/jpg");
            oss.putObject(StringUtils.isBlank(uploadObject.getBucketName()) ? bucketName : uploadObject.getBucketName(), uploadObject.getFileName(), uploadObject.getInputStream(), meta);
        } else if (uploadObject.getFileName().endsWith(".mp3") || uploadObject.getFileName().endsWith(".MP3")) {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType("audio/mp3");
            oss.putObject(StringUtils.isBlank(uploadObject.getBucketName()) ? bucketName : uploadObject.getBucketName(), uploadObject.getFileName(), uploadObject.getInputStream(), meta);
        } else {
            oss.putObject(StringUtils.isBlank(uploadObject.getBucketName()) ? bucketName : uploadObject.getBucketName(), uploadObject.getFileName(), uploadObject.getInputStream());
        }// 设置URL过期时间为10年 3600l* 1000*24*365*10
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10);
        // 生成URL
        return UploadResult.success(baseUrl + "/" + uploadObject.getFileName());
    }

    @Override
    protected void finalize() throws Throwable {
        oss.shutdown();
    }

    @Override
    public URL getImageWithStyle(String key, String style) {
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 10);
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, key, HttpMethod.GET);
        req.setExpiration(expiration);
        req.setProcess(style);
        URL signedUrl = oss.generatePresignedUrl(req);
        return signedUrl;
    }

    @Override
    public void deleteObject(String fileName) {
        oss.deleteObject(bucketName, fileName);
    }
}
