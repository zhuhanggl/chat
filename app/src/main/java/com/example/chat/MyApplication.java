package com.example.chat;

import android.app.Application;
import android.util.Log;

/**
 * Created by Administrator on 2017/8/8.
 */

public class MyApplication extends Application{//注意Application的生命周期！！！
    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level);
        //会发现，每次开启后台，即手机右下角那个正方形的时候，该方法就会执行
        //ActivityCollector.finishAll();
        Log.d("MyApplication","onTrimMemory!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }
    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        Log.d("MyApplication", "onTerminate!!!!!!!!!!!!!!!!!");
        ActivityCollector.finishAll();//这个要在super.onTerminate()前执行
        super.onTerminate();
    }
}
