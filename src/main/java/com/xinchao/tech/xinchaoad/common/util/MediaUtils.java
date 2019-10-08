package com.xinchao.tech.xinchaoad.common.util;

import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author jianghaiqiang
 * @date 2018/12/26
 */

@Slf4j
public class MediaUtils {

    // 图片
    private static int IMAGE_MAX_SIZE = 1024 * 1024 * 5;
    private static String IMAGE_SUPPORT_TYPE = "jpg,png";
    // 音频
    private static int AUDIO_MAX_SIZE = 1024 * 1024 * 1;
    private static String AUDIO_SUPPORT_TYPE = "mp3,wav";
    // 视频
    private static int VIDEO_MAX_SIZE = 1024 * 1024 * 10;
    private static String VIDEO_SUPPORT_TYPE = "avi,mp4,mov";

    /**
     * 校验图片大小
     *
     * @param inputStream
     * @return
     */
    public static void checkImageSize(InputStream inputStream) {
        int size = 0;
        try {
            size = inputStream.available();
        } catch (IOException e) {
            throw new BaseException(ResultCode.FAIL_UNKNOWN);
        }
        if (size > IMAGE_MAX_SIZE) {
            throw new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(), "图片过大");
        }
    }

    /**
     * 校验图片分辨率
     *
     * @param inputStream
     * @return
     */
    public static void checkImageResolution(InputStream inputStream, int width, int height) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new BaseException(ResultCode.FAIL_UNKNOWN);
        }
        if (image.getWidth() != width || image.getHeight() != height) {
            log.error("输入图片分辨率不匹配 width = {},height = {}", image.getWidth(), image.getHeight());
            throw new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(), "图片分辨率不匹配");
        }
    }

    /**
     * 校验图片格式
     *
     * @param originalFileName
     * @return
     */
    public static void checkImageType(String originalFileName) {
        String suffix = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        if (!IMAGE_SUPPORT_TYPE.contains(suffix.trim().toLowerCase())) {
            throw new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(), "不支持的图片格式");
        }
    }

    /**
     * 校验视频大小
     *
     * @param inputStream
     * @return
     */
    public static void checkVideoSize(InputStream inputStream) {
        int size = 0;
        try {
            size = inputStream.available();
        } catch (IOException e) {
            throw new BaseException(ResultCode.FAIL_UNKNOWN);
        }
        if (size > VIDEO_MAX_SIZE) {
            throw new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(), "视频过大");
        }
    }

    /**
     * 校验视频分辨率
     *
     * @param inputStream
     * @return
     */
    public static void checkVideoResolution(InputStream inputStream, String suffix, int width, int height) throws IOException {
        // 写文件
        int index;
        byte[] bytes = new byte[1024];
        FileOutputStream downloadFile = null;
        String filePath = UUIDUtil.generateUUID() + "." + suffix;
        File file = new File(filePath);
        downloadFile = new FileOutputStream(file);
        while ((index = inputStream.read(bytes)) != -1) {
            downloadFile.write(bytes, 0, index);
        }
        inputStream.close();
        downloadFile.flush();
        downloadFile.close();

        // 读文件、校验
        IContainer container = IContainer.make();
        container.open(filePath, IContainer.Type.READ, null);
        try {
            int numStreams = container.getNumStreams();
            for (int i = 0; i < numStreams; i++) {
                IStream stream = container.getStream(i);
                IStreamCoder coder = stream.getStreamCoder();
                if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
                    if (coder.getWidth() != width || coder.getHeight() != height) {
                        throw new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(), "视频分辨率不匹配");
                    }
                    break;
                }
            }
        } finally {
            container.close();
            if (file.exists() && file.isFile()) {
                file.delete();
            }
        }
    }

    /**
     * 校验视频格式
     *
     * @param originalFileName
     * @return
     */
    public static void checkVideoType(String originalFileName) {
        String suffix = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        if (!VIDEO_SUPPORT_TYPE.contains(suffix.trim().toLowerCase())) {
            throw new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(), "不支持的视频格式");
        }
    }

    /**
     * 校验音频大小
     *
     * @param inputStream
     * @return
     */
    public static void checkAudioSize(InputStream inputStream) {
        int size = 0;
        try {
            size = inputStream.available();
        } catch (IOException e) {
            throw new BaseException(ResultCode.FAIL_UNKNOWN);
        }
        if (size > AUDIO_MAX_SIZE) {
            throw new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(), "音频过大");
        }
    }

    /**
     * 校验音频格式
     *
     * @param originalFileName
     * @return
     */
    public static void checkAudioType(String originalFileName) {
        String suffix = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        if (!AUDIO_SUPPORT_TYPE.contains(suffix.trim().toLowerCase())) {
            throw new BaseException(ResultCode.FAIL_ILLEGAL_ARGUMENT.getCode(), "不支持的音频格式");
        }
    }

    /**
     * 获取文件名
     *
     * @param id
     * @param partFileName
     * @param originalFileName
     * @return
     */
    public static String getFileName(Long id, String partFileName, String originalFileName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MATERIAL")
                .append("_")
                .append(id)
                .append("_")
                .append(UUIDUtil.generateUUID())
                .append("_")
                .append(partFileName.toUpperCase())
                .append(originalFileName.substring(originalFileName.lastIndexOf(".")));
        return stringBuilder.toString();
    }

    /**
     * 生成oss name
     * @param id
     * @param partFileName
     * @return
     */
    public static String generateName(Long id, String partFileName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("MATERIAL")
                .append("_")
                .append(id)
                .append("_")
                .append(UUIDUtil.generateUUID())
                .append("_")
                .append(partFileName);
        return stringBuilder.toString();
    }

    /**
     * 获取文件名
     *
     * @param id
     * @param partFileName
     * @param suffix
     * @return
     */
    public static String getVideoFileName(Long id, String partFileName, String suffix) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("TEMPLATE")
                .append("_")
                .append(id)
                .append("_")
                .append(partFileName.toUpperCase())
                .append(".")
                .append(suffix);
        return stringBuilder.toString();
    }
}
