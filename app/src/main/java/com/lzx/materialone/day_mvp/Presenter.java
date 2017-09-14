package com.lzx.materialone.day_mvp;

import android.view.View;
import com.google.gson.Gson;
import com.lzx.materialone.bean.data.common_item.BasicData;
import com.lzx.materialone.bean.data.common_item.ItemData;
import com.lzx.materialone.bean.data.day.content.Author;
import com.lzx.materialone.bean.data.day.content.ContentData;
import com.lzx.materialone.bean.data.day.content.ContentItem;
import com.lzx.materialone.bean.data.day.content.DayContent;
import com.lzx.materialone.bean.data.day.content.Weather;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Created by lizhe on 2017/6/4.
 */

public class Presenter {
    private DayFrag dayFrag;
    private Model model;
    public Presenter(DayFrag dayFrag, int day){
        this.dayFrag = dayFrag;
        this.model = new Model(day);
    }

    public void setAllItem(){
        model.setDayContent();
        dayFrag.setDate(model.getDate().substring(5, 10).toString());
        List<ContentItem> contentItemList = model.getContentItem();
        ContentItem contentItem = contentItemList.get(0);
        dayFrag.setPic(contentItem.getImgUrl(), "插画 | " + contentItem.getPicInfo(), contentItem.getForward(), "by : " + contentItem.getWordsInfo(), contentItem.getImgUrl());
        for (int i = 1; i < contentItemList.size() - 1; i++){
            contentItem = contentItemList.get(i);
            dayFrag.setContentItem("- " + contentItem.getCategory() + " -", contentItem.getImgUrl(), contentItem.getTitle(), contentItem.getAuthor().getUserName(), contentItem.getForward(), contentItem.getShareUrl(), contentItem.getImgUrl(), contentItem.getAudioUrl(), contentItem.getContentType(), i);
        }
        contentItem = contentItemList.get(contentItemList.size() - 1);
        if (contentItem.getIsRegular() == 1){
            dayFrag.setRadio(contentItem.getTitle(), contentItem.getAuthor().getUserName(), contentItem.getImgUrl(), contentItem.getAudioUrl(), contentItem.getShareUrl());
        }
    }
}
