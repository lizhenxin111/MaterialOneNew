package com.lzx.materialone.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lzx.materialone.R;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;

/**
 * Created by lizhe on 2017/7/20.
 */

public class WbSDKManager {

    Context context;
    public WbSDKManager(Context context){
        this.context = context;
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     */
    public void sendMessage(WbShareHandler shareHandler, String shareText, Bitmap shareImage,
                            String title, String description, String actionUrl, String defaultText) {
        sendMultiMessage(shareHandler, shareText, shareImage, title, description, actionUrl, defaultText);
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     */
    private void sendMultiMessage(WbShareHandler shareHandler, String shareText, Bitmap shareImage,
                                  String title, String description, String actionUrl, String defaultText) {


        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject = getTextObj(shareText);
        weiboMessage.imageObject = getImageObj(shareImage);
        weiboMessage.mediaObject = getWebpageObj(title, description, actionUrl, defaultText);
        shareHandler.shareMessage(weiboMessage, false);

    }

    /**
     * 获取分享的文本模板。
     */
    private String getSharedText() {

        return "测试";
    }

    /**
     * 创建文本消息对象。
     * @return 文本消息对象。
     */
    private TextObject getTextObj(String shareText) {
        TextObject textObject = new TextObject();
        textObject.text = shareText;
        textObject.title = "xxxx";
        textObject.actionUrl = "http://www.baidu.com";
        return textObject;
    }

    /**
     * 创建图片消息对象。
     * @return 图片消息对象。
     */
    private ImageObject getImageObj(Bitmap bitmap) {
        ImageObject imageObject = new ImageObject();
        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj(String title, String description, String actionUrl, String defaultText) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = description;
        Bitmap  bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.weibo_logo);
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = actionUrl;
        mediaObject.defaultText = defaultText;
        return mediaObject;
    }
}
