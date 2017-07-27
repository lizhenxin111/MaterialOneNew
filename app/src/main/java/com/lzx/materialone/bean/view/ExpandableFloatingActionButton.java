package com.lzx.materialone.bean.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.PopupWindow;

/**
 * 自定义FloatingActionButton，点击可弹出菜单
 */

public class ExpandableFloatingActionButton extends FloatingActionButton {
    private boolean isShowing = false;
    private PopupWindow popupWindow;
    public ExpandableFloatingActionButton(Context context) {
        super(context);
    }

    public ExpandableFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandableFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addContentView(View view, int width, int height){
        popupWindow = new PopupWindow(view, width, height, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(true);
    }
    public void showContentView(int x, int y){
        isShowing = true;
        popupWindow.getContentView().measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int i = popupWindow.getContentView().getMeasuredHeight();
        popupWindow.showAsDropDown(this, x, -y-i-popupWindow.getHeight());
    }
    public void dismissContentView(){
        isShowing = false;
        popupWindow.dismiss();
    }
    public boolean isShowing(){
        return isShowing;
    }
}
