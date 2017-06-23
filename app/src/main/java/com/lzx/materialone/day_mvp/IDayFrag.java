package com.lzx.materialone.day_mvp;

import android.view.View;

/**
 * Created by lizhe on 2017/6/3.
 */

public interface IDayFrag {
    //参数：根view， 标签， 图片链接， 标题， 作者， 摘要， 文章链接，
    void setContentItem(View view, String category, String picUrl, String title, String author, String abstracts,
                        String url, String imageUrl, String audio, int contentTag, int id);

    void setPic(View view, String picUrl, String picAuthor, String words, String wordsAuthor, String url);

    void setDate(String date);

    String getDate();

    void showLoading();

    void removeLoading();
}
