package com.lzx.materialone.day_mvp;

import com.lzx.materialone.bean.data.common_item.BasicData;
import com.lzx.materialone.bean.data.common_item.ItemData;
import com.lzx.materialone.bean.data.day.content.ContentItem;
import com.lzx.materialone.bean.data.day.content.Weather;

import java.util.List;

/**
 * Created by lizhe on 2017/6/4.
 */

public interface IModel {
    //BasicData getBasicData();
    String getDate();
    List<ContentItem> getContentItem();
    Weather getWeather();
    int getId();
}
