package com.xinchao.tech.xinchaoad.common.util.baidu;

import com.alibaba.fastjson.JSONObject;
import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;
import com.xinchao.tech.xinchaoad.common.util.http.HttpClient;
import com.xinchao.tech.xinchaoad.common.util.http.HttpResult;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * token的获取类
 * 将apiKey和secretKey换取token，注意有效期保存在expiresAt
 */
public class TokenHolder {


    public static final String ASR_SCOPE = "audio_voice_assistant_get";

    public static final String TTS_SCOPE = "audio_tts_post";

    public static final String OCR_SCOPE = "brain_ocr_business_license";

    /**
     * URL , Token的url，http可以改为https
     */
    private static final String URL = "http://openapi.baidu.com/oauth/2.0/token";

    /**
     * asr的权限 scope 是  "audio_voice_assistant_get"
     * tts 的权限 scope 是 "audio_tts_post"
     */
    private String scope;

    /**
     * 网页上申请语音识别应用获取的apiKey
     */
    private String apiKey;

    /**
     * 网页上申请语音识别应用获取的secretKey
     */
    private String secretKey;

    /**
     * 保存访问接口获取的token
     */
    private String token;

    /**
     * 当前的时间戳，毫秒
     */
    private long expiresAt;

    /**
     * @param apiKey    网页上申请语音识别应用获取的apiKey
     * @param secretKey 网页上申请语音识别应用获取的secretKey
     */
    public TokenHolder(String apiKey, String secretKey, String scope) {
        this.apiKey = apiKey;
        this.secretKey = secretKey;
        this.scope = scope;
    }

    /**
     * 获取token，refresh 方法后调用有效
     *
     * @return
     */
    public String getToken() {
        return token;
    }

    /**
     * 获取过期时间，refresh 方法后调用有效
     *
     * @return
     */
    public long getExpiresAt() {
        return expiresAt;
    }
    /**
     * 获取token
     *
     * @return
     * @throws IOException   http请求错误
     */
    public void refresh() throws IOException {
        HttpClient httpClient = new HttpClient();
        String getTokenURL = URL + "?grant_type=client_credentials"
                + "&client_id=" + URLEncoder.encode(apiKey,"UTF-8") + "&client_secret=" + URLEncoder.encode(secretKey,"UTF-8");
        HttpResult httpResult = httpClient.get(getTokenURL,null,"UTF-8");
        String result = httpResult.getResponseBody();
        parseJson(result);
    }

    /**
     * @param result token接口获得的result
     */
    private void parseJson(String result){
        JSONObject json = JSONObject.parseObject(result);
        String accessToken = json.getString("access_token");
        if (StringUtils.isBlank(accessToken)) {
            // 返回没有access_token字段
            throw new BaseException(ResultCode.FAIL_DEPENDENCY_CHECK.getCode(), "获取access_token 失败 ");
        }
        String scopeObtain = json.getString("scope");
        if (StringUtils.isBlank(scopeObtain)) {
            // 返回没有scope字段
            throw new BaseException(ResultCode.FAIL_DEPENDENCY_CHECK.getCode(), "获取scope 失败 ");
        }
        if (!scopeObtain.contains(scope)) {
            throw new BaseException(ResultCode.FAIL_DEPENDENCY_CHECK.getCode(), "scope 不匹配");
        }
        token = accessToken;
        expiresAt = System.currentTimeMillis() + json.getLong("expires_in") * 1000;
    }
}
