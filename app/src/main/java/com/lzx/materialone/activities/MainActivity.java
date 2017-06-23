package com.lzx.materialone.activities;

import android.Manifest;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lzx.materialone.Bean.view.ExpandableFloatingActionButton;
import com.lzx.materialone.R;
import com.lzx.materialone.day_mvp.DayFrag;
import com.lzx.materialone.manager.NetworkManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private int day = 0;
    private ExpandableFloatingActionButton expandableFloatingActionButton;
    private NetworkManager networkManager;
    private List<Fragment> fragments = new ArrayList<Fragment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        expandableFloatingActionButton = (ExpandableFloatingActionButton)findViewById(R.id.main_fab);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.fab_click, null);
        FloatingActionButton pre = (FloatingActionButton)view.findViewById(R.id.previous_day);
        FloatingActionButton next = (FloatingActionButton)view.findViewById(R.id.next_day);
        FloatingActionButton today = (FloatingActionButton)view.findViewById(R.id.refresh);
        pre.setOnClickListener(this);
        next.setOnClickListener(this);
        today.setOnClickListener(this);
        expandableFloatingActionButton.addContentView(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        expandableFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableFloatingActionButton.showContentView(0, expandableFloatingActionButton.getHeight());
            }
        });

        SharedPreferences preferences = getSharedPreferences("EnterCount", MODE_PRIVATE);
        SharedPreferences.Editor spEditor = preferences.edit();
        spEditor.putBoolean("Entered", true);
        spEditor.commit();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }else {
            TextView tv = (TextView)findViewById(R.id.main_offline_textview);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (networkManager.getNetworkState() != NetworkManager.NONE){
                        changeFragment(day);
                    }
                }
            });
            networkManager = new NetworkManager(this);
            if (networkManager.getNetworkState() != NetworkManager.NONE){
                changeFragment(day);
            }
        }
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
                                changeFragment(day);
                            }
                        }
                    });
                    networkManager = new NetworkManager(this);
                    if (networkManager.getNetworkState() != NetworkManager.NONE){
                        changeFragment(day);
                    }
                }
                break;
            default:
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            System.exit(0);
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
        } else if (id == R.id.nav_share) {
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void changeFragment(int day){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        DayFrag frag = new DayFrag();
        Bundle bundle = new Bundle();
        bundle.putInt("day", day);
        frag.setArguments(bundle);
        fragments.add(frag);
        transaction.replace(R.id.main_fragment_content, frag).commit();
    }

    private void removeFragment(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.remove(fragments.get(fragments.size()-1)).commit();
    }

    private void showSnackbar(View view, String msg){
        Snackbar snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onClick(View v) {
        if (networkManager.getNetworkState() == NetworkManager.NONE) {
            if (!fragments.isEmpty()) {
                removeFragment();
            }
        } else {
            switch (v.getId()) {
                case R.id.previous_day:
                    if (day != 0) {
                        changeFragment(--day);
                    } else {
                        showSnackbar(getCurrentFocus(), "今天");
                    }
                    break;
                case R.id.next_day:
                    if (day != 10) {
                        changeFragment(++day);
                    } else {
                        showSnackbar(getCurrentFocus(), "最多显示十天的内容");
                    }
                    break;
                case R.id.refresh:
                    changeFragment(0);
                    showSnackbar(getCurrentFocus(), "返回到今天  ");
                    break;
                default:
                    break;
            }
            expandableFloatingActionButton.dismissContentView();
        }
    }
}
