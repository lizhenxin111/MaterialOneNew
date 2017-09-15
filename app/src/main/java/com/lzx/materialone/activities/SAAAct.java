package com.lzx.materialone.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lzx.materialone.R;
import com.lzx.materialone.manager.MyNotificationManager;
import com.sina.weibo.sdk.auth.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

/*
* 社交应用授权页面
* */
public class SAAAct extends AppCompatActivity implements View.OnClickListener {

    private Oauth2AccessToken mWioboToken;
    private SsoHandler mWeiboHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saa);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mWeiboHandler = new SsoHandler(this);       //创建微博实例

        findViewById(R.id.authorize_to_sina).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.authorize_to_sina:
                authorizeToSina();
                break;
            default:
        }
    }

    private void authorizeToSina(){
        mWeiboHandler.authorize(new WeiboAuthListener());       //All in one方式进行SSO授权
        //mWeiboHandler. authorizeWeb(new WeiboAuthListener());     //网页方式进行SSO授权
    }


    /*
    * 微博授权接口
    * */
    private class WeiboAuthListener implements WbAuthListener{

        @Override
        public void onSuccess(final Oauth2AccessToken oauth2AccessToken) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mWioboToken = oauth2AccessToken;

                    if (mWioboToken.isSessionValid()){
                        AccessTokenKeeper.writeAccessToken(SAAAct.this, mWioboToken);       //写入token，保存到SharePreferences
                        MyNotificationManager.showToast(SAAAct.this, getString(R.string.authSuccess), Toast.LENGTH_SHORT);
                    }
                }
            });
        }

        @Override
        public void cancel() {
            MyNotificationManager.showToast(SAAAct.this, getString(R.string.authCancel), Toast.LENGTH_SHORT);
        }

        @Override
        public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
            MyNotificationManager.showToast(SAAAct.this, getString(R.string.authFailed), Toast.LENGTH_SHORT);
        }
    }


    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     *
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mWeiboHandler != null) {
            mWeiboHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
