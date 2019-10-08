package com.xinchao.tech.xinchaoad.common.util.oss;

import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Base64;

/**
 * @Author Li Hui
 * @Date 2018/10/17 17:26
 */

@Slf4j
public class FileUploadUtil {
    private static final long IMG_MAX_SIZE = 1024 * 1024 * 2;
    private static final long MUSIC_MAX_SIZE = 1024 * 1024;
    private static final int IMG_WIDTH = 1080;
    private static final int IMG_HEIGHT = 1920;
    private static final int BASE64_MAX_SIZE = 10 * 1024 * 1024;

    public static String uploadImg2Oss4Base64(String baseStr, String fileName) {
        if (StringUtils.isEmpty(baseStr)) {
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "无效的图片上传");
        }
        byte[] fileByte = Base64.getDecoder().decode(baseStr);
        byte[] header = new byte[8];
        header = Arrays.copyOfRange(fileByte, 0, 8);
        String fileType = getTypeByStream(header);
        if (!"jpg".equals(fileType)) {
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "无效的图片格式:{}", fileType);
        }

        if (fileByte.length > IMG_MAX_SIZE) {
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "文件过大，当前允许最大容量：{} 文件大小：{}", IMG_MAX_SIZE, fileByte.length);
        }
        InputStream inputStream = new ByteArrayInputStream(fileByte);
        BufferedImage image = null;
        try {
            image = ImageIO.read(inputStream);
            inputStream.reset();
        } catch (IOException e) {
            log.error("io 异常", e);
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "系统错误");
        }
        if (image.getWidth() != IMG_WIDTH || image.getHeight() != IMG_HEIGHT) {
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "图片尺寸不匹配：{}*{} 图片大小：{}*{}", IMG_WIDTH, IMG_HEIGHT, image.getWidth(), image.getHeight());
        }
        String url = upload(inputStream, fileName + "." + fileType);
        return url;
    }

    public static String uploadFile(File file, String fileName) {
        UploadObject uploadObject = new UploadObject();
        uploadObject.setFileName(fileName);
        try {
            uploadObject.setInputStream(new FileInputStream(file) {
            });
        } catch (Exception ex) {
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "无法识别的上传内容", ex);
        }
        UploadResult uploadResult = ObsUtil.upload(uploadObject, HwObsClient.class);
        String url = uploadResult.getUrl();
        log.info("image url = {}", url);
        return url;
    }

    public static String upload(InputStream inputStream, String fileName) {
        UploadObject uploadObject = new UploadObject();
        uploadObject.setFileName(fileName);
        try {
            uploadObject.setInputStream(inputStream);
        } catch (Exception ex) {
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "无法识别的上传内容", ex);
        }
        UploadResult uploadResult = ObsUtil.upload(uploadObject, HwObsClient.class);
        String url = uploadResult.getUrl();
        log.info("image url = {}", url);
        return url;
    }

    public static String uploadFile2Oss4Base64(String baseStr, String fileName) {
        if (StringUtils.isEmpty(baseStr)) {
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "无效的文件上传");
        }
        byte[] fileByte = Base64.getDecoder().decode(baseStr);
        byte[] header = new byte[64];
        header = Arrays.copyOfRange(fileByte, 0, 64);
        String fileType = getTypeByStream(header);
        if (fileByte.length > BASE64_MAX_SIZE) {
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "文件过大，当前允许最大容量：{} 文件大小：{}", MUSIC_MAX_SIZE, fileByte.length);
        }
        InputStream inputStream = new ByteArrayInputStream(fileByte);
        String url = ObsUtil.upload(inputStream, fileName + "." + fileType);
        return url;
    }

    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    private static String getTypeByStream(byte[] fileTypeByte) {

        String type = bytesToHexString(fileTypeByte).toUpperCase();
        if (type.contains("FFD8FF")) {
            return "jpg";
        } else if (type.contains("89504E47")) {
            return "png";
        } else if (type.contains("47494638")) {
            return "gif";
        } else if (type.contains("49492A00")) {
            return "tif";
        } else if (type.contains("424D")) {
            return "bmp";
        } else if (type.contains("57415645")) {
            return "wav";
        } else {
            return null;
        }
    }
}
