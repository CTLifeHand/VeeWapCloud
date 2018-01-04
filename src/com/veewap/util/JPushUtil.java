package com.veewap.util;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.audience.AudienceTarget;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;


import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JPushUtil {
    private JPushUtil() {
    }

    // 静态方法
    static {

    }

    protected static final String APP_KEY ="b9ec994df218ab2148fb891b";
    protected static final String MASTER_SECRET = "091e35ae7eb8b58262ba2842";
    protected static final Logger LOG = LoggerFactory.getLogger(JPushUtil.class);

    public static final String TITLE = "Android Title"; // Android专用
    public static final String ALERT = "Alert for iOS"; // iOS专用
    public static final String MSG_CONTENT = "自定义消息 非APNs";

    public static void PUST(String alias,String content) {
        PUST(alias,content,false,null);
    }
    public static void PUST(String alias,String content, Boolean isProduction) {
        PUST(alias,content,isProduction,null);
    }

    public static void PUST(String alias,String content, Boolean isProduction, JsonObject extra) {
        PushPayload payload = null;
        if (extra !=null){
            payload = _buildPushObject(alias,content, isProduction, extra);
        }else {
            payload = _buildPushObject(alias,content, isProduction);
        }


//        final PushPayload payload = buildPushObject_ios_tagAnd_alertWithExtrasAndMessage();
        ClientConfig clientConfig = ClientConfig.getInstance();
        final JPushClient jpushClient = new JPushClient(MASTER_SECRET, APP_KEY, null, clientConfig);

//        String authCode = ServiceHelper.getBasicAuthorization(APP_KEY, MASTER_SECRET);
        // Here you can use NativeHttpClient or NettyHttpClient or ApacheHttpClient.
//        NativeHttpClient httpClient = new NativeHttpClient(authCode, null, clientConfig);

        try {
            PushResult result = jpushClient.sendPush(payload);
            LOG.info("Got result - " + result);
            System.out.println("Got result - " + result);
            // 如果使用 NettyHttpClient，需要手动调用 close 方法退出进程
            // If uses NettyHttpClient, call close when finished sending request, otherwise process will not exit.
            // jpushClient.close();
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            LOG.error("Sendno: " + payload.getSendno());
            System.out.print(e);
            System.out.print(payload.getSendno());

        } catch (APIRequestException e) {
            System.out.println("Error response from JPush server. Should review and fix it. " + e);

            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Code: " + e.getErrorCode());
            LOG.info("Error Message: " + e.getErrorMessage());
            LOG.info("Msg ID: " + e.getMsgId());
            LOG.error("Sendno: " + payload.getSendno());
        }
    }


    private static PushPayload _buildPushObject(String alias,String content) {
        return _buildPushObject(alias,content,false);
    }

    private static PushPayload _buildPushObject(String alias,String content, Boolean isProduction) {
        Audience target = alias == null ? Audience.all() : Audience.newBuilder()
                .addAudienceTarget(AudienceTarget.alias(alias))
                .build();

        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(target)
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(content)
                                .setBadge(1)
                                .setSound("default")
                                .build())
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle("雨蛙")
                                .setAlert(content)
                                .setAlertType(7)
                                .build())
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(isProduction)
                        .build())
                .build();
    }

    private static PushPayload _buildPushObject(String alias,String content, Boolean isProduction, JsonObject extra) {
        Audience target = alias == null ? Audience.all() : Audience.newBuilder()
                .addAudienceTarget(AudienceTarget.alias(alias))
                .build();

        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(target)
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(content)
                                .setBadge(1)
                                .setSound("default")
                                .addExtra("extra", extra)
                                .build())
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle("雨蛙")
                                .setAlert(content)
                                .setAlertType(7)
                                .addExtra("extra", extra)
                                .build())
                        .build())
//                .setMessage(Message.content(content)) // message：自定义消息
                .setOptions(Options.newBuilder()
                        .setApnsProduction(isProduction)
                        .build())
                .build();
    }

    /*
    * 废弃
    * private static PushPayload _buildPushObject(String alias,String content, Boolean isProduction, String page) {
        Audience target = alias == null ? Audience.all() : Audience.newBuilder()
                .addAudienceTarget(AudienceTarget.alias(alias))
                .build();

        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(target)
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(content)
                                .setBadge(1)
                                .setSound("default")
                                .addExtra("page", page)
                                .build())
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle("雨蛙")
                                .setAlert(content)
                                .setAlertType(7)
                                .addExtra("page", page)
                                .build())
                        .build())
//                .setMessage(Message.content(content)) // message：自定义消息
                .setOptions(Options.newBuilder()
                        .setApnsProduction(isProduction)
                        .build())
                .build();
    }
    *
    *
    * */
}
