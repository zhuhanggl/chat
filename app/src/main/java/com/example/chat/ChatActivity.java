package com.example.chat;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chat.gson.UserAccount;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText sentText;
    private Button sentButton;
    private List<Chat> mChatList=new ArrayList<>();
    private Friend friend;
    private UserAccount userAccount;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    public DrawerLayout drawerLayout;
    private Button backFriendChooseActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        friend=(Friend)intent.getSerializableExtra("friend");//java是讲究顺序的！！！,不讲究顺序的是声明！
        userAccount=(UserAccount)intent.getSerializableExtra("user");
        //逻辑顺序还是有的！！！
        setContentView(R.layout.activity_chat);
        sentText=(EditText)findViewById(R.id.sent_text);
        sentButton=(Button)findViewById(R.id.sent_button);
        backFriendChooseActivity=(Button)findViewById(R.id.back_FriendChooseActivity);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        chatInit();
        recyclerView=(RecyclerView)findViewById(R.id.chat_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        chatAdapter=new ChatAdapter(mChatList,userAccount);
        recyclerView.setAdapter(chatAdapter);
        sentButton.setOnClickListener(this);
        backFriendChooseActivity.setOnClickListener(this);
    }
    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.sent_button:
                Chat chat=new Chat(friend,sentText.getText().toString(),Chat.TYPE_SENT);
                mChatList.add(chat);
                //chatAdapter.notifyItemInserted(mChatList.size()-1);//动态过程中要注意刷新！！
                recyclerView.scrollToPosition(mChatList.size()-1);
                sentText.setText("");
                break;
            case R.id.back_FriendChooseActivity:
                drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    private void chatInit(){
        Chat chat=new Chat(friend,"fengkuangneishe",Chat.TYPE_RECEIVED);
        mChatList.add(chat);
        chat=new Chat(friend,"shedaohuaiyun",Chat.TYPE_RECEIVED);
        mChatList.add(chat);
        chat=new Chat(friend,"kuangcao1xiaoshi",Chat.TYPE_RECEIVED);
        mChatList.add(chat);
    }
    public UserAccount getUserAccount() {
        return userAccount;
    }
}
