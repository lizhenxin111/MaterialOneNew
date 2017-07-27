package com.lzx.materialone.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lzx.materialone.bean.view.Loading;
import com.lzx.materialone.R;

import java.io.File;
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
    public void setImageFromUrl(final ImageView imageView, String url, int quality, final boolean adjusted){
        Glide.with(MyApp.getContext()).asBitmap().load(url).into(imageView);
    }

    /*
    new SimpleTarget<Bitmap>(){
            @Override
            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                resource.setHeight(imageView.getMeasuredHeight());
                imageView.setImageBitmap(resource);
            }
        }
    * */

    public int saveImage(final Context context, final String url){

        final String fileName = url.hashCode() + ".jpg";   //用hashcode命名图片，以唯一区别图片
        //创建目录
        File dir = new File(filePath);
        if (!dir.exists()){
            dir.mkdir();
        }
        //检查文件是否存在
        final File file = new File(filePath, fileName);
        if (file.exists()){
            return IMAGE_EXIST;
        }else {
            /*Glide.with(MyApp.getContext()).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
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
                    MyApp.getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
                }
            });*/

            new AsyncTask<Void, Void, Void>(){
                Loading loading = new Loading(context, R.style.loading);
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    loading.show();
                }

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url(url).build();
                        Response response = client.newCall(request).execute();
                        InputStream inputStream = response.body().byteStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        FileOutputStream fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    loading.dismiss();
                }
            }.execute();


            return SAVE_SECCESSED;
        }
    }


}
