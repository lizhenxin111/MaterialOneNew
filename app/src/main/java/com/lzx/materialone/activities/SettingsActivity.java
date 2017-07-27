package com.lzx.materialone.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.design.widget.Snackbar;
import android.app.AlertDialog;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;

import com.lzx.materialone.bean.download.LocalReceiver;
import com.lzx.materialone.bean.view.Loading;
import com.lzx.materialone.R;
import com.lzx.materialone.manager.NetworkManager;
import com.lzx.materialone.manager.UpdateManager;

import java.util.concurrent.ExecutionException;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private boolean ifUpdate = false;
    private boolean isRegisted = false;
    private LocalReceiver localReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.pref_main);

        findPreference("update").setOnPreferenceClickListener(this);
        //findPreference("ass").setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()){
            case "update":
                checkUpdate();
                return true;
            /*case "ass":
                startActivity(new Intent(SettingsActivity.this, SAAAct.class));
                return true;*/
            default:
        }
        return true;
    }


    private void checkUpdate(){
        NetworkManager networkManager = new NetworkManager(this);
        if (networkManager.getNetworkState() != NetworkManager.NONE){
            final Loading loading = new Loading(SettingsActivity.this, R.style.loading);
            final UpdateManager updateManager = new UpdateManager(SettingsActivity.this);

            new AsyncTask<Object, Object, String>(){
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    loading.show();
                }

                @Override
                protected String doInBackground(Object... params) {
                    String info = null;
                    try {
                        info = updateManager.checkUpdate("https://raw.githubusercontent.com/lizhenxin111/ApkStore/master/MaterialOneUpdate", "version", "info");
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    return info;
                }

                @Override
                protected void onPostExecute(String info) {
                    super.onPostExecute(info);
                    loading.dismiss();

                    AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                    builder.setTitle(getString(R.string.update_title));
                    if (info == null || info.equals("没有更新")){
                        builder.setMessage(getString(R.string.update_none));
                    }else {
                        builder.setMessage(info);
                        ifUpdate = true;
                    }
                    builder.setPositiveButton(getString(R.string.update_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (ifUpdate){
                                try {
                                    updateManager.update("https://raw.githubusercontent.com/lizhenxin111/ApkStore/master/MaterialOneUpdate", "url");

                                    IntentFilter intentFilter = new IntentFilter();
                                    intentFilter.addAction("com.lzx.broadcast.DOWNLOAD_COMPLETE");
                                    localReceiver = new LocalReceiver(SettingsActivity.this);
                                    LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(localReceiver, intentFilter);
                                    isRegisted = true;
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    if (ifUpdate) {
                        builder.setNegativeButton(getString(R.string.update_no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                    }
                    builder.show();
                }
            }.execute();
        }else {
            Snackbar snackbar = Snackbar.make(getCurrentFocus(), getString(R.string.update_offline), Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRegisted = true){
            LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
