package com.example.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.db.Friends;
import com.example.chat.db.User;
import com.example.chat.gson.UserAccount;
import com.example.chat.service.ChatService;
import com.example.chat.service.IPupdate;
import com.example.chat.util.HttpUtil;
import com.example.chat.util.SignUpActivity;
import com.example.chat.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.chat.util.Utility.handleAccountResponse;

public class MainActivity extends BaseActivity implements View.OnClickListener{
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button loginButton;
    private Button signUpButton;
    private Button testButton;
    private CheckBox remember_passwordCheckBox;
    private ImageView userAvatar;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    private TextView ttt;
    private ImageView xxx;
    String account;
    String password;
    String userAvatarAddress;
    List<User>userList;
    UserAccount userAccountForSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accountEdit=(EditText)findViewById(R.id.Account);
        passwordEdit=(EditText)findViewById(R.id.Password);
        userAvatar=(ImageView)findViewById(R.id.user_Avatar);
        remember_passwordCheckBox=(CheckBox)findViewById(R.id.remember_password);
        loginButton=(Button)findViewById(R.id.login);
        loginButton.setOnClickListener(this);
        signUpButton=(Button)findViewById(R.id.sign_up);
        signUpButton.setOnClickListener(this);
        testButton=(Button)findViewById(R.id.test);
        testButton.setOnClickListener(this);
        pref=getSharedPreferences("data",MODE_PRIVATE);
        editor=getSharedPreferences("data",MODE_PRIVATE).edit();
        Boolean isRememberPassword=pref.getBoolean("remember_password",false);
        if(isRememberPassword){
            accountEdit.setText(pref.getString("account",""));
            passwordEdit.setText(pref.getString("password",""));
            //ttt.setText(pref.getString("userAvatarAddress",""));
            Glide.with(this).load("http://"+HttpUtil.localIP+"/"+
                    pref.getString("userAvatarAddress","")+".png").into(userAvatar);
            remember_passwordCheckBox.setChecked(true);
        }
    }
    @Override
    public void onClick(View v){//由于是局域网所以用手机调试的时候使用流量登录不了我的搭建的这个服务器，要
        // 连接wifi
        switch (v.getId()){
            case R.id.login:
                account=accountEdit.getText().toString();
                password=passwordEdit.getText().toString();
                HttpUtil.sendOkHttpLogin("http://" + HttpUtil.localIP + ":8080/okhttp3_test/LoginServlet",
                        account, password, new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseData=response.body().string();
                                if(responseData.equals("x")){
                                    showResponse("用户名不存在的");
                                }else if (responseData.equals("q")){
                                    showResponse("密码错误");
                                }else {
                                    Log.d("MainActivity","login");
                                    UserAccount userAccount=Utility.handleAccountResponse(responseData);
                                    showResponse(userAccount.getPassword());
                                    Intent chatIntent=new Intent(MainActivity.this, ChatService.class);
                                    chatIntent.putExtra("User",userAccount);
                                    startService(chatIntent);
                                    Intent intent = new Intent(MainActivity.this, FriendChooseActivity.class);
                                    intent.putExtra("User",userAccount);
                                    startActivity(intent);
                                }
                            }
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }
                        });
                /*account=accountEdit.getText().toString();
                password=passwordEdit.getText().toString();
                if(remember_passwordCheckBox.isChecked()){
                    editor.putString("account",account);
                    editor.putString("password",password);
                    editor.putBoolean("remember_password",true);
                }else{
                    editor.clear();
                    DataSupport.deleteAll(Friends.class);
                    DataSupport.deleteAll(User.class);
                }

                userList=DataSupport.findAll(User.class);//glide有十分强大的缓存机制，详见网页收藏的Glide的基本用法
                if (userList.size()>0){
                    boolean accountExist=false;
                    for(int i=0;i<userList.size();i++){
                        if(userList.get(i).getAccount().equals(account)){
                            accountExist=true;
                            if (userList.get(i).getPassword().equals(password)){
                                showResponse("OK!(DB)");
                                UserAccount userAccount=new UserAccount();
                                userAccount.setAccount(userList.get(i).getAccount());
                                userAccount.setPassword(userList.get(i).getPassword());
                                userAccount.setName(userList.get(i).getName());
                                userAccount.setAvatar(userList.get(i).getAvatar());
                                userAccount.setFriendsId(userList.get(i).getFriendsId());
                                userAvatarAddress=userList.get(i).getAvatar();
                                editor.putString("userAvatarAddress",userAvatarAddress);
                                Intent IPUpdateIntent=new Intent(this, IPupdate.class);
                                IPUpdateIntent.putExtra("User",userAccount);
                                startService(IPUpdateIntent);
                                Intent intent = new Intent(this, FriendChooseActivity.class);
                                intent.putExtra("User",userAccount);
                                startActivity(intent);
                            }else{
                                showResponse("password wrong!(DB)");
                            }
                            break;
                        }
                    }
                    if (!accountExist){
                        showResponse("No this account!(DB)");
                    }
                }else{
                    HttpUtil.sendOkHttpLogin("http://" + HttpUtil.localIP + ":8080/okhttp3_test/LoginServlet",
                            account, password, new Callback() {
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String responseData=response.body().string();
                                    if(responseData.equals("x")){
                                        showResponse("用户名不存在的");
                                    }else if (responseData.equals("q")){
                                        showResponse("密码错误");
                                    }else {
                                        showResponse(responseData);
                                        showResponse("登录中");
                                        UserAccount userAccount=Utility.handleAccountResponse(responseData);
                                        showResponse(userAccount.getPassword());
                                        Intent IPUpdateIntent=new Intent(MainActivity.this, IPupdate.class);
                                        IPUpdateIntent.putExtra("User",userAccount);
                                        startService(IPUpdateIntent);
                                        Intent intent = new Intent(MainActivity.this, FriendChooseActivity.class);
                                        intent.putExtra("User",userAccount);
                                        startActivity(intent);
                                    }
                                }
                                @Override
                                public void onFailure(Call call, IOException e) {

                                }
                            });

                }
                editor.apply();*/
                break;
            case R.id.sign_up:
                Intent intent=new Intent(MainActivity.this, SignUpActivity.class);
                startActivityForResult(intent,1);
                /*Glide.with(this).load("http://192.168.1.108/1/apple_pic.png").into(xxx);
                HttpUtil.sendOkHttpRequest("http://192.168.1.108/1/1.json",new okhttp3.Callback(){
                    @Override
                    public void onResponse(Call call,Response response)throws IOException{
                        String responseData=response.body().string();
                        showResponse(responseData);
                    }
                    @Override
                    public void onFailure(Call call,IOException e){
                        e.printStackTrace();
                    }
                });
                Intent intent=new Intent(this, TestChatActivity.class);
                startActivity(intent);*/
                break;
            case R.id.test:
                HttpUtil.sendOkHttpMultipart("http://" + HttpUtil.localIP + ":8080/okhttp3_test/FileServlet"
                        , "/sdcard/t.png", new Callback() {
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {

                            }
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }
                        });
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    userAccountForSignUp=(UserAccount)data.getSerializableExtra("User");
                }
        }
    }

    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();
            }
        });
    }
