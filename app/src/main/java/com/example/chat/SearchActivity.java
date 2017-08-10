package com.example.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat.gson.UserAccount;
import com.example.chat.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {
    private EditText searchText;
    private Button addFriend;
    private UserAccount userAccount;
    private String friendAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchText=(EditText) findViewById(R.id.search_text);
        addFriend=(Button)findViewById(R.id.add_friend);
        Intent intent=getIntent();
        userAccount=(UserAccount)intent.getSerializableExtra("User");
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friendAccount=searchText.getText().toString();
                Log.d("SearchActivity",friendAccount);
                HttpUtil.sendOkHttpAddFriend("http://" + HttpUtil.localIP + ":8080/okhttp3_test/LoginServlet",
                        userAccount, friendAccount, new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseData=response.body().string();
                                if(responseData.equals("a")){
                                    showResponse("已添加好友：账号"+friendAccount);
                                }
                                if(responseData.equals("x")){
                                    showResponse("没有该用户！");
                                }
                            }
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }
                        });
            }
        });
    }
    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SearchActivity.this,response,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
