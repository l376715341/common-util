package com.xinchao.tech.xinchaoad.common.util.tencentcloud;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import com.tencentcloudapi.ocr.v20181119.models.BizLicenseOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.BizLicenseOCRResponse;
import com.xinchao.tech.xinchaoad.common.util.Base64Util;
import lombok.NoArgsConstructor;

/**
 * @Author: RobertSean
 * @Date: 2019/9/19 14:17
 */
@NoArgsConstructor
public class BizLicenseOCRUtil {
    public BizLicenseOCRResponse regnizeBizLicense(String img){
        return this.regnizeBizLicense(img,0);
    }

    private String secretId = "AKIDEyEG2ea83n26v2qleUDCzYu40hARIjSv";

    private String secretKey = "QdrjO8MOEqENPLO55xSvY3FF7wWqtfDP";

    private String endpoint = "ocr.tencentcloudapi.com";

    private String region = "ap-shanghai";

    private BizLicenseOCRUtil(String secretId,String secretKey,String endpoint,String region){
        this.secretId = secretId;
        this.secretKey = secretKey;
        this.endpoint = endpoint;
        this.region = region;
    }

    public BizLicenseOCRResponse regnizeBizLicense(String img,int type){
        try{
            Credential cred = new Credential(secretId, secretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(endpoint);
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            OcrClient client = new OcrClient(cred, region, clientProfile);
            StringBuilder paramsSb = new StringBuilder();
            switch (type){
                case 1:
                    paramsSb .append("{\"ImageUrl\":\"");
                    paramsSb.append(img);
                    paramsSb.append("\"}");
                    break;
                case 0:
                default:
                    paramsSb.append("{\"ImageBase64\":\"");
                    paramsSb.append(img);
                    paramsSb.append("\"}");
                    break;
            }
            BizLicenseOCRRequest req = BizLicenseOCRRequest.fromJsonString(paramsSb.toString(), BizLicenseOCRRequest.class);
            BizLicenseOCRResponse resp = client.BizLicenseOCR(req);
            return resp;
        } catch (TencentCloudSDKException e) {
            return null;
        }
    }

    public static void main(String [] args){
        BizLicenseOCRUtil bizLicenseOCRUtil = new BizLicenseOCRUtil();
        String imageBase64 = Base64Util.getImageBinary("C:\\Users\\xiaoj\\Downloads\\business_licence_chinese_082019.jpg");
        BizLicenseOCRResponse res = bizLicenseOCRUtil.regnizeBizLicense(imageBase64);
    }
}
