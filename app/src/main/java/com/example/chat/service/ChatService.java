package com.example.chat.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.chat.Chat;
import com.example.chat.ChatActivity;
import com.example.chat.Friend;
import com.example.chat.FriendChooseActivity;
import com.example.chat.R;
import com.example.chat.gson.UserAccount;
import com.example.chat.util.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ChatService extends Service {
    private Friend friend=null;
    private Friend otherFriend=null;
    private UserAccount userAccount;
    private WebSocket webSocket;
    private LocalBroadcastManager localBroadcastManager;
    private ChatBinder mBinder=new ChatBinder();
    private JSONObject jsonObject;
    public  class ChatBinder extends Binder {
        public void setFriend(Friend friend){
            ChatService.this.friend=friend;
        }
        public WebSocket getWebSocket(){
            return webSocket;
        }
        public void webSocketClose(){
            webSocket.close(1000,null);
        }
    }
    @Override
    public void onCreate(){
        super.onCreate();
        localBroadcastManager=LocalBroadcastManager.getInstance(this);
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        Log.d("ChatService","正在onStartCommand");
        userAccount=(UserAccount)intent.getSerializableExtra("User");
        connect();
        return super.onStartCommand(intent,flags,startId);
    }

    public ChatService() {
    }

    @Override
    public IBinder onBind(Intent intent) {//Bind一次后不会bind第二次
        Log.d("ChatService","正在binding");
        Intent intentBroad=new Intent("com.example.chat.service.message");
        intentBroad.putExtra("message","连接成功");
        localBroadcastManager.sendBroadcast(intentBroad);
        return mBinder;
    }

    private void connect() {
        final JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("Id",userAccount.getFriendsId());
            jsonObject.put("Account",userAccount.getAccount());
            jsonObject.put("Name",userAccount.getName());//生成的不带括号
            //jsonObject.put("FriendId",friend.getFriendId());
            //jsonObject.put("FriendAccount",friend.getAccount());
        }catch (JSONException e){
            e.printStackTrace();
        }
        //在手机中，通过左下角的返回键退出程序后，程序还是在内存中，不是真正的退出。但过一段时间系统会
        //把它所占内存回收掉，此时才是真正意义上的退出
        HttpUtil.FriendChatConnect("ws://"+HttpUtil.localIP+":8080/okhttp3_test/websocket/{"
                +jsonObject.toString()+"}",new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                ChatService.this.webSocket=webSocket;
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                try{
                    ChatService.this.jsonObject=new JSONObject(text);
                    if (friend.getAccount().equals(ChatService.this.jsonObject.getString("FromAccount"))){
                        Intent intent=new Intent("com.example.chat.service.message");
                        intent.putExtra("message",ChatService.this.jsonObject.getString("Message"));
                        localBroadcastManager.sendBroadcast(intent);//通过广播把收到的消息传到chatActivity
                    }else{
                        Log.d("ChatService","ELSE!!!!!");
                        HttpUtil.sendOkHttpFriendData("http://" + HttpUtil.localIP +
                                        ":8080/okhttp3_test/LoginServlet", userAccount.getFriendsId(),
                                ChatService.this.jsonObject.getString("FromAccount"), new Callback(){
                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        String responseData=response.body().string();
                                        try{
                                            Log.d("onMessage friendData",responseData);
                                            JSONObject friendJsonObject=new JSONObject(responseData);
                                            Log.d("onMessage setFriendId",friendJsonObject.getString("FriendId"));
                                            otherFriend=new Friend(friendJsonObject.getString("FriendId"),
                                                    friendJsonObject.getString("Account"),friendJsonObject.getString("Avatar"),
                                                    friendJsonObject.getString("Name"));
                                            Intent intent=new Intent(ChatService.this,ChatActivity.class);
                                            intent.setAction(""+System.currentTimeMillis());//很重要，不使用这个语句的话，Intent
                                            // 无法传递参数
                                            intent.putExtra("friend",otherFriend);
                                            intent.putExtra("user",userAccount);
                                            PendingIntent pi=PendingIntent.getActivity(ChatService.this,0,intent,0);
                                            Notification notification1=new NotificationCompat.Builder(ChatService.this)
                                                    .setContentTitle(otherFriend.getName())
                                                    .setContentText(ChatService.this.jsonObject.getString("Message"))
                                                    .setWhen(System.currentTimeMillis())
                                                    .setContentIntent(pi)
                                                    .setSmallIcon(R.mipmap.ic_launcher)
                                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                                                            R.mipmap.ic_launcher))
                                                    .build();
                                            startForeground(1,notification1);
                                        }catch (JSONException e){
                                            e.printStackTrace();
                                        }

                                    }
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }
                                });
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                webSocket.close(1000,null);
                Log.d("ChatActivity","正在onClosing");
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
    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d("ChatService","onDestroy()!!!!!!!!!!!!!!!!!!!!!");
        webSocket.close(1000,null);
    }

}
