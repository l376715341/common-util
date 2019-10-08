package com.xinchao.tech.xinchaoad.common.util;

import com.alibaba.fastjson.JSONObject;
import com.xinchao.tech.xinchaoad.common.util.http.HttpClient;
import com.xinchao.tech.xinchaoad.common.util.http.HttpResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import javax.servlet.http.HttpServletRequest;


@Slf4j
public class IPUtils {

    private static String URL = "http://ip.taobao.com/service/getIpInfo.php?ip=";

    private static String UNKNOWN = "unknown";

    private static String TXURL = "https://apis.map.qq.com/ws/location/v1/ip?key=VHOBZ-JX7WK-DJ5J2-AC3BO-3L53E-76FKP&ip=";

    public static String getCityCodeByIP(String ip) {
        if (StringUtils.isBlank(ip)){
            return null;
        }
        try {
            String requestURL = String.format("%s%s", URL , ip);
            HttpClient httpClient = new HttpClient();
            HttpResult result = httpClient.get(requestURL, null, "UTF-8");
            if (result.getStatus() == 200) {
                JSONObject jsonObject = JSONObject.parseObject(result.getResponseBody());
                return jsonObject.getJSONObject("data").getString("city_id");
            } else {
                return getTXCityCodeByIp(ip);
            }
        } catch (Exception e) {
            log.error("getCityCodeByIP ip:[{}] is Exception:{}", ip, e.toString());
        }
        return null;
    }

    public static String getIPFromHttpServlet(HttpServletRequest request) {
        if (null == request) {
            return null;
        }
        String ip = null;
        try {
            String ipAddresses = request.getHeader("X-Forwarded-For");
            if (ipAddresses == null || ipAddresses.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddresses)) {
                //Proxy-Client-IP：apache 服务代理
                ipAddresses = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddresses == null || ipAddresses.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddresses)) {
                //WL-Proxy-Client-IP：weblogic 服务代理
                ipAddresses = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddresses == null || ipAddresses.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddresses)) {
                //HTTP_CLIENT_IP：有些代理服务器
                ipAddresses = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ipAddresses == null || ipAddresses.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddresses)) {
                //X-Real-IP：nginx服务代理
                ipAddresses = request.getHeader("X-Real-IP");
            }
            //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
            if (ipAddresses != null && ipAddresses.length() != 0) {
                ip = ipAddresses.split(",")[0];
            }
            //还是不能获取到，最后再通过request.getRemoteAddr();获取
            if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddresses)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.info("getIPFromHttpServlet is Exception:{}", e.toString());
        }
        return ip;
    }

    public static String getTXCityCodeByIp(String ip) {
        try {
            String requestURL = String.format("%s%s", TXURL , ip);
            HttpClient httpClient = new HttpClient();
            HttpResult result = httpClient.get(requestURL, null, "UTF-8");
            if (result.getStatus() == 200) {
                JSONObject jsonObject = JSONObject.parseObject(result.getResponseBody());
                JSONObject adInfo = jsonObject.getJSONObject("result").getJSONObject("ad_info");
                if (!StringUtils.isBlank(adInfo.getString("city"))) {
                    String cityCode = adInfo.getString("adcode");
                    if (!StringUtils.isBlank(adInfo.getString("district"))) {
                        cityCode = cityCode.substring(0,4) + "00";
                    }
                    return cityCode;
                }
            }
        } catch (Exception e) {
            log.error("getTXCityCodeByIp ip:[{}] is Exception:{}", ip, e.toString());
        }
        return null;
    }

}
