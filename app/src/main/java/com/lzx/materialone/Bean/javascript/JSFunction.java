package com.lzx.materialone.Bean.javascript;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;

/**
 * Created by lizhe on 2017/6/15.
 */

public class JSFunction {
    private Context mContext;
    public JSFunction(Context context){
        mContext = context;
    }

    @JavascriptInterface
    public void openUrlBySystem(String url){
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        mContext.startActivity(intent);
    }
}
