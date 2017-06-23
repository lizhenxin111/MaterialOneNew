package com.lzx.materialone.manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

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

    //缩放功能待添加
    public void setImageFromUrl(final View imageView, final String url, int quality, final boolean adjusted){
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                try {
                    Request request = new Request.Builder()
                            .url(url)
                            .build();
                    Response response = client.newCall(request).execute();
                    InputStream inputStream = response.body().byteStream();
                    final Drawable drawable = Drawable.createFromStream(inputStream, "pic");
                    imageView.post(new Runnable() {
                        @Override
                        public void run() {
                            //pic.setImageBitmap(bitmap);
                            imageView.setBackground(drawable);
                            if (adjusted){
                                int pWidth = drawable.getIntrinsicWidth();
                                int pHeight = drawable.getIntrinsicHeight();

                                WindowManager wm = (WindowManager)imageView.getContext().getSystemService(Context.WINDOW_SERVICE);
                                DisplayMetrics dm = new DisplayMetrics();
                                wm.getDefaultDisplay().getMetrics(dm);
                                int sWidth = dm.widthPixels;// 屏幕宽度（像素）

                                int iHeight = (int)(pHeight * sWidth / pWidth);
                                final ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                                lp.width = sWidth;
                                lp.height = iHeight;
                                imageView.setLayoutParams(lp);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public int saveImage(Context context, ImageView imageView, String savePath, String imageName){
        //创建路径
        File dir = new File(savePath);
        if (!dir.exists()){
            dir.mkdir();
        }

        File image = new File(savePath, imageName);     //创建图片文件
        if (image.exists()){
            return IMAGE_EXIST;
        }else {
            Bitmap bitmap = null;
            //下载图片
            FileOutputStream out = null;
            try {
                imageView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                imageView.layout(0, 0, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
                imageView.buildDrawingCache();
                bitmap = imageView.getDrawingCache();

                out = new FileOutputStream(image);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null){
                        out.flush();
                        out.close();
                        out = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //把图片插入到图库
            /*try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(), image.getAbsolutePath(), imageName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }*/
            if (!bitmap.isRecycled()){
                bitmap.recycle();
                bitmap = null;
                System.gc();
            }
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imageName)));
            return SAVE_SECCESSED;
        }
    }

    public int saveImage(Context context, final String url, String savePath, String imageName){
        //创建路径
        File dir = new File(savePath);
        if (!dir.exists()){
            dir.mkdir();
        }

        File image = new File(savePath, imageName);     //创建图片文件
        if (image.exists()){
            return IMAGE_EXIST;
        }else {
            Bitmap bitmap = null;
            //下载图片
            FileOutputStream out = null;
            try {
                Callable<Bitmap> callable = new Callable<Bitmap>() {
                    @Override
                    public Bitmap call() throws Exception {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(url).build();
                        Response response = client.newCall(request).execute();

                        return BitmapFactory.decodeStream(response.body().byteStream());
                    }
                };
                FutureTask<Bitmap> futureTask = new FutureTask<Bitmap>(callable);
                Thread thread = new Thread(futureTask);
                thread.start();
                bitmap = futureTask.get();
                out = new FileOutputStream(image);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null){
                        out.flush();
                        out.close();
                        out = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //把图片插入到图库
            try {
                MediaStore.Images.Media.insertImage(context.getContentResolver(), image.getAbsolutePath(), imageName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (!bitmap.isRecycled()){
                bitmap.recycle();
                bitmap = null;
                System.gc();
            }
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + imageName)));
            return SAVE_SECCESSED;
        }
    }
}
