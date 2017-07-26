package com.example.chat;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.chat.db.User;
import com.example.chat.gson.UserAccount;

public class FriendChooseActivity extends AppCompatActivity {
    UserAccount userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        userAccount=(UserAccount)intent.getSerializableExtra("User");
        setContentView(R.layout.activity_friend_choose);//代码的顺序会对逻辑有影响，若这句比account靠前，
        //则不会在碎片的标题栏上显示账号
    }
    public UserAccount getUserAccount() {
        return userAccount;
    }
}
