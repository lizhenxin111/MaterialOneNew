package com.lzx.materialone.activities;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzx.materialone.Bean.javascript.JSFunction;
import com.lzx.materialone.R;
import com.lzx.materialone.manager.ImageManager;

import java.io.IOException;
import java.io.InputStream;

public class DetailAct extends AppCompatActivity {

    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.detail_collapsingtoolbar);
        Toolbar toolbar = (Toolbar)findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(null);
        TextView textView = (TextView)findViewById(R.id.detail_toolbar_title);
        textView.setText(getIntent().getStringExtra("title"));

        ImageView imageView = (ImageView)findViewById(R.id.detail_collapsing_imageview);
        ImageManager imageManager = new ImageManager();
        imageManager.setImageFromUrl(imageView, getIntent().getStringExtra("imageUrl"), 80, false);

        webView = (WebView)findViewById(R.id.detail_activity_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                webView.loadUrl(getJsFromFile("Javascript/remove"));
                webView.loadUrl("javascript:remove()");
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new JSFunction(DetailAct.this), "webview");
        webView.loadUrl(getIntent().getStringExtra("url"));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //finish();
                this.finishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (webView.canGoBack()){
            webView.goBack();
        }else {
            //super.onBackPressed();
            this.finishAfterTransition();
        }
    }

    private String getJsFromFile(String fileName){
        String js = null;
        try {
            InputStream inputStream = this.getAssets().open(fileName);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            js = new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return js;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null){
            webView.clearCache(true);
            webView.clearHistory();
            webView.destroy();
            webView = null;
        }
    }
}
