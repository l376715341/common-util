package com.xinchao.tech.xinchaoad.common.util.apppush;

import com.alibaba.fastjson.JSON;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
public class AppPush {

    //定义常量, appId、appKey、masterSecret 采用本文档 "第二步 获取访问凭证 "中获得的应用配置
    private String appId = "Eyz6vqYMQk9bSnUMBgq4e2";
    private String appKey = "9M3L1GnW7r9YTuB4u0cwl1";
    private String masterSecret = "3I6VQg4HL97r7BZToKD6I";
    private String host = "http://sdk.open.api.igexin.com/apiex.htm";

    public void sendPushTOApp(String title, AppPushEntity appPushEntity) throws Exception {
        try {
            IGtPush push = new IGtPush(host, appKey, masterSecret);

            // 定义"点击链接打开通知模板"，并设置标题、内容、链接
            TransmissionTemplate template = getTransmissionTemplate(appPushEntity);

            List<String> appIds = new ArrayList<>();
            appIds.add(appId);

            // 定义"AppMessage"类型消息对象，设置消息内容模板、发送的目标App列表、是否支持离线发送、以及离线消息有效期(单位毫秒)
            AppMessage message = new AppMessage();
            message.setData(template);
            message.setAppIdList(appIds);
            message.setOffline(true);
            message.setOfflineExpireTime(1000 * 600);

            //注释部分是 添加省市 标签 推送
//        AppConditions cdt = new AppConditions();
//        cdt.addCondition(AppConditions.PHONE_TYPE, StringList, AppConditions.OptType.or);
//        cdt.addCondition(AppConditions.REGION, StringList, AppConditions.OptType.or);
//        cdt.addCondition(AppConditions.TAG, StringList, AppConditions.OptType.or);
//        message.setConditions(cdt);

            IPushResult ret = push.pushMessageToApp(message);
            log.info(ret.getResponse().toString());
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * 推送给某个用户
     *
     * @param appPushEntity
     * @param cid
     */
    public void sendMassagePushToCid(AppPushEntity appPushEntity, String cid) throws Exception {
        if (StringUtils.isBlank(cid)) {
            log.info("cid 为空");
            return;
        }

        try {
            IGtPush push = new IGtPush(host, appKey, masterSecret);
            TransmissionTemplate template = getTransmissionTemplate(appPushEntity);
            SingleMessage message = new SingleMessage();
            message.setOffline(true);
            // 离线有效时间，单位为毫秒，可选
            message.setOfflineExpireTime(24 * 3600 * 1000);
            message.setData(template);
            // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
            message.setPushNetWorkType(0);
            Target target = new Target();
            target.setAppId(appId);
            target.setClientId(cid);
            //target.setAlias(Alias);
            IPushResult ret = null;
            try {
                ret = push.pushMessageToSingle(message, target);
            } catch (RequestException e) {
                e.printStackTrace();
                ret = push.pushMessageToSingle(message, target, e.getRequestId());
            }
            if (ret != null) {
                System.out.println(ret.getResponse().toString());
            } else {
                System.out.println("服务器响应异常");
            }
            log.info(ret.getResponse().toString());
        }catch (Exception e){
            throw e;
        }
    }

    /**
     * @param cids
     * @param appPushEntity
     * @throws Exception
     */
    public void sendNotficationToCids(List<String> cids, AppPushEntity appPushEntity) throws Exception {
        if (CollectionUtils.isEmpty(cids)) {
            log.info("cids 为空");
            return;
        }
        try {
            // 配置返回每个用户返回用户状态，可选
            System.setProperty("gexin.rp.sdk.pushlist.needDetails", "true");
            IGtPush push = new IGtPush(host, appKey, masterSecret);
            // 通知透传模板
            TransmissionTemplate template = getTransmissionTemplate(appPushEntity);
            ListMessage message = new ListMessage();
            message.setData(template);
            // 设置消息离线，并设置离线时间
            message.setOffline(true);
            // 离线有效时间，单位为毫秒，可选
            message.setOfflineExpireTime(24 * 1000 * 3600);
            // 配置推送目标
            List targets = new ArrayList();
            cids.forEach(cid -> {
                Target target = new Target();
                target.setAppId(appId);
                target.setClientId(cid);
                targets.add(target);
            });
            // taskId用于在推送时去查找对应的message
            String taskId = push.getContentId(message);
            IPushResult ret = push.pushMessageToList(taskId, targets);
           log.info(ret.getResponse().toString());
        }catch (Exception e){
            throw e;
        }
    }

    private NotificationTemplate initNotificationTemplate(String title, String text, String content) throws Exception{
        NotificationTemplate template = new NotificationTemplate();
        // 设置APPID与APPKEY
        template.setAppId(appId);
        template.setAppkey(appKey);

        Style0 style = new Style0();
        // 设置通知栏标题与内容
        style.setTitle(title);
        style.setText(text);
        // 配置通知栏图标
        style.setLogo("icon.png");
        // 配置通知栏网络图标
        style.setLogoUrl("");
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        template.setStyle(style);

        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(2);
        template.setTransmissionContent(content);
        return template;
    }

    private LinkTemplate initLinkTemplate(String title, String text, String url) throws  Exception{
        LinkTemplate template = new LinkTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);

        Style0 style = new Style0();
        // 设置通知栏标题与内容
        style.setTitle(title);
        style.setText(text);
        // 配置通知栏图标
        style.setLogo("icon.png");
        // 配置通知栏网络图标
        style.setLogoUrl("");
        // 设置通知是否响铃，震动，或者可清除
        style.setRing(true);
        style.setVibrate(true);
        style.setClearable(true);
        template.setStyle(style);
        template.setUrl(url);

        return template;
    }

    private TransmissionTemplate getTransmissionTemplate(AppPushEntity appPushEntity) throws Exception{
       try {
           TransmissionTemplate template = new TransmissionTemplate();
           template.setAppId(appId);
           template.setAppkey(appKey);
           template.setTransmissionContent(JSON.toJSONString(appPushEntity));
           template.setTransmissionType(2);
//        try {
//            template.setDuration("2019-05-07 14:58:00","2019-05-07 15:00:00");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
           template.setAPNInfo(initAPNPayload(appPushEntity));
           return template;
       }catch (Exception e){
           throw e;
       }
    }

    private APNPayload initAPNPayload(AppPushEntity appPushEntity)throws  Exception {
      try {
          APNPayload payload = new APNPayload();
          //在已有数字基础上加1显示，设置为-1时，在已有数字上减1显示，设置为数字时，显示指定数字
          payload.setAutoBadge("+1");
          payload.setContentAvailable(0);
          //ios 12.0 以上可以使用 Dictionary 类型的 sound
          payload.setSound("default");
          payload.setCategory(appPushEntity.getCategory());
          payload.addCustomMsg("payload", "payload");

          //简单模式APNPayload.SimpleMsg
          payload.setAlertMsg(getDictionaryAlertMsg(appPushEntity.getTitle(), appPushEntity.getContent()));

          //字典模式使用APNPayload.DictionaryAlertMsg
          //payload.setAlertMsg(getDictionaryAlertMsg());

          //设置语音播报类型，int类型，0.不可用 1.播放body 2.播放自定义文本
          payload.setVoicePlayType(0);
          //设置语音播报内容，String类型，非必须参数，用户自定义播放内容，仅在voicePlayMessage=2时生效
          //注：当"定义类型"=2, "定义内容"为空时则忽略不播放
          payload.setVoicePlayMessage("");

          // 添加多媒体资源
//        payload.addMultiMedia(new MultiMedia().setResType(MultiMedia.MediaType.video)
//                .setResUrl("http://ol5mrj259.bkt.clouddn.com/test2.mp4")
//                .setOnlyWifi(true));
//需要使用IOS语音推送，请使用VoIPPayload代替APNPayload
// VoIPPayload payload = new VoIPPayload();
// JSONObject jo = new JSONObject();
// jo.put("key1","value1");
//         payload.setVoIPPayload(jo.toString());
//
          return payload;
      }catch (Exception e){
          throw e;
      }
    }

    private static APNPayload.DictionaryAlertMsg getDictionaryAlertMsg(String title, String content) throws Exception{
      try {
          APNPayload.DictionaryAlertMsg alertMsg = new APNPayload.DictionaryAlertMsg();
          alertMsg.setBody(content);
//        alertMsg.setActionLocKey("ActionLockey");
//        alertMsg.setLocKey("LocKey");
//        alertMsg.addLocArg("loc-args");
//        alertMsg.setLaunchImage("launch-image");
          // iOS8.2以上版本支持
          alertMsg.setTitle(title);
//        alertMsg.setTitleLocKey("TitleLocKey");
//        alertMsg.addTitleLocArg("TitleLocArg");
          return alertMsg;
      }catch (Exception e){
          throw e;
      }
    }

    public static void main(String[] args) {
//        AppPush appPush = new AppPush();
//        AppPushEntity appPushEntity = new AppPushEntity();
//        appPushEntity.setContent("测试推送223");
//        appPushEntity.setTitle("test");
//        appPushEntity.setPath("order/detail");
//        appPushEntity.setCategory(AppPushEntity.getCategoryOrderdetail(442L));
//        Map map = new HashMap<>();
//        map.put("id", 442);
//        appPushEntity.setData(map);
//        appPushEntity.setType("order");
//        System.out.println(JSON.toJSONString(appPushEntity));
//        appPush.sendMassagePushToCid(appPushEntity, "a1e81bb61bc6586ad15fbe46fbf94282");
////        appPush.sendMassagePushToCid(appPushEntity, "b5c8381f44e1e5f2d83f89708f238aa5");
    }
}

