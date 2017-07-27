package com.lzx.materialone.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.Toast;

import com.lzx.materialone.R;
import com.lzx.materialone.manager.DateManager;
import com.lzx.materialone.manager.ImageManager;
import com.lzx.materialone.manager.NetworkManager;

import java.io.File;

public class ImageViewAct extends AppCompatActivity implements View.OnLongClickListener {

    private static final int NO_NETWORK = 0;
    private static final int SAVE_COMPLETE = 1;
    private static final int FILE_EXIST = 2;


    private ImageView imageView;
    private TextView author;
    private ImageManager imageManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        init();
    }

    private void init(){
        ((TextView)findViewById(R.id.detail_pic_author)).setText(getIntent().getStringExtra("author"));

        String url = getIntent().getStringExtra("imageUrl");
        imageView = (ImageView)findViewById(R.id.detail_imageview);
        //getImage(imageView, url);
        imageManager = new ImageManager();
        imageManager.setImageFromUrl(imageView, url, 100, true);

        imageView.setOnLongClickListener(this);

        View rootView = findViewById(R.id.image_view_activity_root);
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

                ImageManager imageManager = new ImageManager();
                //imageManager.saveImage(ImageViewAct.this, imageView, Environment.getExternalStorageDirectory().toString(), DateManager.getYear() + getIntent().getStringExtra("date") + ".jpg");
                String message = null;
                int result = imageManager.saveImage(ImageViewAct.this, getIntent().getStringExtra("imageUrl"));
                if (result == ImageManager.IMAGE_EXIST){
                    message = getResources().getString(R.string.image_exist);
                }else {
                    message = getResources().getString(R.string.save_complete);
                }
                Toast.makeText(ImageViewAct.this, message, Toast.LENGTH_SHORT).show();
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

                }
                break;
            default:
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        this.finishAfterTransition();
    }
}
