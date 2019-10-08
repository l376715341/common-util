package com.xinchao.tech.xinchaoad.common.util.baidu;

import com.alibaba.fastjson.JSONObject;
import com.xinchao.tech.xinchaoad.common.exception.BaseException;
import com.xinchao.tech.xinchaoad.common.exception.ResultCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: RobertSean
 * @Date: 2019/9/20 17:09
 */
@NoArgsConstructor
@Slf4j
public class OctUtil {
    //  填写网页上申请的appkey 如 $apiKey="g8eBUMSokVB1BHGmgxxxxxx"
    private static String appKey = "V9GaBvngA1Opvxlrjerbmrr6";
    // 填写网页上申请的APP SECRET 如 $secretKey="94dc99566550d87f8fa8ece112xxxxx"
    private static String secretKey = "Ewa6ohqNwFsbl2QfikqA9fA61HYdu5vg";
    //OCR接口url
    private static String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/business_license";

    public static OcrResult regnizeBizLicense(String base64Raw) throws IOException,BaseException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        TokenHolder holder = new TokenHolder(appKey, secretKey, TokenHolder.OCR_SCOPE);
        holder.refresh();
        String token = holder.getToken();
        String fullUrl = url + "?access_token=" + token;
        HttpPost httpPost = new HttpPost(fullUrl);
        Header header = new BasicHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.addHeader(header);
        // 创建参数队列
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        // 添加参数
        formparams.add(new BasicNameValuePair("image", base64Raw));
        formparams.add(new BasicNameValuePair("accuracy", "high"));
        UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
        httpPost.setEntity(uefEntity);
        // execute request
        HttpResponse httpResponse = httpClient.execute(httpPost);
        StatusLine statusLine = httpResponse.getStatusLine();
        if(null == statusLine || 200 != statusLine.getStatusCode()){
            throw new BaseException(ResultCode.FAIL_HTTP_REQ.getCode(),"调用百度OCR接口失败,status code不为200");
        }
        HttpEntity httpEntity = httpResponse.getEntity();
        InputStream inputStream = httpEntity.getContent();
        String response = new String(ConnUtil.read(inputStream, 1024), "UTF-8").trim();
        JSONObject fullRes = JSONObject.parseObject(response);
        if(null != fullRes){
            OcrResult result = new OcrResult();
            JSONObject wordsResult = fullRes.getJSONObject("words_result");
            log.info("ocrResult:" + wordsResult.toJSONString());
            JSONObject addressResult = wordsResult.getJSONObject("地址");
            result.setAddress(addressResult.getString("words"));
            JSONObject businessResult = wordsResult.getJSONObject("经营范围");
            result.setBusiness(businessResult.getString("words"));
            JSONObject capitalResult = wordsResult.getJSONObject("注册资本");
            result.setCapital(capitalResult.getString("words"));
            JSONObject composingFormResult = wordsResult.getJSONObject("组成形式");
            result.setComposingForm(composingFormResult.getString("words"));
            JSONObject nameResult = wordsResult.getJSONObject("单位名称");
            result.setName(nameResult.getString("words"));
            JSONObject personResult = wordsResult.getJSONObject("法人");
            result.setPerson(personResult.getString("words"));
            JSONObject periodEndResult = wordsResult.getJSONObject("有效期");
            result.setPeriodEnd(periodEndResult.getString("words"));
            JSONObject periodStartResult = wordsResult.getJSONObject("成立日期");
            result.setPeriodStart(periodStartResult.getString("words"));
            JSONObject regNumResult = wordsResult.getJSONObject("社会信用代码");
            result.setRegNum(regNumResult.getString("words"));
            JSONObject typeResult = wordsResult.getJSONObject("类型");
            result.setType(typeResult.getString("words"));
            return result;
        }
        return null;
    }

}
