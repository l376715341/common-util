package com.xinchao.tech.xinchaoad.common.util.ffmpeg;

import com.alibaba.fastjson.JSON;
import com.xinchao.tech.xinchaoad.common.constant.life.LifeConstants;
import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;
import com.xinchao.tech.xinchaoad.common.util.http.LifeUrlUtils;
import com.xinchao.tech.xinchaoad.common.util.oss.HwObsClient;
import com.xinchao.tech.xinchaoad.common.util.oss.ObsUtil;
import com.xinchao.tech.xinchaoad.common.util.oss.UploadObject;
import com.xinchao.tech.xinchaoad.common.util.oss.UploadResult;
import com.xinchao.tech.xinchaoad.common.util.security.MD5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * @author: luhanyu
 * @Date: 2018/12/19 14:52
 * @Description:
 */
@Slf4j
public class FfmpegUtil {

    private static String GENERATE_VIDEO_SHELL = "ffmpeg -y -r imgNum/duration -i orderFolder/%d.jpg -i audioUrl -t duration -c:v libx264  -vf fps=25,format=yuv420p,transpose=2,scale=1920:1080 orderFolder/out.type";
    private static String GENERATE_VIDEO_NOMUSIC_SHELL = "ffmpeg -y -r imgNum/duration  -i orderFolder/%d.jpg -t duration  -c:v libx264  -vf fps=25,format=yuv420p,transpose=2,scale=1920:1080 orderFolder/out.type";
    private static String VIDEO_CONVERT_SHELL = "ffmpeg -i video -vf fps=25,format=yuv420p,transpose=2,scale=1920:1080  -y -c:v libx264 -c:a libmp3lame -b:a 384K -acodec copy -y orderFolder/out.type";
    private static String VIDEO_IMG_SPELLS[] = {"ffmpeg", "-y", "-i", "video1", "-i", "video2", "-filter_complex",
            "[0:v]scale=size1[v1];[v1]pad=iw:ih+high2[a];[1:v]scale=size2[b];[a][b]overlay=0:high1,transpose=2", "-t", "duration", "-s", "1920*1080", "-c:v", "libx264", "orderFolder/out.type"};
    //    private static String VIDEO_AUDIO_SPELL = "ffmpeg -y -i video -i audio -flags global_header -filter_complex \\\"[0:a]aformat=sample_fmts=fltp:channel_layouts=stereo,volume=0.7[a0];[1:a]aformat=sample_fmts=fltp:channel_layouts=stereo,volume=0.9,adelay=startTime|startTime|startTime[a1];[a0][a1]amix=inputs=2:duration=first[aout]\\\" -map [aout] -ac 2 -c:v copy -map 0:v:0 orderFolder/spellOut.type";
    private static String[] VIDEO_AUDIO_SPELLS = {"ffmpeg", "-y", "-i", "video", "-i", "audio", "-flags", "global_header",
            "-filter_complex", "[0:a]aformat=sample_fmts=fltp:channel_layouts=stereo,volume=0.7[a0];[1:a]aformat=sample_fmts=fltp:channel_layouts=stereo,volume=0.9,adelay=startTime|startTime|startTime[a1];[a0][a1]amix=inputs=2:duration=first[aout]",
            "-map", "[aout]", "-ac", "2", "-c:v", "copy", "-map", "0:v:0", "orderFolder/spellOut.type"};
    private static String[] VIDEO_IMG_AUDIO_SPELLS = {"ffmpeg", "-y", "-r", "imgNum/duration", "-i", "orderFolder/%d.jpg",
            "-i", "audioUrl", "-i", "adAudioUrl", "-t", "duration", "-filter_complex",
            "[1:a]aformat=sample_fmts=fltp:channel_layouts=stereo,volume=0.7[a0];[2:a]aformat=sample_fmts=fltp:channel_layouts=stereo,volume=0.9,adelay=startTime|startTime|startTime[a1];[a0][a1]amix=inputs=2:duration=first[aout]",
            "-map", "[aout]", "-ac", "2", "-c:v", "copy", "-map", "0:v:0", "orderFolder/spellOut.type"};

