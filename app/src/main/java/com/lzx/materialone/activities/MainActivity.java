package com.lzx.materialone.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.lzx.materialone.bean.download.LocalReceiver;
import com.lzx.materialone.bean.view.FragCircle;
import com.lzx.materialone.R;
import com.lzx.materialone.adapters.MyFragmentPagerAdapter;
import com.lzx.materialone.day_mvp.DayFrag;
import com.lzx.materialone.manager.MyNotificationManager;
import com.lzx.materialone.manager.NetworkManager;
import com.lzx.materialone.manager.UpdateManager;
import com.lzx.materialone.services.MusicService;
import com.lzx.materialone.share.SinaMicroBlog.Constants;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, WbShareCallback {

    private int day = 0;
    private boolean isRegisted = false;
    private List<Fragment> fragmentList;

    private LocalReceiver localReceiver;

    private NetworkManager networkManager;

    private MyFragmentPagerAdapter myFragmentPagerAdapter;

    private ViewPager mainViewPager;
    private DrawerLayout drawer;
    private SwipeRefreshLayout swipeRefreshLayout;

    Intent musicIntent;

    private static WbShareHandler wbShareHandler;
    public static WbShareHandler getWbShareHandler(){
        return wbShareHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WbSdk.install(this, new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE));

        wbShareHandler = new WbShareHandler(this);
        wbShareHandler.registerApp();

        networkManager = new NetworkManager(this);

        musicIntent = new Intent(MainActivity.this, MusicService.class);
        startService(musicIntent);

        initUI();
        initViewPager();

        SharedPreferences preferences = getSharedPreferences("EnterCount", MODE_PRIVATE);
        SharedPreferences.Editor spEditor = preferences.edit();
        spEditor.putBoolean("Entered", true);
        spEditor.commit();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else {
            if (networkManager.getNetworkState() != NetworkManager.NONE){
                changeFragment(day);
                checkUpdate();
            } else {
                changeFragment(-1);
            }
        }
    }

    private void initUI(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                changeFragment(day);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        TextView dateTV = (TextView)findViewById(R.id.main_date);
        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(0);
                MyNotificationManager.showToast(MainActivity.this, "返回到今天", Toast.LENGTH_SHORT);
            }
        });
    }

    private void initViewPager(){
        FragCircle f1 = new FragCircle();
        FragCircle f2 = new FragCircle();
        FragCircle f3 = new FragCircle();

        fragmentList = new ArrayList<>();
        fragmentList.add(f1);
        fragmentList.add(f2);
        fragmentList.add(f3);

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        mainViewPager = (ViewPager)findViewById(R.id.main_view_Pager);
        mainViewPager.setAdapter(myFragmentPagerAdapter);
        mainViewPager.setCurrentItem(1, false);
        mainViewPager.addOnPageChangeListener(new MyPageChangeListener());

        //close SwipeFreshLayout when scroll ViewPager
        mainViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_MOVE:
                        swipeRefreshLayout.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        swipeRefreshLayout.setEnabled(true);
                        break;
                    default:
                }
                return false;
            }
        });
    }

    private void checkUpdate(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                if (sp.getBoolean("autoupdate", false)){
                    String info = null;
                    final UpdateManager updateManager = new UpdateManager(MainActivity.this);
                    try {
                        info = updateManager.checkUpdate("https://raw.githubusercontent.com/lizhenxin111/ApkStore/master/MaterialOneUpdate", "version", "info");
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (info != null && !info.equals("没有更新")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle(getString(R.string.update_title));
                        builder.setMessage(info);
                        builder.setPositiveButton(getString(R.string.update_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    updateManager.update("https://raw.githubusercontent.com/lizhenxin111/ApkStore/master/MaterialOneUpdate", "url");
                                    IntentFilter intentFilter = new IntentFilter();
                                    intentFilter.addAction("com.lzx.broadcast.DOWNLOAD_COMPLETE");
                                    localReceiver = new LocalReceiver(MainActivity.this);
                                    LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(localReceiver, intentFilter);
                                    isRegisted = true;
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder.setNegativeButton(getString(R.string.update_no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.show();
                    }
                }
            }
        }).start();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        final TextView tv = (TextView)findViewById(R.id.main_offline_textview);
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    tv.setText(getString(R.string.permission_denied));
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent localIntent = new Intent();
                            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (Build.VERSION.SDK_INT >= 9) {
                                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                            } else if (Build.VERSION.SDK_INT <= 8) {
                                localIntent.setAction(Intent.ACTION_VIEW);
                                localIntent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
                                localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
                            }
                            startActivity(localIntent);
                            System.exit(0);
                        }
                    });
                }else {
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (networkManager.getNetworkState() != NetworkManager.NONE){
                                changeFragment(0);
                            }
                        }
                    });
                    networkManager = new NetworkManager(this);
                    if (networkManager.getNetworkState() != NetworkManager.NONE){
                        changeFragment(0);
                    }
                }
                break;
            default:
        }
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_day) {
            // Handle the camera action
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_tip){
            showTips();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showTips(){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.motion_tip))
                .setMessage(getString(R.string.motion_tip_content))
                .show();
    }

    private void changeFragment(final int mDay){
        if (mDay == -1){
            FragCircle f = new FragCircle();
            f.setContent(getString(R.string.main_offline_text));
            fragmentList.set(1, f);
            fragmentList.clear();
            fragmentList.add(f);
            f.setOnContentClickListener(new FragCircle.OnContentClickListener() {
                @Override
                public void onClick(View v) {
                    if (networkManager.getNetworkState() != NetworkManager.NONE){
                        changeFragment(0);
                        checkUpdate();
                    }
                }
            });
            myFragmentPagerAdapter.notifyDataSetChanged();
        } else {
            DayFrag frag = new DayFrag();
            Bundle bundle = new Bundle();
            bundle.putInt("day", mDay);
            frag.setArguments(bundle);
            fragmentList.set(1, frag);
            myFragmentPagerAdapter.notifyDataSetChanged();
        }
    }

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //Log.d("pager", "onPageScrolled : position : " + position + "     positionOffset : " + positionOffset + "     positionOffsetPixels : " + positionOffsetPixels);
            if (position == 0 || position == 2){
                mainViewPager.setCurrentItem(1, false);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (networkManager.getNetworkState() != NetworkManager.NONE){
                if (position == 0){
                    if (day == 0){
                        MyNotificationManager.showToast(MainActivity.this, "今天", Toast.LENGTH_SHORT);
                    } else {
                        Toast.makeText(MainActivity.this, "上一天", Toast.LENGTH_SHORT).show();
                        changeFragment(--day);
                    }
                } else if (position == 2){
                    if (day == 10){
                        MyNotificationManager.showToast(MainActivity.this, "最多显示十天的内容", Toast.LENGTH_SHORT);
                    } else {
                        Toast.makeText(MainActivity.this, "下一天", Toast.LENGTH_SHORT).show();
                        changeFragment(++day);
                    }
                }
            }else {
                changeFragment(-1);     //添加无防落连接的Fragment
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }


    /*
    * 微博分享的回调函数
    * */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        wbShareHandler.doResultIntent(intent, this);
    }

    @Override
    public void onWbShareSuccess() {
        MyNotificationManager.showToast(this, "分享成功", Toast.LENGTH_SHORT);
    }

    @Override
    public void onWbShareCancel() {
        MyNotificationManager.showToast(this, "取消分享", Toast.LENGTH_SHORT);
    }

    @Override
    public void onWbShareFail() {
        MyNotificationManager.showToast(this, "分享失败", Toast.LENGTH_SHORT);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegisted){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);
        }
        stopService(musicIntent);
    }
}
