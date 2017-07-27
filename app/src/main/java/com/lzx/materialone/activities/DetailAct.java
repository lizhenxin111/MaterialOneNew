package com.lzx.materialone.activities;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.lzx.materialone.bean.javascript.JSFunction;
import com.lzx.materialone.bean.view.Loading;
import com.lzx.materialone.R;
import com.lzx.materialone.manager.ImageManager;
import com.lzx.materialone.manager.NetworkManager;

import java.io.IOException;
import java.io.InputStream;

public class DetailAct extends AppCompatActivity implements View.OnLongClickListener {

    private WebView webView;
    private Loading loading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        loading = new Loading(this, R.style.loading, 50);
        loading.show();
        initToolbar();
        initWebView();
    }

    private void initToolbar(){
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
        imageView.setOnLongClickListener(this);
    }

    private void initWebView(){
        webView = (WebView)findViewById(R.id.detail_activity_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                //super.onPageFinished(view, url);
                webView.loadUrl(getJsFromFile("Javascript/remove"));
                webView.loadUrl("javascript:remove()");
                loading.dismiss();
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
        return true;
    }

    @Override
    public void onBackPressed() {

        if (webView.canGoBack()){
            webView.goBack();
        }else {
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
        }
    }

    @Override
    public boolean onLongClick(View v) {
        final PopupMenu menu = new PopupMenu(this, v, Gravity.CENTER);
        menu.getMenuInflater().inflate(R.menu.image_long_click, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.save_image){
                    if (new NetworkManager(DetailAct.this).getNetworkState() != NetworkManager.NONE){
                        int result = new ImageManager().saveImage(DetailAct.this, getIntent().getStringExtra("imageUrl"));
                        String message = null;
                        if (result == ImageManager.IMAGE_EXIST){
                            message = getString(R.string.image_exist);
                        }else {
                            message = getString(R.string.save_complete);
                        }
                        Toast.makeText(DetailAct.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
        menu.show();
        return true;
    }
}
