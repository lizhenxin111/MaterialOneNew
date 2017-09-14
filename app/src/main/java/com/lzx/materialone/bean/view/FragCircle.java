package com.lzx.materialone.bean.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lzx.materialone.R;

/**
 * Created by lizhenxin on 17-7-22.
 */

public class FragCircle extends Fragment {

    public interface OnContentClickListener{
        void onClick(View v);
    }

    private OnContentClickListener mOnContentClickListener = null;

    private String mContent;
    private TextView mContentView;

    public void setContent(String content){
        mContent = content;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vp_circle, container, false);
        mContentView = (TextView)view.findViewById(R.id.fragment_vp_circle_tv);
        mContentView.setText(mContent);
        return view;
    }

    public void setOnContentClickListener(final OnContentClickListener onContentClickListener){
        mOnContentClickListener = onContentClickListener;
    }
}
