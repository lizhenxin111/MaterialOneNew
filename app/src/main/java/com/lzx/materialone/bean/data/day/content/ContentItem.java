package com.lzx.materialone.bean.data.day.content;

import java.util.List;

/**
 * Created by lizhe on 2017/4/23.
 */

public class ContentItem {
    private String id;
    private String category;
    private String display_category;
    private String item_id;
    private String title;
    private String forward;
    private String img_url;
    private String like_count;
    private String post_date;
    private String last_update_date;
    private Author author;
    private String video_url;
    private String audio_url;
    private String audio_platform;
    private String start_video;
    private String volume;
    private String pic_info;
    private String words_info;
    private String subTitle;
    private String number;
    private String serial_id;
    private List<Integer> serial_list;
    private String movie_story_id;
    private String ad_id;
    private String ad_type;
    private String ad_pvurl;
    private String ad_linkurl;
    private String ad_makettime;
    private String ad_closetime;
    private String ad_share_cnt;
    private String ad_pvurl_vendor;
    private String content_id;
    private String content_type;
    private String content_bgcolor;
    private List<TagList> tag_list;      //tag_list是一个数组！！！
    private String share_url;

    //仅在电台栏目里有
    private String music_name;
    private String audio_author;
    private String audio_album;

    public String getId(){
        return id;
    }
    public String getTitle(){
        return title;
    }
    public String getForward(){
        return forward;}
    public String getImgUrl(){
        return img_url;}
    public Author getAuthor(){return author;}
    public String getVideoUrl(){return video_url;}

    public String getAudioUrl() {
        return audio_url;
    }

    public String getVolume(){return volume;}
    public String getPicInfo(){
        return pic_info;}
    public String getWordsInfo(){
        return words_info;
    }
    public String getNumber(){return number;}
    public String getSerialId(){return serial_id;}
    public List<Integer> getSerialList(){return serial_list;}
    public String getMovieStoryId(){return movie_story_id;}
    public String getContentId(){return content_id;}
    public String getContentType(){return content_type;}
    public TagList getTagList(){
        if (tag_list.isEmpty()){
            return null;
        }else {
            return tag_list.get(0);
        }
    }
    public String getCategory(){return category;}
    public String getItemId(){return item_id;}
    public String getAudioPlatform(){return audio_platform;}
    public String getSubTitle(){return subTitle;}
    public String getShareUrl() {
        return share_url;
    }

    public String getMusicName(){return music_name;}
    public String getAudioAuthor(){return audio_author;}
    public String getAudioAlbum(){return audio_album;}
}
