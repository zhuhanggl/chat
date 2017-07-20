package com.example.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class FriendChooseActivity extends AppCompatActivity {
    String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        account=intent.getStringExtra("titleAccount");
        setContentView(R.layout.activity_friend_choose);//代码的顺序会对逻辑有影响，若这句比account靠前，
        //则不会在碎片的标题栏上显示账号
    }
    public String getAccount() {
        return account;
    }
}
