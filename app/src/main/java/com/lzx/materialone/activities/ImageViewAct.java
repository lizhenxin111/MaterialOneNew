package com.lzx.materialone.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzx.materialone.R;
import com.lzx.materialone.manager.DateManager;
import com.lzx.materialone.manager.ImageManager;
import com.lzx.materialone.manager.NetworkManager;

public class ImageViewAct extends AppCompatActivity implements View.OnLongClickListener {

    private static final int NO_NETWORK = 0;
    private static final int SAVE_COMPLETE = 1;
    private static final int FILE_EXIST = 2;

    private TextView author;
    private ImageManager imageManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        author = (TextView)findViewById(R.id.detail_pic_author);
        author.setText(getIntent().getStringExtra("author"));

        String url = getIntent().getStringExtra("imageUrl");
        ImageView imageView = (ImageView)findViewById(R.id.detail_imageview);
        //getImage(imageView, url);
        imageManager = new ImageManager();
        imageManager.setImageFromUrl(imageView, url, 100, true);

        imageView.setOnLongClickListener(this);

        View rootView = (LinearLayout)findViewById(R.id.image_view_activity_root);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });
    }

    @Override
    public boolean onLongClick(View v) {
        PopupMenu popupMenu = new PopupMenu(ImageViewAct.this, v, Gravity.RIGHT);
        popupMenu.getMenuInflater().inflate(R.menu.image_long_click, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (ContextCompat.checkSelfPermission(ImageViewAct.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ImageViewAct.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else {
                    getImage();
                }
                return true;
            }
        });
        popupMenu.show();
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.main_fragment_content), getString(R.string.permission_denied), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }else {
                    getImage();
                }
                break;
            default:
        }
    }

    //节省代码
    private void getImage(){
        int flag = saveImage();
        String message = null;
        if (flag == FILE_EXIST){
            message = getString(R.string.image_exist);
        }else if (flag == SAVE_COMPLETE){
            message = getString(R.string.save_complete);
        }else if (flag == NO_NETWORK){
            message = getString(R.string.update_offline);
        }
        Snackbar snackbar = Snackbar.make(findViewById(R.id.image_view_activity_root),
                message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    //保存图片
    private int saveImage(){
        NetworkManager networkManager = new NetworkManager(ImageViewAct.this);
        if (networkManager.getNetworkState() != NetworkManager.NONE){
            ImageManager imageManager = new ImageManager();
            int result = imageManager.saveImage(ImageViewAct.this, getIntent().getStringExtra("imageUrl"),
                    Environment.getExternalStorageDirectory().toString(),
                    DateManager.getYear() + getIntent().getStringExtra("date") + ".jpg");
            if (result == ImageManager.IMAGE_EXIST){
                return FILE_EXIST;
            }else if (result == ImageManager.SAVE_SECCESSED){
                return SAVE_COMPLETE;
            }else{
                return 3;
            }
        }else {
            return NO_NETWORK;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        finishAfterTransition();
    }
}
