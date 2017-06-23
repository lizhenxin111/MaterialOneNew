package com.lzx.materialone.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.lzx.materialone.Bean.download.DownloadService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 更新管理类
 * 检查更新：public String checkUpdate(final String jsonUrl, final String versionKey, @Nullable final String infoKey) throws ExecutionException, InterruptedException
 * 获取新文件链接：public String update(final String jsonUrl, final String apkKey) throws ExecutionException, InterruptedException
 */

public class UpdateManager {
    Context mContext;
    Intent intent;
    public UpdateManager(Context context){
        mContext = context;
        intent = new Intent(mContext, DownloadService.class);
        mContext.startService(intent);
        mContext.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    /*
    * 功能：获取更新信息
    * 前提：网络中包含版本信息的json文件
    * 参数：json文件地址， 版本号的key， 更新内容的key
    * 返回值：无更新：null
    *         有更新：更新内容 / "update"
    * */
    public String checkUpdate(final String jsonUrl, final String versionKey, @Nullable final String infoKey) throws ExecutionException, InterruptedException {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(jsonUrl)
                            .build();
                    Response response = client.newCall(request).execute();
                    String jsonData = response.body().string();
                    JSONArray jsonArray = new JSONArray(jsonData);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    int nVersion = jsonObject.getInt(versionKey);
                    PackageInfo pkg = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                    int oVersion = pkg.versionCode;
                    if (oVersion == nVersion){
                        return "没有更新";
                    }else{
                        if (infoKey != null){
                            return jsonObject.getString(infoKey);
                        }else {
                            return "update";
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        FutureTask<String> futureTask = new FutureTask<String>(callable);
        Thread thread = new Thread(futureTask);
        thread.start();
        return futureTask.get();
    }

    private DownloadService.DownloadBinder downloadBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    /*
    * 功能：获取安装包链接
    * 前提：网络中包含版本信息的json文件
    * 参数：json文件地址， 安装包的key
    * 返回值：安装包链接
    * */
    public void update(final String jsonUrl, final String apkKey) throws ExecutionException, InterruptedException {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(jsonUrl)
                        .build();
                Response response = client.newCall(request).execute();
                String json = response.body().string();
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                return jsonObject.getString(apkKey);
            }
        };
        final FutureTask<String> futureTask = new FutureTask<String>(callable);
        Thread thread = new Thread(futureTask);
        thread.start();
        final String fileUrl = futureTask.get();
        try {
            downloadBinder.startDownload(fileUrl);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mContext.unbindService(connection);
            mContext.stopService(intent);
        }
    }
}
