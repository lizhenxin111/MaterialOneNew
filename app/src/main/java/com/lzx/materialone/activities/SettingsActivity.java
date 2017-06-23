package com.lzx.materialone.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.app.AlertDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MenuItem;

import com.lzx.materialone.Bean.download.LocalReceiver;
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
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey().equals("update")){
            NetworkManager networkManager = new NetworkManager(this);
            if (networkManager.getNetworkState() != NetworkManager.NONE){
                String info = null;
                final UpdateManager updateManager = new UpdateManager(this);
                try {
                    info = updateManager.checkUpdate("https://raw.githubusercontent.com/lizhenxin111/ApkStore/master/MaterialOneUpdate", "version", "info");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.update_title));
                if (info == null){
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
                builder.setNegativeButton(getString(R.string.update_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }else {
                Snackbar snackbar = Snackbar.make(getCurrentFocus(), getString(R.string.update_offline), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        }
        return true;
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
