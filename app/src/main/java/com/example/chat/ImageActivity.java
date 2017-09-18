package com.example.chat;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageActivity extends BaseActivity {
    private ImageView imageView;
    private String imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);//全屏
        //getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
        setContentView(R.layout.activity_image);
        Intent intent=getIntent();
        imageView=(ImageView)findViewById(R.id.imageBig);
        imagePath=intent.getStringExtra("imagePath");
        Glide.with(this).load(imagePath)//内部有子线程
                //.placeholder(R.mipmap.ic_launcher)使用占位图会导致一直为占位图
                .into(imageView);
    }
    @Override
    public void onDestroy(){
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        super.onDestroy();
        Log.d("onDestroy","!!!!!!!!!!!!!!!!!!!!");

    }
}
