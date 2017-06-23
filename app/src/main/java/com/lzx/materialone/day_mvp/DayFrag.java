package com.lzx.materialone.day_mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzx.materialone.Bean.data.common_item.ItemData;
import com.lzx.materialone.Bean.view.DayItem;
import com.lzx.materialone.Bean.view.DayPic;
import com.lzx.materialone.Bean.view.ExpandableFloatingActionButton;
import com.lzx.materialone.R;
import com.lzx.materialone.activities.DetailAct;
import com.lzx.materialone.activities.DetailMusicAct;
import com.lzx.materialone.activities.ImageViewAct;

/**
 * Created by lizhe on 2017/6/3.
 */

public class DayFrag extends Fragment implements IDayFrag {
    private int date;
    Presenter presenter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_day, container, false);


        //滑动隐藏和显示FloatingActionButton
        NestedScrollView nsv = (NestedScrollView)view.findViewById(R.id.fragment_day_root);
        nsv.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                ExpandableFloatingActionButton fab = (ExpandableFloatingActionButton)getActivity().findViewById(R.id.main_fab);
                if (scrollY > oldScrollY){
                    fab.hide();
                }else if (oldScrollY - scrollY > 10){       //降低显示的灵敏度
                    fab.show();
                }
            }
        });
        Bundle bundle = getArguments();
        int day = bundle.getInt("day");
        presenter = new Presenter(this, day);
        presenter.setAllItem(view);
        return view;
    }

    @Override
    public void setContentItem(View view, String category, String picUrl, String title, String author, String abstracts, String contentUrl, String imageUrl, String audioUrl, int contentTag, int id) {
        DayItem dayItem = new DayItem(getActivity(), getFragmentManager());
        dayItem.setId(id);
        dayItem.setCategory(category);
        dayItem.setPicByUrl(picUrl);
        dayItem.setTitle(title);
        dayItem.setAuthor(author);
        dayItem.setAbstracts(abstracts);
        dayItem.setContentUrl(contentUrl);
        dayItem.setImageUrl(imageUrl);
        dayItem.setAudioUrl(audioUrl);
        dayItem.setContentTag(contentTag);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.fd_root_layout);
        layout.addView(dayItem, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void setPic(View view, String picUrl, String picAuthor, String words, String wordsAuthor, String url) {
        DayPic dayPic = new DayPic(getActivity(), getFragmentManager(),  ImageViewAct.class);
        dayPic.setId(0);
        dayPic.setPicByUrl(picUrl);
        dayPic.setPicAuthor(picAuthor);
        dayPic.setWords(words);
        dayPic.setWordsAuthor(wordsAuthor);
        dayPic.setUrl(url);
        dayPic.setDate(getDate());
        LinearLayout layout = (LinearLayout)view.findViewById(R.id.fd_root_layout);
        layout.addView(dayPic, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void setDate(String date) {
        TextView tv = (TextView)getActivity().findViewById(R.id.main_date);
        tv.setText(date);
    }

    @Override
    public String getDate() {
        TextView tv = (TextView)getActivity().findViewById(R.id.main_date);
        return tv.getText().toString();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void removeLoading() {

    }
}
