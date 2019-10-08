package com.xinchao.tech.xinchaoad.common.util.baidu;

import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import lombok.NoArgsConstructor;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;

/**
 * @Author: RobertSean
 * @Date: 2019/9/19 10:40
 */
@NoArgsConstructor
public class TtsUtil {
    //  填写网页上申请的appkey 如 $apiKey="g8eBUMSokVB1BHGmgxxxxxx"
    private String appKey = "IOrNnPFVXIkATbb9j5sTXX6a";
    // 填写网页上申请的APP SECRET 如 $secretKey="94dc99566550d87f8fa8ece112xxxxx"
    private String secretKey = "Negyi9hjyAisTLHvWNozKmDIifEFQl7b";
    // 下载的文件格式, 3：mp3(default) 4： pcm-16k 5： pcm-8k 6. wav,默认为mp3
    private final int aue = 3;

    private String cuid = "c95209712d11";

    private String url = "http://tsn.baidu.com/text2audio"; //可以使用https

    TtsUtil(String appKey, String secretKey, String url){
        this.appKey = appKey;
        this.secretKey = secretKey;
        this.url = url;
    }

    public ByteArrayInputStream tts(String text,int per,int spd,int pit,int vol) throws IOException, BaseException{
        TokenHolder holder = new TokenHolder(appKey, secretKey, TokenHolder.TTS_SCOPE);
        holder.refresh();
        String token = holder.getToken();

        // 此处2次urlencode， 确保特殊字符被正确编码
        String params = "tex=" + URLEncoder.encode(URLEncoder.encode(text,"UTF-8"),"UTF-8");
        params += "&per=" + per;
        params += "&spd=" + spd;
        params += "&pit=" + pit;
        params += "&vol=" + vol;
        params += "&cuid=" + cuid;
        params += "&tok=" + token;
        params += "&aue=" + aue;
        params += "&lan=zh&ctp=1";
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
        printWriter.write(params);
        printWriter.close();
        String contentType = conn.getContentType();
        if (contentType.contains("audio/")) {
            byte[] bytes = ConnUtil.getResponseBytes(conn);
            return new ByteArrayInputStream(bytes);
        } else {
            return null;
        }
    }

    // 下载的文件格式, 3：mp3(default) 4： pcm-16k 5： pcm-8k 6. wav

    /**
     * 获取文件格式
     * @param aue
     * @return
     */
    private String getFormat(int aue) {
        String[] formats = {"mp3", "pcm", "pcm", "wav"};
        return formats[aue - 3];
    }

    public static void main(String[] args) throws IOException, BaseException{
        // 发音人选择, 基础音库：0为度小美，1为度小宇，3为度逍遥，4为度丫丫，
        // 精品音库：5为度小娇，103为度米朵，106为度博文，110为度小童，111为度小萌，默认为度小美
        int per = 1;
        // 语速，取值0-15，默认为5中语速
        int spd = 5;
        // 音调，取值0-15，默认为5中语调
        int pit = 5;
        // 音量，取值0-9，默认为5中音量
        int vol = 5;
        TtsUtil ttsUtil = new TtsUtil();
        ByteArrayInputStream inputStream = ttsUtil.tts("欢迎使用百度语音，这是一个测试用的20字广告语",per,spd,pit,vol);
        String format = ttsUtil.getFormat(ttsUtil.aue);
        String path = "D:\\result." + format; // 打开mp3文件即可播放
        FileUtil.writeToLocal(path,inputStream);
    }
}
