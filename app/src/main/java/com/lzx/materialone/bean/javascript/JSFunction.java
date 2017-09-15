package com.lzx.materialone.bean.javascript;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * Created by lizhe on 2017/6/15.
 */

public class JSFunction {
    private Context mContext;
    public JSFunction(Context context){
        mContext = context;
    }

    @JavascriptInterface
    public void showSrc(String url){
        Log.d("JsFunction", "url : " + url);
    }

    public void addRemoveFun(WebView webView){
        webView.loadUrl("function remove(){\n" +
                "    var a = document.querySelector(\"div.header\");\n" +
                "    a.parentNode.removeChild(a);\n" +
                "    var c = document.querySelector(\"div.footer\");\n" +
                "    c.parentNode.removeChild(c);\n" +
                "    var d = document.querySelector(\"p.text-title\");\n" +
                "    d.parentNode.removeChild(d);\n" +
                "    var e = document.querySelector(\"hr.sort-separate-line15\");\n" +
                "    e.parentNode.removeChild(e);\n" +
                "}");
    }
}
