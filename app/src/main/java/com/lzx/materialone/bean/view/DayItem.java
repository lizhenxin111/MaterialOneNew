package com.lzx.materialone.bean.view;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.lzx.materialone.bean.data.common_item.ItemData;
import com.lzx.materialone.R;
import com.lzx.materialone.activities.DetailAct;
import com.lzx.materialone.activities.DetailMusicAct;
import com.lzx.materialone.manager.ImageManager;
import com.lzx.materialone.manager.NetworkManager;


/**
 * 各个项目的自定义View
 */

public class DayItem extends CardView implements View.OnClickListener, View.OnLongClickListener {
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
        initViews();
    }
    private void initViews(){
        LayoutInflater.from(mContext).inflate(R.layout.day_item_normal, this, true);
        pic = (ImageView)findViewById(R.id.day_item_pic);
        category = (TextView)findViewById(R.id.day_item_category);
        title = (TextView)findViewById(R.id.day_item_title);
        author = (TextView)findViewById(R.id.day_item_author);
        abstracts = (TextView)findViewById(R.id.day_item_abstract);
        linearLayout = (LinearLayout) findViewById(R.id.day_item_root);
        linearLayout.setOnClickListener(this);
        pic.setOnClickListener(this);
        pic.setOnLongClickListener(this);
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
        imageManager.setImageFromUrl((Activity) mContext, pic, url, true);
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

    @Override
    public void onClick(View v) {
        if (contentTag == ItemData.CONTENT_TEXT){
            Intent intent = new Intent(mContext, DetailAct.class);
            intent.putExtra("tag", contentTag);
            intent.putExtra("url", tContentUrl);
            intent.putExtra("imageUrl", tImageUrl);
            intent.putExtra("title", title.getText().toString());
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)mContext, pic, "shareImageView");
            mContext.startActivity(intent, options.toBundle());
        }else {
            Intent intent = new Intent(mContext, DetailMusicAct.class);
            intent.putExtra("tag", contentTag);
            intent.putExtra("url", tContentUrl);
            intent.putExtra("imageUrl", tImageUrl);
            intent.putExtra("title", title.getText().toString());
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)mContext, title, "shareIitle");
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
                        int result = new ImageManager().saveImage(mContext, tImageUrl);
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
