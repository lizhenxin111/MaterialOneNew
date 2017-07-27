package com.lzx.materialone.manager;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

/**
 * Created by lizhe on 2017/7/1.
 */

public class MyApp extends Application{
    private static Context context;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate() {
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
