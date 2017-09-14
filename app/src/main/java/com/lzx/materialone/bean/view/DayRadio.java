package com.lzx.materialone.bean.view;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.lzx.materialone.R;
import com.lzx.materialone.activities.DetailAct;
import com.lzx.materialone.activities.MainActivity;
import com.lzx.materialone.day_mvp.DayFrag;
import com.lzx.materialone.manager.ImageManager;
import com.lzx.materialone.manager.MyNotificationManager;
import com.lzx.materialone.manager.NetworkManager;
import com.lzx.materialone.services.MusicService;

/**
 * Created by lizhenxin on 17-8-18.
 */

public class DayRadio extends CardView implements View.OnClickListener, View.OnLongClickListener {
    private String mTitle;
    private String mAudioUrl;
    private String mImageUrl;
    private String mArticleUrl;

    private Context mContext;

    private ImageButton toggle;
    private ImageView background;

    public DayRadio(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public DayRadio(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public DayRadio(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setParams(String title, String author, String audioUrl, String imageUrl, String articleUrl) {
        mTitle = title;
        mAudioUrl = audioUrl;
        mImageUrl = imageUrl;
        mArticleUrl = articleUrl;

        setContent(author);
    }

    public void setOnToggleClick(OnClickListener onClickListener){
        toggle.setOnClickListener(onClickListener);
    }

    public void setToggleImage(int resId){
        toggle.setBackgroundResource(resId);
    }

    private void setContent(String author) {
        LayoutInflater.from(mContext).inflate(R.layout.day_item_radio, this, true);

        background = (ImageView) findViewById(R.id.day_radio_pic);
        ImageManager imageManager = new ImageManager();
        imageManager.setImageFromUrl((Activity) mContext, background, mImageUrl, true);

        ((TextView)findViewById(R.id.day_radio_author)).setText(author);

        ((TextView)findViewById(R.id.day_radio_title)).setText(mTitle);
        toggle = (ImageButton) findViewById(R.id.radio_toggle);

        findViewById(R.id.radio_root).setOnClickListener(this);

        findViewById(R.id.radio_root).setOnLongClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.radio_root) {
            Intent intent = new Intent(mContext, DetailAct.class);
            intent.putExtra("tag", "radio");
            intent.putExtra("url", mArticleUrl);
            intent.putExtra("imageUrl", mImageUrl);
            intent.putExtra("title", mTitle);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, background, "shareImageView");
            mContext.startActivity(intent, options.toBundle());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        final PopupMenu menu = new PopupMenu(mContext, v, Gravity.CENTER);
        menu.getMenuInflater().inflate(R.menu.image_long_click, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.save_image){
                    if (new NetworkManager(mContext).getNetworkState() != NetworkManager.NONE){
                        int result = new ImageManager().saveImage(mContext, mImageUrl);
                        String message = null;
                        if (result == ImageManager.IMAGE_EXIST){
                            message = mContext.getString(R.string.image_exist);
                        }else {
                            message = mContext.getString(R.string.save_complete);
                        }
                        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
        menu.show();
        return true;
    }

}
