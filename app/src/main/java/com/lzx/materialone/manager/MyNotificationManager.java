package com.lzx.materialone.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.widget.RemoteViews;

/**
 * 适配器模式封装通知管理类
 */

public class MyNotificationManager {
    private Notification.Builder builder;
    private NotificationManager notificationManager;

    public MyNotificationManager(Context context){
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new Notification.Builder(context);
    }

    private void setContentText(String contentText){
        builder.setContentText(contentText);
    }
    private void setSmallIcon(int resourceId){
        builder.setSmallIcon(resourceId);
    }
    private void setContentTitle(String contentTitle){
        builder.setContentTitle(contentTitle);
    }
    private void setContentIntent(PendingIntent pendingIntent){
        builder.setContentIntent(pendingIntent);
    }
    private void setPriority(int pri){
        builder.setPriority(pri);
    }
    private void notify(int id){
        notificationManager.notify(id, builder.build());
    }
    private void setContentView(RemoteViews remoteViews){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setCustomContentView(remoteViews);
        }
    }
    private void setContent(RemoteViews remoteViews){
        builder.setContent(remoteViews);
    }
    public void cancle(int id){
        notificationManager.cancel(id);
    }

    public static class Builder{
        MyNotificationManager myNotificationManager;
        public Builder (Context context){
            myNotificationManager = new MyNotificationManager(context);
        }
        public Builder setContentTitle(String contentTitle){
            myNotificationManager.setContentTitle(contentTitle);
            return this;
        }
        public Builder setContentText(String contentText){
            myNotificationManager.setContentText(contentText);
            return this;
        }
        public Builder setSmallIcon(int resourceId){
            myNotificationManager.setSmallIcon(resourceId);
            return this;
        }
        public Builder setContentIntent(PendingIntent pendingIntent){
            myNotificationManager.setContentIntent(pendingIntent);
            return this;
        }
        public Builder setPriority(int pri){
            myNotificationManager.setPriority(pri);
            return this;
        }
        public Builder setContentRemoteViews(RemoteViews remoteViews){
            myNotificationManager.setContentView(remoteViews);
            return this;
        }
        public Builder setContent(RemoteViews remoteViews){
            myNotificationManager.setContent(remoteViews);
            return this;
        }
        public MyNotificationManager build(int id){
            myNotificationManager.notify(id);
            return myNotificationManager;
        }
    }
}
