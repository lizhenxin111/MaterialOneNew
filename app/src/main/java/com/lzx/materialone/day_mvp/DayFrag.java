package com.lzx.materialone.day_mvp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lzx.materialone.bean.view.DayItem;
import com.lzx.materialone.bean.view.DayPic;
import com.lzx.materialone.bean.view.DayRadio;
import com.lzx.materialone.bean.view.Loading;
import com.lzx.materialone.R;
import com.lzx.materialone.activities.ImageViewAct;
import com.lzx.materialone.activities.MainActivity;
import com.lzx.materialone.manager.MyNotificationManager;
import com.lzx.materialone.manager.WbSDKManager;
import com.lzx.materialone.services.MusicService;

/**
 * Created by lizhe on 2017/6/3.
 */

public class DayFrag extends Fragment implements IDayFrag, View.OnLongClickListener, View.OnClickListener {
    private boolean isRegisted = false;

    private Presenter presenter;
    private Loading loading;
    private PopupWindow shareList;

    private View view;

    private DayPic dayPic;
    private DayRadio dayRadio;

    private static MusicService.MusicControlBinder binder;
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (MusicService.MusicControlBinder) service;
            isRegisted = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isRegisted = false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_day, container, false);
        Bundle bundle = getArguments();
        if (bundle != null && !bundle.isEmpty()){
            presenter = new Presenter(this, bundle.getInt("day", 0));
            presenter.setAllItem();
        }
        getActivity().bindService(new Intent(getActivity(), MusicService.class), connection, getActivity().BIND_AUTO_CREATE);

        return view;
    }

    @Override
    public void setContentItem(String category, String picUrl, String title, String author, String abstracts,
                               String contentUrl, String imageUrl, String audioUrl, int contentTag, int id) {
        DayItem dayItem = new DayItem(getActivity(), getActivity().getSupportFragmentManager());
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
    public void setPic(String picUrl, String picAuthor, String words, String wordsAuthor, String url) {
        dayPic = new DayPic(getActivity(), getFragmentManager(), ImageViewAct.class);
        dayPic.setId(0);
        dayPic.setPicByUrl(picUrl);
        dayPic.setPicAuthor(picAuthor);
        dayPic.setWords(words);
        dayPic.setWordsAuthor(wordsAuthor);
        dayPic.setUrl(url);
        dayPic.setDate(getDate());
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.fd_root_layout);
        dayPic.setOnLongClickListener(this);
        layout.addView(dayPic, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void setRadio(final String title, String author, final String imageUrl, final String audioUrl, final String articleUrl) {

        dayRadio = new DayRadio(getActivity());
        dayRadio.setParams(title, author, audioUrl, imageUrl, articleUrl);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.fd_root_layout);
        layout.addView(dayRadio, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dayRadio.setOnToggleClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binder != null){
                    switch (binder.getState()){
                        case MusicService.MEDIAPLAYER_NULL:
                            binder.init(audioUrl, title, audioUrl, imageUrl, articleUrl);
                            MyNotificationManager.showToast(getActivity(), getResources().getString(R.string.loading), Toast.LENGTH_SHORT);
                            dayRadio.setToggleImage(R.mipmap.bg_load);
                            binder.setOnPlayerPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    binder.play();
                                }
                            });
                            binder.setOnStateChangeListener(new MusicService.OnStateChangeListehner() {
                                @Override
                                public void onChanged() {
                                    dayRadio.setToggleImage(binder.isPlaying() ? R.mipmap.bg_pause : R.mipmap.bg_play);
                                }
                            });
                            break;
                        case MusicService.MEDIAPLAYER_PLAYING:
                            binder.pause();
                            break;
                        case MusicService.MEDIAPLAYER_PAUSING:
                            binder.play();
                            break;
                        default:
                    }
                }
            }
        });

    }

    @Override
    public void setDate(String date) {
        TextView tv = (TextView) getActivity().findViewById(R.id.main_date);
        tv.setText(date);
    }

    @Override
    public String getDate() {
        TextView tv = (TextView) getActivity().findViewById(R.id.main_date);
        return tv.getText().toString();
    }

    @Override
    public void showLoading() {
        loading = new Loading(getActivity(), R.style.loading);
        loading.show();
    }

    @Override
    public void removeLoading() {
        loading.dismiss();
    }

    @Override
    public boolean onLongClick(View v) {
        showPopupWindow(v);
        return true;
    }

    private void showPopupWindow(View v){
        View shareListView = LayoutInflater.from(getActivity()).inflate(R.layout.share_list, null);

        shareListView.findViewById(R.id.share_to_sina).setOnClickListener(this);

        shareList = new PopupWindow(shareListView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        shareList.setBackgroundDrawable(new ColorDrawable(0x00000000));
        shareList.setOutsideTouchable(true);
        //shareList.setAnimationStyle(R.style.shareWindowAnim);
        //shareList.update();
        shareList.showAtLocation(dayPic, Gravity.BOTTOM, 0, 0);
    }

    private void closePopupWindow(){
        shareList.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.share_to_sina:
                shareToSina();
                closePopupWindow();
                break;
            default:
        }
    }

    private void shareToSina(){
        WbSDKManager wbSDKManager = new WbSDKManager(getActivity());

        String shareText = dayPic.getSharedText();
        Bitmap shareImage = dayPic.getSharedImage();
        String title = "分享";
        String destription = "分享自MaterialOne";
        String actionUrl = dayPic.getUrl();
        String defaultText = "复杂的世界里，一个就够了";

        wbSDKManager.sendMessage(MainActivity.getWbShareHandler(), shareText, shareImage,
                title, destription, actionUrl, defaultText);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRegisted) {
            getActivity().unbindService(connection);
        }
    }
}
