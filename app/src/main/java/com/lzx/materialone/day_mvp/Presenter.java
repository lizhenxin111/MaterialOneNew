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

    public void setAllItem(View view){
        String contentJson = model.getContentJson();
        BasicData basicData = getBasicData(contentJson);
        StringBuilder dateBuilder = new StringBuilder(basicData.getDate());
        dayFrag.setDate(dateBuilder.substring(5, 10).toString());
        List<ItemData> contentItemList = getItemDataList(contentJson);
        ItemData picInfo = contentItemList.get(0);
        dayFrag.setPic(view, picInfo.getImgUrl(), "插画 | " + picInfo.getPicInfo(), picInfo.getForward(), "by : " + picInfo.getWordsInfo(), picInfo.getImgUrl());
        for (int i = 1; i < contentItemList.size(); i++){
            ItemData itemInfo = contentItemList.get(i);
            dayFrag.setContentItem(view, "- " + itemInfo.getCategory() + " -", itemInfo.getImgUrl(), itemInfo.getTitle(), itemInfo.getUserName(), itemInfo.getForward(), itemInfo.getShareUrl(), itemInfo.getImgUrl(), itemInfo.getAudioUrl(), itemInfo.getContentTag(), i);
        }
    }

    private List<ItemData> getItemDataList(final String contentJson){
        Callable<List<ItemData>> callable = new Callable<List<ItemData>>() {
            @Override
            public List<ItemData> call() throws Exception {
                List<ItemData> itemDataList = new ArrayList<>();
                Gson contentGson = new Gson();
                DayContent content = contentGson.fromJson(contentJson, DayContent.class);
                ContentData contentData = content.getData();
                List<ContentItem> contentItemList = contentData.getContentList();
                ContentItem mContentItem = new ContentItem();
                for (int i = 0; i < contentItemList.size(); i++) {
                    mContentItem = contentItemList.get(i);
                    ItemData mItemData = new ItemData();
                    mItemData.setTitle(mContentItem.getTitle());
                    mItemData.setId(mContentItem.getId());
                    mItemData.setCategory(mContentItem.getCategory());
                    mItemData.setItemId(mContentItem.getItemId());
                    mItemData.setTitle(mContentItem.getTitle());
                    mItemData.setForward(mContentItem.getForward());
                    String mImageUrl = mContentItem.getImgUrl();
                    mItemData.setImgUrl(mImageUrl);
                    Author mAuthor = mContentItem.getAuthor();
                    mItemData.setUserName(mAuthor.getUserName());
                    String mAudioUrl = mContentItem.getAudioUrl();
                    mItemData.setAudioUrl(mAudioUrl);
                    mItemData.setPicInfo(mContentItem.getPicInfo());
                    mItemData.setWordsInfo(mContentItem.getWordsInfo());
                    mItemData.setSubTitle(mContentItem.getSubTitle());
                    mItemData.setNumber(mContentItem.getNumber());
                    mItemData.setSerialId(mContentItem.getSerialId());
                    mItemData.setMovieStoryId(mContentItem.getMovieStoryId());
                    mItemData.setContentId(mContentItem.getContentId());
                    mItemData.setContentType(mContentItem.getContentType());
                    mItemData.setShareUrl(mContentItem.getShareUrl());
                    mItemData.setTagList(mContentItem.getTagList());
                    mItemData.setSerialList(mContentItem.getSerialList());
                    //mItemData.setPicBytesInputSteam(getImageBytesInputSteam(mImageUrl));
                    if (!mAudioUrl.equals("")) {
                        mItemData.setAudioAlbum(mContentItem.getAudioAlbum());
                        mItemData.setAudioAuthor(mContentItem.getAudioAuthor());
                        mItemData.setMusicName(mContentItem.getMusicName());
                    }
                    itemDataList.add(mItemData);
                }
                return itemDataList;
            }
        };
        FutureTask<List<ItemData>> task = new FutureTask<List<ItemData>>(callable);
        Thread thread = new Thread(task);
        thread.start();
        List<ItemData> itemDataList = null;
        try {
            itemDataList = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }finally {
            return itemDataList;
        }
    }

    private BasicData getBasicData(final String contentJson) {
        Callable<BasicData> callable = new Callable<BasicData>() {
            @Override
            public BasicData call() throws Exception {
                BasicData basicData;
                Gson contentGson = new Gson();
                DayContent content = contentGson.fromJson(contentJson, DayContent.class);
                ContentData contentData = content.getData();
                String date = contentData.getDate();
                Weather weather = contentData.getWeather();
                String climate = weather.getClimate();
                String cityName = weather.getCityName();
                basicData = new BasicData(date, cityName, climate);
                return basicData;
            }
        };
        FutureTask<BasicData> task = new FutureTask<BasicData>(callable);
        Thread thread = new Thread(task);
        thread.start();
        BasicData basicData = null;
        try{
            basicData = task.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }finally {
            return basicData;
        }
    }
}
