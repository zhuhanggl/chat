package com.example.chat;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chat.gson.UserAccount;
import com.example.chat.service.ChatService;
import com.example.chat.service.IPupdate;
import com.example.chat.util.EchoWebSocketListener;
import com.example.chat.util.HttpUtil;
import com.example.chat.util.Utility;

import org.json.JSONArray;
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

public class ChatActivity extends BaseActivity implements View.OnClickListener{
    private EditText sentText;
    private Button sentButton;
    private TextView friendName;
    private List<Chat> mChatList=new ArrayList<>();
    private Friend friend;
    private UserAccount userAccount;
    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    public DrawerLayout drawerLayout;
    private Button backFriendChooseActivity;
    private WebSocket webSocket;
    private LocalReceiver localReceiver;
    private IntentFilter intentFilter;
    private LocalBroadcastManager localBroadcastManager;
    private ChatService.ChatBinder chatBinder;
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            chatBinder=(ChatService.ChatBinder)iBinder;
            webSocket=chatBinder.getWebSocket();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
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
        friendName=(TextView)findViewById(R.id.friend_name);
        friendName.setText(friend.getName());
        backFriendChooseActivity=(Button)findViewById(R.id.back_FriendChooseActivity);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        recyclerView=(RecyclerView)findViewById(R.id.chat_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        chatAdapter=new ChatAdapter(mChatList,userAccount);
        recyclerView.setAdapter(chatAdapter);
        intentFilter=new IntentFilter();
        intentFilter.addAction("com.example.chat.service.message");
        localReceiver=new LocalReceiver();
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);
        Intent bindIntent=new Intent(ChatActivity.this,ChatService.class);
        bindService(bindIntent,connection,BIND_AUTO_CREATE);
        chatInit();
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
                    jsonObject.put("ToId",friend.getFriendId());
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
    public UserAccount getUserAccount() {
        return userAccount;
    }


    public void sendMessage(String string){
        Chat chat=new Chat(friend,string,Chat.TYPE_SENT);
        mChatList.add(chat);
        //chatAdapter.notifyItemInserted(mChatList.size()-1);//动态过程中要注意刷新！！
        recyclerView.scrollToPosition(mChatList.size()-1);
        sentText.setText("");
    }
    private void showMessage(final Friend friend,final String message,final int messageType){
        //由于friend和message和messageType要应用于内部类，所以要定义为final
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Chat chat=new Chat(friend,message,messageType);
                mChatList.add(chat);
                //chatAdapter.notifyItemInserted(mChatList.size()-1);//动态过程中要注意刷新！！
                recyclerView.scrollToPosition(mChatList.size()-1);
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
        unbindService(connection);
        //webSocket.close(1000,null);//一定要在活动和服务中销毁时将连接中断！！！，否则服务器端会出现错误
        //但是如果使用后台杀死该程序的话，则不会调用该函数
    }

    class LocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context,Intent intent){

        }
    }

    private void chatInit(){
        HttpUtil.sendOkHttpChatInit("http://" + HttpUtil.localIP + ":8080/okhttp3_test/LoginServlet"
                , userAccount, friend, new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData=response.body().string();
                        try{
                            JSONArray jsonArray=new JSONArray(responseData);
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                String fromAccount=jsonObject.getString("FromAccount");
                                String toAccount=jsonObject.getString("ToAccount");
                                String message=jsonObject.getString("Message");
                                if (fromAccount.equals(friend.getAccount())){
                                    showMessage(friend,message,Chat.TYPE_RECEIVED);
                                }
                                if (toAccount.equals(friend.getAccount())){
                                    showMessage(friend,message,Chat.TYPE_SENT);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }
                });
    }

}
