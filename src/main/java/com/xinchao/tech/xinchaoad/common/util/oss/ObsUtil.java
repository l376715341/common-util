package com.xinchao.tech.xinchaoad.common.util.oss;

import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;
import com.xinchao.tech.xinchaoad.common.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Li Hui
 * @Date 2019/5/28 16:13
 **/

@Slf4j
public class ObsUtil {


    protected static ConcurrentHashMap<Class<?>, OSSClient>
            cache = new ConcurrentHashMap<>();

    public static UploadResult upload(UploadObject uploadObject, Class<? extends OSSClient> clazz) {
        if (clazz == null || uploadObject == null || StringUtils.isBlank(uploadObject.getFileName()) || uploadObject.getInputStream() == null) {
            return UploadResult.fail("参数校验失败");
        }
        OSSClient ossClient = getObsClient(clazz);
        if (ossClient == null) {
            return UploadResult.fail(String.format("构建OSS客户端失败，type:%s", clazz.getName()));
        }
        return ossClient.upload(uploadObject);
    }

    protected static OSSClient getObsClient(Class<? extends OSSClient> clazz) {
        OSSClient result = cache.get(clazz);
        if (result == null) {
            synchronized (ObsUtil.class) {
                result = cache.get(clazz);
                if (result == null) {
                    try {
                        result = clazz.newInstance();
                        cache.put(clazz, result);
                    } catch (Exception ex) {
                        log.error("build oss client err,clazz:{}", clazz.getName(), ex);
                    }
                }
            }
        }
        return result;
    }

    public URL getImageWithStyle(String key, String style, Class<? extends OSSClient> clazz) {
        if (clazz == null || StringUtils.isAllEmpty(key, style)) {
            return null;
        }
        OSSClient ossClient = getObsClient(clazz);
        URL url = ossClient.getImageWithStyle(key, style);
        return url;
    }

    /**
     * oss上传
     *
     * @param inputStream
     * @param fileName
     * @return
     */
    public static String upload(InputStream inputStream, String fileName) {
        UploadObject uploadObject = new UploadObject();
        uploadObject.setFileName(fileName);
        try {
            uploadObject.setInputStream(inputStream);
        } catch (Exception e) {
            throw new BaseException(ResultCode.FAIL_UNKNOWN);
        }
        UploadResult uploadResult = ObsUtil.upload(uploadObject, HwObsClient.class);
        String url = uploadResult.getUrl();
        return url;
    }

    /**
     * oss上传
     *
     * @param inputStream
     * @param fileName
     * @return
     */
    public static String uploadUUID(InputStream inputStream, String fileName) {
        UploadObject uploadObject = new UploadObject();
        uploadObject.setFileName(UUIDUtil.generateUUID() + fileName.substring(fileName.lastIndexOf(".")));
        try {
            uploadObject.setInputStream(inputStream);
        } catch (Exception e) {
            throw new BaseException(ResultCode.FAIL_UNKNOWN);
        }
        UploadResult uploadResult = ObsUtil.upload(uploadObject, HwObsClient.class);
        String url = uploadResult.getUrl();
        url = getRealObsUrl(url);
        return url;
    }

    public static String uploadFile(MultipartFile file) {
        String url;
        try (InputStream inputStream = file.getInputStream()) {
            url = ObsUtil.uploadUUID(inputStream, file.getOriginalFilename());
        } catch (Exception ex) {
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), ex.getMessage());
        }

        return url;
    }

    public static String getRealObsUrl(String ossUrl) {
        int index = ossUrl.length();
        if (ossUrl.contains("?")) {
            index = ossUrl.indexOf("?");
        }
        return ossUrl.substring(0, index);
    }


    public static String getOSSUrlPreview(String ossUrl) {
        if (ossUrl.contains(".mp4")) {
            return ossUrl + "?x-oss-process=video/snapshot,t_1000,f_jpg,h_300,m_fast";
        } else if (ossUrl.contains(".jpg") || ossUrl.contains(".png")) {
            return ossUrl + "?x-oss-process=image/resize,h_300";
        } else {
            return ossUrl;
        }
    }

    public static void deleteFile(String ossFullUrl) {
        String fileName = ossFullUrl.substring(ossFullUrl.lastIndexOf("/") + 1);
        OSSClient ossClient = getObsClient(HwObsClient.class);
        ossClient.deleteObject(fileName);
    }
}
