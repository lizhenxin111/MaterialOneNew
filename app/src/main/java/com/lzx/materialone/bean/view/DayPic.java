package com.lzx.materialone.bean.view;

import android.app.Activity;
import android.app.ActivityOptions;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzx.materialone.R;
import com.lzx.materialone.activities.ImageViewAct;
import com.lzx.materialone.manager.ImageManager;


/**
 * 每日一图的自定义View
 */

public class DayPic extends CardView implements View.OnClickListener {
    private Context mContext;
    private ImageView pic;
    private TextView picAuthor, words, wordsAuthor;
    private CardView cardView;
    private FragmentManager mFragmentManager;
    private String tUrl, date;

    public DayPic(Context context, FragmentManager fragmentManager, @Nullable Class<ImageViewAct> aimAct) {
        super(context);
        initViews(context, aimAct);
        mContext = context;
        mFragmentManager = fragmentManager;
    }
    private void initViews(Context context, @Nullable final Class<ImageViewAct> aimAct){
        LayoutInflater.from(context).inflate(R.layout.day_item_pic, this, true);
        cardView = (CardView)findViewById(R.id.day_item_one_pic);
        pic = (ImageView)findViewById(R.id.day_item_pic);
        picAuthor = (TextView)findViewById(R.id.day_item_pic_author);
        words = (TextView)findViewById(R.id.day_item_words);
        wordsAuthor = (TextView)findViewById(R.id.day_item_words_author);
        cardView.setOnClickListener(this);
    }

    public void setPic(Bitmap image){
        pic.setImageBitmap(image);
    }
    public void setPicByUrl(String url){
        ImageManager imageManager = new ImageManager();
        imageManager.setImageFromUrl((Activity) mContext, pic, url, true);
    }
    public void setPicAuthor(String author){
        picAuthor.setText(author);
    }
    public void setWords(String word){
        words.setText(word);
    }
    public void setWordsAuthor(String author){
        wordsAuthor.setText(author);
    }
    public void setUrl(String url){
        this.tUrl = url;
    }

    public void setDate(String date){
        this.date = date;
    }
    public String getUrl(){
        return tUrl;
    }

    public String getSharedText(){
        return words.getText().toString() + "    " + wordsAuthor.getText().toString();
    }

    public Bitmap getSharedImage(){
        pic.setDrawingCacheEnabled(true);
        pic.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable)pic.getDrawable()).getBitmap();
        pic.setDrawingCacheEnabled(false);
        return bitmap;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, ImageViewAct.class);
        intent.putExtra("imageUrl", tUrl);
        intent.putExtra("author", picAuthor.getText().toString());
        intent.putExtra("date", date);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity)mContext, pic, "shareImage");
        mContext.startActivity(intent, options.toBundle());
    }
}