/*HttpUtil.sendOkHttpRequest("http://"+ HttpUtil.localIP+":8080/okhttp3_test/LoginServlet",new okhttp3.Callback(){
                        @Override
                        public void onResponse(Call call,Response response)throws IOException{
                            String responseData=response.body().string();
                            List<UserAccount> UserAccountList=Utility.handleAccountResponse(responseData);
                            boolean accountExist=false;
                            for(int i=0;i<UserAccountList.size();i++){
                                if (UserAccountList.get(i).getAccount().equals(account)){
                                    accountExist=true;
                                    if (UserAccountList.get(i).getPassword().equals(password)){
                                        showResponse("OK!");
                                        User user=new User();
                                        user.setAccount(account);
                                        user.setPassword(password);
                                        user.setName(UserAccountList.get(i).getName());
                                        user.setAvatar(UserAccountList.get(i).getAvatar());
                                        user.setFriendsId(UserAccountList.get(i).getFriendsId());
                                        user.save();
                                        userAvatarAddress=UserAccountList.get(i).getAvatar();
                                        editor.putString("userAvatarAddress",userAvatarAddress);
                                        Intent intent = new Intent(MainActivity.this, FriendChooseActivity.class);
                                        intent.putExtra("User",UserAccountList.get(i));
                                        startActivity(intent);
                                    }else{
                                        showResponse("password wrong!");
                                    }
                                    break;
                                }
                            }
                            if (!accountExist){
                                showResponse("No this account!");
                            }
                        }

                        @Override
                        public void onFailure(Call call,IOException e){
                            e.printStackTrace();
                        }
                    });*/
    @Override
    protected void onDestroy(){//凡是绑定或打开过service的活动，都需要在最后解绑和停止service
        super.onDestroy();
        Intent stopIntent=new Intent(this,ChatService.class);
        stopService(stopIntent);
        //webSocket.close(1000,null);//一定要在活动和服务中销毁时将连接中断！！！，否则服务器端会出现错误
        //但是如果使用后台杀死该程序的话，则不会调用该函数
    }
}
