package com.lzx.materialone.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lzx.materialone.R;
import com.lzx.materialone.bean.view.Loading;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 图片处理类
 * 方法：
 * 从网络链接获取图片并设为背景：public void setImageFromUrl(final View imageView, final String url)
 * 保存图片：public int saveImage(Context context, Bitmap bitmap, String savePath, String imageName)
 */

public class ImageManager {
    public static final int IMAGE_EXIST = 1;
    public static final int SAVE_SECCESSED = 2;
    private String filePath = Environment.getExternalStorageDirectory().toString();

    //缩放功能待添加
    public void setImageFromUrl(final Activity activity, final ImageView imageView, String url, boolean adjuest) {
        if (adjuest) {
            Glide.with(MyApp.getContext()).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    //adjust the height according to screen width
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    float screenWidth = displayMetrics.widthPixels;

                    float imageWidth = resource.getWidth();
                    float imageHeight = resource.getHeight();

                    float height = screenWidth / imageWidth * imageHeight;
                    imageView.setImageBitmap(Bitmap.createScaledBitmap(resource, (int) screenWidth, (int) height, true));
                }
            });
        } else {
            Glide.with(MyApp.getContext()).asBitmap().load(url).into(imageView);
        }
    }

    public int saveImage(final Context context, final String url) {

        Loading loading = new Loading(context, R.style.loading);
        final String fileName = url.hashCode() + ".jpg";   //用hashcode命名图片，以唯一区别图片
        //创建目录
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //检查文件是否存在
        final File file = new File(filePath, fileName);
        if (file.exists()) {
            return IMAGE_EXIST;
        } else {
            loading.show();
            Glide.with(context).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    //resource = BitmapFactory.decodeStream(inputStream);
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        resource.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            loading.dismiss();

            // 把文件插入到系统图库
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(),
                        file.getAbsolutePath(), fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 通知图库更新
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(file);
            intent.setData(uri);
            context.sendBroadcast(intent);
        }

        return SAVE_SECCESSED;
    }
}



