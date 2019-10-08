package com.xinchao.tech.xinchaoad.common.util.oss;

import com.obs.services.ObsClient;
import com.obs.services.model.ObjectMetadata;
import com.xinchao.tech.xinchaoad.common.util.PropertiesLoadUtil;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.Properties;

/**
 * @Author Li Hui
 * @Date 2019/5/27 17:31
 **/


public class HwObsClient implements OSSClient {
    public static final String CONFIG_PATH = "classpath:obs.properties";

    private String endPoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;
    private String baseUrl;

    private boolean initiated = false;

    private ObsClient obs;

    public HwObsClient() {
        Properties properties = PropertiesLoadUtil.loadAsProperties(CONFIG_PATH);
        if (properties != null) {
            endPoint = properties.getProperty("obs.huawei.endpoint");
            accessKeyId = properties.getProperty("obs.huawei.accessKeyId");
            accessKeySecret = properties.getProperty("obs.huawei.accessKeySecret");
            bucketName = properties.getProperty("obs.huawei.bucket");
            baseUrl = properties.getProperty("obs.huawei.baseUrl");
            if (StringUtils.isNoneBlank(endPoint) && StringUtils.isNoneBlank(accessKeyId) && StringUtils.isNoneBlank(accessKeySecret) && StringUtils.isNoneBlank(bucketName) && StringUtils.isNoneBlank(baseUrl)) {
                obs = new ObsClient(accessKeyId, accessKeySecret, endPoint);
                initiated = true;
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        obs.close();
    }

    @Override
    public UploadResult upload(UploadObject uploadObject) {
        if (!initiated) {
            return UploadResult.fail("OBS客户端初始化失败");
        }
        if (uploadObject == null || StringUtils.isBlank(uploadObject.getFileName()) || uploadObject.getInputStream() == null) {
            return UploadResult.fail("上传信息残缺");
        }
        String validBucketName = StringUtils.isBlank(uploadObject.getBucketName()) ? bucketName : uploadObject.getBucketName();
        if (uploadObject.getFileName().toLowerCase().endsWith(".jpg")) {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType("image/jpg");
            obs.putObject(validBucketName, uploadObject.getFileName(), uploadObject.getInputStream(), meta);
        } else if (uploadObject.getFileName().toLowerCase().endsWith(".mp3")) {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType("audio/mp3");
            obs.putObject(validBucketName, uploadObject.getFileName(), uploadObject.getInputStream(), meta);
        } else {
            obs.putObject(validBucketName, uploadObject.getFileName(), uploadObject.getInputStream());
        }
        // 生成URL
        return UploadResult.success(baseUrl + "/" + uploadObject.getFileName());
    }

    @Override
    public URL getImageWithStyle(String key, String style) {
        return null;
    }

    @Override
    public void deleteObject(String fileName) {
        obs.deleteObject(bucketName, fileName);
    }
}
