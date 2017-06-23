package com.lzx.materialone.Bean.data.common_item;

/**
 * Created by lizhe on 2017/4/23.
 *
 * 向view传递pic数据时使用的类
 */

public class BasicData {
    private String date;
    private String cityName;
    private String climate;

    public BasicData(){

    }

    public BasicData(String date, String cityName, String climate){
        this.date = date;
        this.climate = climate;
        this.cityName = cityName;
    }

    public String getDate() {
        return date;
    }
    public String getCityName() {
        return cityName;
    }
    public String getClimate() {
        return climate;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    public void setClimate(String climate) {
        this.climate = climate;
    }
}
