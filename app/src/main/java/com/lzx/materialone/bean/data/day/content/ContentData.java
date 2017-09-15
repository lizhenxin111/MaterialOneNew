package com.lzx.materialone.bean.data.day.content;

import java.util.List;

/**
 * Created by lizhe on 2017/4/23.
 *
 */

public class ContentData {
    private int id;
    private String date;
    private Weather weather;
    private List<ContentItem> content_list;

    public int getId(){return id;}
    public String getDate(){return date;}
    public Weather getWeather(){return weather;}
    public List<ContentItem> getContentList(){return content_list;}

}
