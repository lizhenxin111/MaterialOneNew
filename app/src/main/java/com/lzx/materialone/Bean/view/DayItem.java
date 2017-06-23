package com.lzx.materialone.Bean.view;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzx.materialone.Bean.data.common_item.ItemData;
import com.lzx.materialone.R;
import com.lzx.materialone.activities.DetailAct;
import com.lzx.materialone.activities.DetailMusicAct;
import com.lzx.materialone.manager.ImageManager;


/**
 * 各个项目的自定义View
 */

public class DayItem extends CardView {
    private TextView category, title, author, abstracts;
    private ImageView pic;
    private LinearLayout linearLayout;
    private Context mContext;
    private FragmentManager mFragmentManager;

    private String tContentUrl, tImageUrl, tAudioUrl;
    private int contentTag;

    public DayItem(Context context, FragmentManager fragmentManager){
        super(context);
        mFragmentManager = fragmentManager;
        mContext = context;
        initViews(context);
    }
    private void initViews(final Context context){
        LayoutInflater.from(context).inflate(R.layout.day_item_normal, this, true);
        pic = (ImageView)findViewById(R.id.day_item_pic);
        category = (TextView)findViewById(R.id.day_item_category);
        title = (TextView)findViewById(R.id.day_item_title);
        author = (TextView)findViewById(R.id.day_item_author);
        abstracts = (TextView)findViewById(R.id.day_item_abstract);
        linearLayout = (LinearLayout) findViewById(R.id.day_item_root);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contentTag == ItemData.CONTENT_TEXT){
                    Intent intent = new Intent(context, DetailAct.class);
                    intent.putExtra("tag", contentTag);
                    intent.putExtra("url", tContentUrl);
                    intent.putExtra("imageUrl", tImageUrl);
                    intent.putExtra("title", title.getText().toString());
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)context, pic, "shareImageView");
                    context.startActivity(intent, options.toBundle());
                }else {
                    Intent intent = new Intent(context, DetailMusicAct.class);
                    intent.putExtra("tag", contentTag);
                    intent.putExtra("url", tContentUrl);
                    intent.putExtra("imageUrl", tImageUrl);
                    intent.putExtra("title", title.getText().toString());
                    context.startActivity(intent);
                }
            }
        });
    }

    public void setCategory(String mCategory){
        category.setText(mCategory);
    }
    public void setTitle(String mTitle){
        title.setText(mTitle);
    }
    public void setAuthor(String mAuthor){
        author.setText(mAuthor);
    }
    public void setAbstracts(String mAbstracts){
        abstracts.setText(mAbstracts);
    }
    public void setPic(Bitmap image){
        pic.setImageBitmap(image);
    }
    public void setPicByUrl(final String url){
        ImageManager imageManager = new ImageManager();
        imageManager.setImageFromUrl(pic, url, 80, false);
    }

    public void setContentUrl(String tContentUrl) {
        this.tContentUrl = tContentUrl;
    }
    public void setAudioUrl(String tAudioUrl) {
        this.tAudioUrl = tAudioUrl;
    }
    public void setImageUrl(String tImageUrl) {
        this.tImageUrl = tImageUrl;
    }
    public void setContentTag(int contentTag) {
        this.contentTag = contentTag;
    }

    public String getAudioUrl() {
        return tAudioUrl;
    }
    public String getImageUrl() {
        return tImageUrl;
    }
    public String getContentUrl() {
        return tContentUrl;
    }
    public int getContentTag() {
        return contentTag;
    }
}
