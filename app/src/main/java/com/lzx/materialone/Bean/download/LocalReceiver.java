package com.lzx.materialone.Bean.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.lzx.materialone.R;

import java.io.File;

/**
 * Created by lizhe on 2017/6/17.
 */

public class LocalReceiver extends BroadcastReceiver {
    private Context mContext;
    public LocalReceiver(){

    }
    public LocalReceiver(Context context){
        mContext = context;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String fileUrl = intent.getStringExtra("downloadUrl");
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/"));
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        File file = new File(directory + fileName);

        if (!file.exists())
        {
            Toast.makeText(mContext, mContext.getString(R.string.file_not_exist), Toast.LENGTH_LONG).show();
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);

        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            //provider authorities
            Uri apkUri = FileProvider.getUriForFile(context, "com.mydomain.fileprovider", file);
            //Granting Temporary Permissions to a URI
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            i.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }

        context.startActivity(i);
    }
}
