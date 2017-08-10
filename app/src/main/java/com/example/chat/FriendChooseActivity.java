package com.example.chat;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.chat.db.User;
import com.example.chat.gson.UserAccount;
import com.example.chat.service.ChatService;

import okhttp3.WebSocket;

public class FriendChooseActivity extends BaseActivity {
    UserAccount userAccount;
    private ChatService.ChatBinder chatBinder;
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            chatBinder=(ChatService.ChatBinder)iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        userAccount=(UserAccount)intent.getSerializableExtra("User");
        setContentView(R.layout.activity_friend_choose);//代码的顺序会对逻辑有影响，若这句比account靠前，
        //则不会在碎片的标题栏上显示账号
        Intent bindIntent=new Intent(this,ChatService.class);
        bindService(bindIntent,connection,BIND_AUTO_CREATE);
    }
    public UserAccount getUserAccount() {
        return userAccount;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        chatBinder.webSocketClose();
        unbindService(connection);
        //webSocket.close(1000,null);//一定要在活动和服务中销毁时将连接中断！！！，否则服务器端会出现错误
        //但是如果使用后台杀死该程序的话，则不会调用该函数
    }
}
