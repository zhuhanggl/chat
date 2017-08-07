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
import android.widget.Toast;

import com.example.chat.gson.UserAccount;
import com.example.chat.service.IPupdate;
import com.example.chat.util.EchoWebSocketListener;
import com.example.chat.util.HttpUtil;
import com.example.chat.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

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
    private WebSocket webSocket;
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
        connect(friend);
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
                JSONObject jsonObject=new JSONObject();
                try{
                    jsonObject.put("From",userAccount.getAccount());
                    jsonObject.put("To",friend.getAccount());
                    jsonObject.put("Message",sentText.getText().toString());
                }catch (JSONException e){
                    e.printStackTrace();
                }
                webSocket.send(jsonObject.toString());
                sendMessage(sentText.getText().toString());
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

    private void connect(Friend friend) {
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("Id",userAccount.getFriendsId());
            jsonObject.put("Account",userAccount.getAccount());
            jsonObject.put("Name",userAccount.getName());//生成的不带括号
            jsonObject.put("FriendId",friend.getFriendId());
            jsonObject.put("FriendAccount",friend.getAccount());
        }catch (JSONException e){
            e.printStackTrace();
        }
        HttpUtil.FriendChatConnect("ws://"+HttpUtil.localIP+":8080/okhttp3_test/websocket/{"
                +jsonObject.toString()+"}",friend, new WebSocketListener() {
                    @Override
                    public void onOpen(WebSocket webSocket, Response response) {
                        super.onOpen(webSocket, response);
                        showResponse("连接成功");
                        ChatActivity.this.webSocket=webSocket;
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, String text) {
                        super.onMessage(webSocket, text);
                        showResponse(text);
                    }

                    @Override
                    public void onMessage(WebSocket webSocket, ByteString bytes) {
                        super.onMessage(webSocket, bytes);
                    }

                    @Override
                    public void onClosing(WebSocket webSocket, int code, String reason) {
                        super.onClosing(webSocket, code, reason);
                    }

                    @Override
                    public void onClosed(WebSocket webSocket, int code, String reason) {
                        super.onClosed(webSocket, code, reason);
                    }

                    @Override
                    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                        super.onFailure(webSocket, t, response);
                    }
                });
    }
    /*private void connect(Friend friend) {
        HttpUtil.sendOkHttpFriendIP("http://" + HttpUtil.localIP + ":8080/okhttp3_test/LoginServlet",
                friend,new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String FriendIP=response.body().string();
                        showResponse(FriendIP);
                        Request request = new Request.Builder()
                                .url("ws://echo.websocket.org")
                                .build();
                        OkHttpClient client = new OkHttpClient();
                        client.newWebSocket(request, new WebSocketListener() {
                            @Override
                            public void onOpen(WebSocket webSocket, Response response) {
                                super.onOpen(webSocket, response);
                                showResponse("连接成功");
                                ChatActivity.this.webSocket=webSocket;
                                webSocket.send("hello world");
                            }

                            @Override
                            public void onMessage(WebSocket webSocket, String text) {
                                super.onMessage(webSocket, text);
                                showResponse(text);
                            }

                            @Override
                            public void onMessage(WebSocket webSocket, ByteString bytes) {
                                super.onMessage(webSocket, bytes);
                            }

                            @Override
                            public void onClosing(WebSocket webSocket, int code, String reason) {
                                super.onClosing(webSocket, code, reason);
                            }

                            @Override
                            public void onClosed(WebSocket webSocket, int code, String reason) {
                                super.onClosed(webSocket, code, reason);
                            }

                            @Override
                            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                                super.onFailure(webSocket, t, response);
                            }
                        });

                        client.dispatcher().executorService().shutdown();
                    }
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }
                });
    }*/
    public void sendMessage(String string){
        Chat chat=new Chat(friend,string,Chat.TYPE_SENT);
        mChatList.add(chat);
        //chatAdapter.notifyItemInserted(mChatList.size()-1);//动态过程中要注意刷新！！
        recyclerView.scrollToPosition(mChatList.size()-1);
        sentText.setText("");
    }
    public void receiveMessage(String string){
        Chat chat=new Chat(friend,string,Chat.TYPE_RECEIVED);
        mChatList.add(chat);
        //chatAdapter.notifyItemInserted(mChatList.size()-1);//动态过程中要注意刷新！！
        recyclerView.scrollToPosition(mChatList.size()-1);
        sentText.setText("");
    }
    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                receiveMessage(response);
            }
        });
    }
}
