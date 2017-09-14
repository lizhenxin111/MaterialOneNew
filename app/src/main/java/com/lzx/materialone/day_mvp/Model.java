package com.lzx.materialone.day_mvp;

import com.google.gson.Gson;
import com.lzx.materialone.bean.data.common_item.BasicData;
import com.lzx.materialone.bean.data.common_item.ItemData;
import com.lzx.materialone.bean.data.day.DayContentId;
import com.lzx.materialone.bean.data.day.content.ContentData;
import com.lzx.materialone.bean.data.day.content.ContentItem;
import com.lzx.materialone.bean.data.day.content.DayContent;
import com.lzx.materialone.bean.data.day.content.Weather;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lizhe on 2017/6/4.
 */

public class Model implements IModel {
    private DayContent mDayContent = null;
    private int l;
    public Model(int l){
        this.l = l;
    }


    public void setDayContent(){
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                String mContentJson = null;
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request idRequest = new Request.Builder()
                            .url("http://v3.wufazhuce.com:8000/api/onelist/idlist")
                            .build();
                    Response idResponse = client.newCall(idRequest).execute();
                    String idJson = idResponse.body().string();
                    Gson idGson = new Gson();
                    DayContentId contentId = idGson.fromJson(idJson, DayContentId.class);
                    List<Integer> idList = contentId.getData();
                    int aimId = idList.get(l);
                    Request contentRequest = new Request.Builder()
                            //.url("http://v3.wufazhuce.com:8000/api/onelist/3994         /0?channel=huawei&version=4.1.0&uuid=&platform=android")
                            .url("http://v3.wufazhuce.com:8000/api/onelist/" + aimId + "/0?version=4.3.0")
                            .build();
                    Response contentResponse = client.newCall(contentRequest).execute();
                    mContentJson = contentResponse.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return mContentJson;
            }
        };
        FutureTask<String> task = new FutureTask<String>(callable);
        Thread thread = new Thread(task);
        thread.start();
        Gson gson = new Gson();
        try {
            mDayContent = gson.fromJson(task.get(), DayContent.class);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDate(){
        if (mDayContent == null){
            setDayContent();
        }
        return mDayContent.getData().getDate();
    }

    @Override
    public List<ContentItem> getContentItem(){
        if (mDayContent == null){
            setDayContent();
        }
        return mDayContent.getData().getContentList();
    }

    @Override
    public Weather getWeather(){
        if (mDayContent == null){
            setDayContent();
        }
        return mDayContent.getData().getWeather();
    }

    @Override
    public int getId(){
        if (mDayContent == null){
            setDayContent();
        }
        return mDayContent.getData().getId();
    }
}
