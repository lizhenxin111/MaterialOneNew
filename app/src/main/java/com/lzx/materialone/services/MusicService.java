package com.lzx.materialone.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.lzx.materialone.R;
import com.lzx.materialone.activities.DetailAct;
import com.lzx.materialone.activities.MainActivity;
import com.lzx.materialone.bean.download.LocalReceiver;
import com.lzx.materialone.day_mvp.DayFrag;
import com.lzx.materialone.manager.MyApp;

import java.io.IOException;
import java.util.Map;

public class MusicService extends Service {

    private final String ACTION_PLAY = "notification_action_play";
    private final String ACTION_PAUSE = "notification_action_pause";
    private final String ACTION_CLOSE = "notification_action_close";
    private final String ACTION_TOGGLE = "notification_action_toggle";

    public static final int MEDIAPLAYER_NULL = 0;
    public static final int MEDIAPLAYER_PLAYING = 1;
    public static final int MEDIAPLAYER_PAUSING = 2;

    private String mTitle;
    private String mAudioUrl;
    private String mImageUrl;

    private String mArticleUrl;

    private RemoteViews remoteViews;

    /**
     * gloable MediaPlayer
     */
    private MediaPlayer mediaPlayer;

    public MusicService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PLAY);
        intentFilter.addAction(ACTION_PAUSE);
        intentFilter.addAction(ACTION_CLOSE);
        intentFilter.addAction(ACTION_TOGGLE);
        registerReceiver(localReceiver, intentFilter);
    }


    public interface OnStateChangeListehner{
        void onChanged();
    }
    public interface OnReleaseListener{
        void onRelease();
    }
    private OnStateChangeListehner mOnStateChangeListehner = null;
    private OnReleaseListener mOnReleaseListener = null;


    /**
     * music control interface
     */
    private interface IMusicControl {
        void init(String url, String title, String audioUrl, String imageUrl, String articleUrl);
        void play();
        void pause();
        void release();
        boolean isPlaying();
        int getDuration();
        int getCurrentPosition();
        void seekTo(int position);
        void setOnPlayerPreparedListener(MediaPlayer.OnPreparedListener onPlayerPreparedListener);
        boolean isInit();
        void setOnStateChangeListener(OnStateChangeListehner onStateChangeListener);
        void setOnReleaseListener(OnReleaseListener onReleaseListener);
        int getState();
    }


    /**
     * implement interface
     */
    private IMusicControl iMusicControl = new IMusicControl() {

        @Override
        public void init(final String url, String title, String audioUrl, String imageUrl, String articleUrl) {
            try {
                release();
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(url);
                mediaPlayer.prepareAsync();

                mAudioUrl = audioUrl;
                mTitle = title;
                mArticleUrl = articleUrl;
                mImageUrl = imageUrl;
                mArticleUrl = articleUrl;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void play() {
            if (mediaPlayer != null){
                if (!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                    if (mOnStateChangeListehner != null){
                        mOnStateChangeListehner.onChanged();
                    }
                }
            }
        }

        @Override
        public void pause() {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    if (mOnStateChangeListehner != null){
                        mOnStateChangeListehner.onChanged();
                    }
                }
            }
        }

        @Override
        public void release() {
            if (mediaPlayer != null){
                mediaPlayer.release();
                mediaPlayer = null;
                if (mOnReleaseListener != null){
                    mOnReleaseListener.onRelease();
                }
            }
        }

        @Override
        public boolean isPlaying() {
            if (mediaPlayer != null) {
                return mediaPlayer.isPlaying();
            } else {
                return false;
            }
        }

        @Override
        public int getDuration() {
            if (mediaPlayer != null){
                return mediaPlayer.getDuration();
            } else {
                return 0;
            }
        }

        @Override
        public int getCurrentPosition() {
            if (mediaPlayer != null){
                return mediaPlayer.getCurrentPosition();
            } else {
                return 0;
            }
        }

        @Override
        public void seekTo(int position) {
            mediaPlayer.seekTo(position);
        }

        @Override
        public void setOnPlayerPreparedListener(MediaPlayer.OnPreparedListener onPlayerPreparedListener) {
            mediaPlayer.setOnPreparedListener(onPlayerPreparedListener);
        }

        @Override
        public boolean isInit() {
            return mediaPlayer != null;
        }

        @Override
        public void setOnStateChangeListener(OnStateChangeListehner onStateChangeListener) {
            mOnStateChangeListehner = onStateChangeListener;
        }

        @Override
        public void setOnReleaseListener(OnReleaseListener onReleaseListener) {
            mOnReleaseListener = onReleaseListener;
        }

        @Override
        public int getState() {
            if (mediaPlayer == null) {
                return MEDIAPLAYER_NULL;
            } else {
                if (mediaPlayer.isPlaying()){
                    return MEDIAPLAYER_PLAYING;
                } else {
                    return MEDIAPLAYER_PAUSING;
                }
            }
        }
    };



    private MusicControlBinder binder = new MusicControlBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    /**
     * to control the MediaPlayer
     */
    public class MusicControlBinder extends Binder {
        public void init(String url, String title, String audioUrl, String imageUrl, String articleUrl) {
            iMusicControl.init(url, title, audioUrl, imageUrl, articleUrl);
        }

        public void setOnPlayerPreparedListener(MediaPlayer.OnPreparedListener onPlayerPreparedListener){
            iMusicControl.setOnPlayerPreparedListener(onPlayerPreparedListener);
        }

        public void play(){
            iMusicControl.play();
            getNotificationManager().notify(1, getForegroundNotification(mTitle));
            remoteViews.setImageViewResource(R.id.music_noti_toggle, R.mipmap.ic_pause_translution);
            getNotificationManager().notify(1, getForegroundNotification(mTitle));
        }

        public void pause(){
            iMusicControl.pause();
            remoteViews.setImageViewResource(R.id.music_noti_toggle, R.mipmap.ic_play_translution);
            getNotificationManager().notify(1, getForegroundNotification(mTitle));
        }

        public boolean isPlaying() {
            return iMusicControl.isPlaying();
        }

        public void release(){
            iMusicControl.release();
            getNotificationManager().cancel(1);
        }

        public int getDuration(){
            return iMusicControl.getDuration();
        }

        public int getCurrentPosition(){
            return iMusicControl.getCurrentPosition();
        }

        public void seekTo(int position){
            iMusicControl.seekTo(position);
        }

        public boolean isInit(){
            return iMusicControl.isInit();
        }

        public void setOnStateChangeListener(OnStateChangeListehner onStateChangeListener){
            iMusicControl.setOnStateChangeListener(onStateChangeListener);
        }

        public void setOnReleaseListener(OnReleaseListener onReleaseListener){
            iMusicControl.setOnReleaseListener(onReleaseListener);
        }

        public int getState(){
            return iMusicControl.getState();
        }
    }


    private NotificationManager getNotificationManager(){
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
    private Notification getForegroundNotification(String title){
        remoteViews = new RemoteViews(getPackageName(), R.layout.notification_remoteview);
        remoteViews.setTextColor(R.id.noti_title, Color.BLACK);
        remoteViews.setTextViewText(R.id.noti_title, title);
        remoteViews.setTextViewTextSize(R.id.noti_title, 1, 16);

        remoteViews.setImageViewResource(R.id.music_noti_toggle, binder.isPlaying() ? R.mipmap.ic_pause_translution : R.mipmap.ic_play_translution);
        remoteViews.setOnClickPendingIntent(R.id.music_noti_toggle, getBroadcastPendingIntent(ACTION_TOGGLE, 1));
        remoteViews.setOnClickPendingIntent(R.id.music_noti_cancel, getBroadcastPendingIntent(ACTION_CLOSE, 2));

        Intent intent = new Intent(this, DetailAct.class);
        intent.putExtra("tag", "radio");
        intent.putExtra("url", mArticleUrl);
        intent.putExtra("imageUrl", mImageUrl);
        intent.putExtra("title", mTitle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_launcher_translution)
                //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_translution))
                .setContent(remoteViews)
                .setPriority(2)
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        return notification;
    }
    private PendingIntent getBroadcastPendingIntent(String action, int requestCode){
        Intent toggleIntent = new Intent();
        toggleIntent.setAction(action);
        return PendingIntent.getBroadcast(getApplicationContext(), requestCode, toggleIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    private BroadcastReceiver localReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case ACTION_TOGGLE:
                    if (binder.isPlaying()){
                        binder.pause();
                    } else {
                        binder.play();
                    }
                    break;
                case ACTION_CLOSE:
                    binder.pause();
                    binder.release();
                    break;
                default: break;
            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        binder.release();
        mediaPlayer = null;
        unregisterReceiver(localReceiver);
    }
}
