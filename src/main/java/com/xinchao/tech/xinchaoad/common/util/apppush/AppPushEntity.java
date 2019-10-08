package com.xinchao.tech.xinchaoad.common.util.apppush;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.xinchao.tech.xinchaoad.common.constant.life.AppPushConstants;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: luhanyu
 * @Date: 2019/4/28 10:29
 * @Description:
 */
@Data
public class AppPushEntity implements Serializable {
    private String type;
    private String path;
    private String title;
    private String content;
    private Object data;

    @JsonIgnore
    @JSONField(serialize = false)
    private String category;



    public static String getCategoryUsercard() {
        return AppPushConstants.CATEGORY_USERCARD;
    }

    public static String getCategoryOrderdetail(Long orderId) {
        return AppPushConstants.CATEGORY_ORDERDETAIL.replace("ORDERID", orderId + "");
    }

    public static AppPushEntity init(String type, String content, String title) {
        AppPushEntity appPushEntity = init(content, title);
        appPushEntity.setType(type);
        if (AppPushConstants.TYPE_USER_CERT.equals(type)) {
            appPushEntity.setPath(AppPushConstants.USER_CERT_URL);
            appPushEntity.setCategory(AppPushEntity.getCategoryUsercard());
        }
        return appPushEntity;
    }

    public static AppPushEntity init(String content, String title) {
        AppPushEntity appPushEntity = new AppPushEntity();
        appPushEntity.setContent(content);
        appPushEntity.setTitle(title);
        return appPushEntity;
    }

    public static AppPushEntity init(String type, String content, String title, Long orderId) {
        AppPushEntity appPushEntity = init(content, title);

        appPushEntity.setType(type);
        if (AppPushConstants.TYPE_USER_CERT.equals(type)) {
            appPushEntity.setPath(AppPushConstants.USER_CERT_URL);
        } else if (AppPushConstants.TYPE_ORDER.equals(type)) {
            if (null != orderId) {
                Map map = new HashMap<>();
                map.put("id", orderId);
                appPushEntity.setData(map);
            }
            appPushEntity.setPath(AppPushConstants.ORDER_URL);
            appPushEntity.setCategory(AppPushEntity.getCategoryOrderdetail(orderId));
        }

        return appPushEntity;
    }
}