    /**
     * @param creativeList    素材url列表
     * @param templateId      模板id  用于区别不同模板的素材
     * @param creativeAddress 生成视频的地址
     * @param type            生成视频类型
     * @return
     */
    public static FfRes generateVideoUrl(List<String> creativeList, long templateId, String creativeAddress, String type, Integer duration) {
        if (null == duration) {
            duration = LifeConstants.PLAY_TIME_15_SECONDS;
        }
        long startTime = System.currentTimeMillis();

        String audioUrl = null;
        String orderFolderName = creativeAddress + "/" + "template" + templateId;
        File dir = new File(orderFolderName);
        if (!dir.exists()) {
            dir.mkdir();
        }
        int imgNum = 0;
        try {
            for (int i = 0; i < creativeList.size(); i++) {
                if (creativeList.get(i).contains("mp3")) {
                    File file = new File(orderFolderName + "/audio.mp3");
                    FileUtils.copyURLToFile(new URL(creativeList.get(i)), file);
                    audioUrl = file.getPath();
                    continue;
                } else if (creativeList.get(i).contains("wav")) {
                    File file = new File(orderFolderName + "/audio.wav");
                    FileUtils.copyURLToFile(new URL(creativeList.get(i)), file);
                    audioUrl = file.getPath();
                    continue;
                } else if (creativeList.get(i).contains("jpg")) {
                    File file = new File(orderFolderName + "/" + (i + 1) + ".jpg");
                    FileUtils.copyURLToFile(new URL(creativeList.get(i)), file);
                    imgNum++;
                } else {
                    log.error("所有素材：{}", JSON.toJSONString(creativeList, true));
                    throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "素材 i 格式不符合:{}", i, creativeList.get(i));
                }
            }
        } catch (IOException e) {
            log.error("copy File fail", e);
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "copy File fail", e);
        }
        String shell;
        if (!StringUtils.isEmpty(audioUrl)) {
            shell = GENERATE_VIDEO_SHELL.replace("imgNum", String.valueOf(imgNum))
                    .replace("orderFolder", orderFolderName)
                    .replace("audioUrl", audioUrl)
                    .replace("duration", String.valueOf(duration))
                    .replace("type", type);
        } else {
            shell = GENERATE_VIDEO_NOMUSIC_SHELL.replace("imgNum", String.valueOf(imgNum))
                    .replace("orderFolder", orderFolderName)
                    .replace("duration", String.valueOf(duration))
                    .replace("type", type);
        }
        if ("mp4".equals(type)) {
            //如果是MP4  不做旋转
            shell = shell.replace("transpose=2,", "")
                    .replace("1920:1080", "1080:1920");
        }
        Process process = runShell(shell);
        File outFile = new File(orderFolderName + "/out." + type);
        log.info("generate success {},shell:{}", outFile.getPath(), shell);
        FfRes ffRes = uploadOSS(outFile, templateId, type);
        log.info("use time:{}", System.currentTimeMillis() - startTime);
        return ffRes;
    }

    public static FfRes videoConvert(ByteArrayInputStream inputStream, long orderId, String creativeAddress, String type) {
        if (LifeConstants.VIDEO_TYPE_MP4.equals(type)) {
            log.info("原始文件是MP4");
            return uploadOSS(inputStream, orderId, type);
        }
        String orderFolderName = creativeAddress + "/" + "order" + orderId;
        File dir = new File(orderFolderName);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(orderFolderName + "/video.mp4");
        inputstreamtofile(inputStream, file);
        String shell = VIDEO_CONVERT_SHELL.replace("video", file.getPath())
                .replace("orderFolder", orderFolderName)
                .replace("type", type);
        if (LifeConstants.VIDEO_TYPE_MP4.equals(type)) {
            //如果是MP4  不做旋转
            shell = shell.replace("transpose=2,", "")
                    .replace("1920:1080", "1080:1920");
        }
        Process process = runShell(shell);
        File outFile = new File(orderFolderName + "/out." + type);
        log.info("generate success {},shell:{}", outFile.getPath(), shell);
        return uploadOSS(outFile, orderId, type);
    }

    /**
     * @param url
     * @param orderId
     * @param creativeAddress
     * @param type
     * @return
     */
    public static FfRes videoConvert(String url, long orderId, String creativeAddress, String type) {
        try {
            String orderFolderName = creativeAddress + "/" + "order" + orderId;
            File dir = new File(orderFolderName);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(orderFolderName + "/video.mp4");
            FileUtils.copyURLToFile(new URL(url), file);
            if (LifeConstants.VIDEO_TYPE_MP4.equals(type)) {
                log.info("原始文件是MP4");
                return uploadOSS(new FileInputStream(file), orderId, type);
            }
            String shell = VIDEO_CONVERT_SHELL.replace("video", file.getPath())
                    .replace("orderFolder", orderFolderName)
                    .replace("type", type);
            if (LifeConstants.VIDEO_TYPE_MP4.equals(type)) {
                //如果是MP4  不做旋转
                shell = shell.replace("transpose=2,", "")
                        .replace("1920:1080", "1080:1920");
            }
            Process process = runShell(shell);
            File outFile = new File(orderFolderName + "/out." + type);
            log.info("generate success {},shell:{}", outFile.getPath(), shell);
            return uploadOSS(outFile, orderId, type);
        } catch (IOException e) {
            log.info("convert fail {}", url);
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "convert fail");
        }
    }


    private static Process runShell(String shell) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(shell);
            StringBuilder stringOut = new StringBuilder();
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while ((line = input.readLine()) != null) {
                    stringOut.append(line).append("\n");
                }
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            int code = process.waitFor();
            if (code != 0) {
                log.error(stringOut.toString());
                log.error("shell code error:{} ,shell:{}", code, shell);
            }
            process.destroy();
        } catch (Exception e) {
            log.error("vedio fail", e);
        }
        return process;
    }

    private static Process runShell(String[] shell) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(shell);
            StringBuilder stringOut = new StringBuilder();
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String line;
                while ((line = input.readLine()) != null) {
                    stringOut.append(line).append("\n");
                }
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            int code = process.waitFor();
            if (code != 0) {
                log.error(stringOut.toString());
                log.error("shell code error:{} ,shell:{}", code, shell);
            }
            process.destroy();
        } catch (Exception e) {
            log.error("vedio fail", e);
        }
        return process;
    }

    private static FfRes uploadOSS(File outFile, Long templateId, String type) {
        UploadObject uploadObject = new UploadObject();
        uploadObject.setFileName("template" + templateId + "Out." + type);
        try {
            FileInputStream inputStream = new FileInputStream(outFile);
            uploadObject.setInputStream(inputStream);
        } catch (IOException ex) {
            log.error("upload fail {}", outFile.getName(), ex);
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "无法识别的上传内容", ex);
        }
        UploadResult uploadResult = ObsUtil.upload(uploadObject, HwObsClient.class);
        String url = uploadResult.getUrl();
        log.info("video url = {}", url);
        String md5 = null;
        try {
            md5 = MD5Utils.md5(outFile);
        } catch (IOException e) {
            log.error("ffmeg md5 fail");
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "ffmeg md5 fail", e);
        }
        FfRes ffRes = new FfRes();
        ffRes.setUrl(url);
        ffRes.setMd5(md5);
        return ffRes;
    }

    private static FfRes uploadOSS(InputStream inputStream, Long templateId, String type) {
        try {
            inputStream.reset();
        } catch (Exception ex) {
            log.error("exception:", ex);
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "inputStream reset failed", ex);
        }
        UploadObject uploadObject = new UploadObject();
        uploadObject.setFileName("template" + templateId + "Out." + type);
        uploadObject.setInputStream(inputStream);
        UploadResult uploadResult = ObsUtil.upload(uploadObject, HwObsClient.class);
        String url = uploadResult.getUrl();
        log.info("video url = {}", url);
        String md5 = null;
        try {
            inputStream.reset();
            md5 = MD5Utils.md5(inputStream);
            inputStream.reset();
        } catch (IOException e) {
            log.error("ffmeg md5 fail");
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "ffmeg md5 fail", e);
        }
        FfRes ffRes = new FfRes();
        ffRes.setUrl(url);
        ffRes.setMd5(md5);
        return ffRes;
    }

    public static void inputstreamtofile(InputStream ins, File file) {
        OutputStream os = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("erro file write");
        }

    }

    /**
     * 视频流截帧
     *
     * @param inputStream
     * @param id
     * @param creativeAddress
     * @return
     * @throws Exception
     */
    public static FfRes fetchFrame(InputStream inputStream, Long id, String creativeAddress)
            throws Exception {
        long start = System.currentTimeMillis();
        String orderFolderName = creativeAddress + "/preview";
        File dir = new File(orderFolderName);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(orderFolderName + "/" + id + "video.mp4");
        inputstreamtofile(inputStream, file);
        String shell = "ffmpeg -i video -y -f image2 -ss 1 -t 0.001 -s 112x200 orderFolder/" + id + "preview.jpg";
        shell = shell.replace("video", file.getPath())
                .replace("orderFolder", orderFolderName);

        Process process = runShell(shell);
        File outFile = new File(orderFolderName + "/" + id + "preview.jpg");
        log.info("generate success {},shell:{}", outFile.getPath(), shell);
        System.out.println(shell);
        System.out.println(System.currentTimeMillis() - start);
        return uploadOSS(outFile, id, "jpg");
    }

    /**
     * 视频url截帧
     *
     * @param url
     * @param creativeAddress
     * @return 预览图url
     * @throws Exception
     */
    public static String fetchFrame(String url, String creativeAddress) throws Exception {
        long start = System.currentTimeMillis();
        String orderFolderName = creativeAddress + "/preivew";
        File dir = new File(orderFolderName);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(orderFolderName + "/video" + start + ".mp4");
        FileUtils.copyURLToFile(new URL(url), file);


        String shell = "ffmpeg -i video -y -f image2 -ss 1 -t 0.001 -s 112x200 orderFolder/preivew" + start + ".jpg";
        shell = shell.replace("video", file.getPath())
                .replace("orderFolder", orderFolderName);

        Process process = runShell(shell);
        File outFile = new File(orderFolderName + "/preivew" + start + ".jpg");
        FileInputStream inputStream = new FileInputStream(outFile);
        log.info("generate success,shell:{}", shell);

        return ObsUtil.upload(inputStream, outFile.getName());
    }

    /**
     * 根据url上下拼接视频
     *
     * @param req4url
     * @return
     */
    public static FfRes spellVideo4Url(FfSpellReq4url req4url) {
        if (null == req4url.getDuration()) {
            req4url.setDuration(LifeConstants.PLAY_TIME_15_SECONDS);
        }
        long startTime = System.currentTimeMillis();

        String video1Url = null;
        String video2Url = null;
        String orderFolderName = req4url.getCreativeAddress() + "/" + "template" + req4url.getTemplateId();
        File dir = new File(orderFolderName);
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            File video1 = new File(orderFolderName + "/video1.mp4");
            FileUtils.copyURLToFile(new URL(req4url.getUpUrl()), video1);
            video1Url = video1.getPath();
            File video2 = new File(orderFolderName + "/video2.mp4");
            FileUtils.copyURLToFile(new URL(req4url.getDownUrl()), video2);
            video2Url = video2.getPath();
        } catch (IOException e) {
            log.error("copy File fail", e);
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "copy File fail", e);
        }
        String[] shell = VIDEO_IMG_SPELLS.clone();
        shell[3] = video1Url;
        shell[5] = video2Url;
        shell[7] = shell[7].replace("size1", req4url.getUpSize())
                .replace("size2", req4url.getDownSize())
                .replace("high2", String.valueOf(req4url.getDownHigh()))
                .replace("high1", String.valueOf(req4url.getUpHigh()));
        shell[9] = String.valueOf(req4url.getDuration());
        shell[14] = shell[14].replace("orderFolder", orderFolderName)
                .replace("type", req4url.getType());
        if ("mp4".equals(req4url.getType())) {
            //如果是MP4  不做旋转
            shell[7] = shell[7].replace(",transpose=2", "");
            shell[11] = "1080*1920";
        }
        Process process = runShell(shell);
        File outFile = new File(orderFolderName + "/out." + req4url.getType());
        log.info("generate success {},shell:{}", outFile.getPath(), shell);
        FfRes ffRes = uploadOSS(outFile, req4url.getTemplateId(), req4url.getType());
        log.info("use time:{}", System.currentTimeMillis() - startTime);
        return ffRes;
    }

    /**
     * 根据inputStream 上下拼接
     *
     * @param req4InputStream
     * @return
     */
    public static FfRes spellVideo4InputStream(FfSpellReq4InputStream req4InputStream) {
        if (null == req4InputStream.getDuration()) {
            req4InputStream.setDuration(LifeConstants.PLAY_TIME_15_SECONDS);
        }
        long startTime = System.currentTimeMillis();

        String video1Url = null;
        String video2Url = null;
        String orderFolderName = req4InputStream.getCreativeAddress() + "/" + "template" + req4InputStream.getTemplateId();
        File dir = new File(orderFolderName);
        if (!dir.exists()) {
            dir.mkdir();
        }
        try {
            File video1 = new File(orderFolderName + "/video1.mp4");
            inputstreamtofile(req4InputStream.getUpInput(), video1);
            video1Url = video1.getPath();
            File video2 = new File(orderFolderName + "/video2.mp4");
            inputstreamtofile(req4InputStream.getDownInput(), video2);
            video2Url = video2.getPath();
        } catch (Exception e) {
            log.error("copy File fail", e);
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "copy File fail", e);
        }
        String[] shell = VIDEO_IMG_SPELLS.clone();
        shell[3] = video1Url;
        shell[5] = video2Url;
        shell[7] = shell[7].replace("size1", req4InputStream.getUpSize())
                .replace("size2", req4InputStream.getDownSize())
                .replace("high2", String.valueOf(req4InputStream.getDownHigh()))
                .replace("high1", String.valueOf(req4InputStream.getUpHigh()));
        shell[9] = String.valueOf(req4InputStream.getDuration());
        shell[14] = shell[14].replace("orderFolder", orderFolderName)
                .replace("type", req4InputStream.getType());
        if ("mp4".equals(req4InputStream.getType())) {
            //如果是MP4  不做旋转
            shell[7] = shell[7].replace(",transpose=2", "");
            shell[11] = "1080*1920";
        }
        Process process = runShell(shell);
        File outFile = new File(orderFolderName + "/out." + req4InputStream.getType());
        log.info("generate success {},shell:{}", outFile.getPath(), shell);
        FfRes ffRes = uploadOSS(outFile, req4InputStream.getTemplateId(), req4InputStream.getType());
        log.info("use time:{}", System.currentTimeMillis() - startTime);
        return ffRes;
    }

    /**
     * 混合音频
     *
     * @param req4url
     * @return
     */
    public static FfRes spellAudio4Url(FfSpellAudioReq4url req4url) {
        String orderFolderName = req4url.getCreativeAddress() + "/" + "template" + req4url.getTemplateId();
        String[] shell = VIDEO_AUDIO_SPELLS.clone();
        try {
            File dir = new File(orderFolderName);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File video = new File(orderFolderName + "/video.mp4");
            FileUtils.copyURLToFile(new URL(req4url.getVideoUrl()), video);
            File audio = new File(orderFolderName + "/adAudio.mp3");
            FileUtils.copyURLToFile(new URL(req4url.getAudioUrl()), audio);
            shell[3] = video.getPath();
            shell[5] = audio.getPath();
            shell[9] = shell[9].replaceAll("startTime", String.valueOf(req4url.getStartTime()));
            shell[18] = shell[18].replace("orderFolder", orderFolderName).replace("type", req4url.getType());

        } catch (IOException e) {
            log.info("spell fail {}", req4url.getVideoUrl());
            throw new BaseException(ResultCode.FAIL_DATA_WRONG.getCode(), "spell fail");
        }

        log.info("shell:{}", shell);
        Process process = runShell(shell);
        File outFile = new File(orderFolderName + "/spellOut." + req4url.getType());

        return uploadOSS(outFile, req4url.getTemplateId(), req4url.getType());
    }

    public static void main(String[] args) throws Exception {
        String[] temp = VIDEO_IMG_AUDIO_SPELLS.clone();
        for (int i = 0; i < temp.length; i++) {
            System.out.println(i + "--" + temp[i]);
        }
    }
}
