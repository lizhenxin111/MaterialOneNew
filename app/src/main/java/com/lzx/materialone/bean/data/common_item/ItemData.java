package com.lzx.materialone.bean.data.common_item;

import com.lzx.materialone.bean.data.day.content.TagList;

import java.io.InputStream;
import java.util.List;

/**
 * Created by lizhe on 2017/4/23.
 *
 * 向view传递数据时使用的类
 */

public class ItemData {
    public static final int CONTENT_TEXT = 1;
    public static final int CONTENT_MUSIC = 2;
    public static final int CONTENT_FM = 3;

    private String id;
    private String category;
    private String itemId;
    private String title;
    private String forward;
    private String imgUrl;
    private String userName;
    private String videoUrl;
    private String audioUrl;
    private String picInfo;
    private String wordsInfo;
    private String subTitle;
    private String number;
    private String serialId;
    private String movieStoryId;
    private String contentId;
    private String contentType;
    private String shareUrl;
    private TagList tagList;
    private List<Integer> serialList;
    private String picBytes;
    private String musicName;
    private String audioAuthor;
    private String audioAlbum;
    private InputStream picBytesInputSteam;
    private int contentTag;

    public String getId() {
        return id;
    }
    public String getCategory() {
        if (tagList != null){
            return tagList.getTitle();
        }
        else {
            switch (category){
                case "0":
                    return "每日一图";
                case "2":
                    return "连载";
                case "3":
                    return "问答";
                case "4":
                    return "音乐";
                case "5":
                    return "电影";
                default:
                    return null;
            }
        }
        //return category;
    }
    public String getItemId() {
        return itemId;
    }
    public String getTitle() {
        return title;
    }
    public String getForward() {
        return forward;
    }
    public String getImgUrl() {
        return imgUrl;
    }
    public String getUserName() {
        return userName;
    }
    public String getVideoUrl() {
        return videoUrl;
    }
    public String getAudioUrl() {
        return audioUrl;
    }
    public String getPicInfo() {
        return picInfo;
    }
    public String getWordsInfo() {
        return wordsInfo;
    }
    public String getSubtitle() {
        return subTitle;
    }
    public String getNumber() {
        return number;
    }
    public String getSerialId() {
        return serialId;
    }
    public String getMovieStoryId() {
        return movieStoryId;
    }
    public String getContentId() {
        return contentId;
    }
    public String getContentType() {
        return contentType;
    }
    public String getShareUrl() {
        return shareUrl;
    }
    public TagList getTagList() {
        return tagList;
    }
    public List<Integer> getSerialList() {
        return serialList;
    }
    public String getPicBytes() {
        return picBytes;
    }
    public String getMusicName() {
        return musicName;
    }
    public String getAudioAlbum() {
        return audioAlbum;
    }
    public String getAudioAuthor() {
        return audioAuthor;
    }
    public InputStream getPicBytesInputSteam() {
        return picBytesInputSteam;
    }
    public int getContentTag() {
        if (category.equals("4")){
            if (tagList == null){
                return CONTENT_MUSIC;
            }else {
                return CONTENT_FM;
            }
        }else {
            return CONTENT_TEXT;
        }
    }


    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setContentId(String contentId) {
        this.contentId = contentId;
    }
    public void setForward(String forward) {
        this.forward = forward;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public void setMovieStoryId(String movieStoryId) {
        this.movieStoryId = movieStoryId;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public void setPicInfo(String picInfo) {
        this.picInfo = picInfo;
    }
    public void setSerialId(String serialId) {
        this.serialId = serialId;
    }
    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setWordsInfo(String wordsInfo) {
        this.wordsInfo = wordsInfo;
    }
    public void setSerialList(List<Integer> serialList) {
        this.serialList = serialList;
    }
    public void setTagList(TagList tagList) {
        this.tagList = tagList;
    }
    public void setAudioAlbum(String audioAlbum) {
        this.audioAlbum = audioAlbum;
    }
    public void setAudioAuthor(String audioAuthor) {
        this.audioAuthor = audioAuthor;
    }
    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }
}
