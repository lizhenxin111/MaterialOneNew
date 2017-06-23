package com.lzx.materialone.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 网络管理类， 用于获取网络状态
 */

public class NetworkManager {
    private Context mContext;
    public static final int NONE = 0;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;

    public NetworkManager(Context context){
        mContext = context;
    }

    public int getNetworkState(){
        if (mContext != null){
            ConnectivityManager manager = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null){
                if (info.getType() == ConnectivityManager.TYPE_WIFI){
                    return TYPE_WIFI;
                }else if (info.getType() == ConnectivityManager.TYPE_MOBILE){
                    return TYPE_MOBILE;
                }
            }else {
                return NONE;
            }
        }
        return NONE;
    }
}
