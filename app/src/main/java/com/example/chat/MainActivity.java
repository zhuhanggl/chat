package com.example.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.chat.gson.UserAccount;
import com.example.chat.util.HttpUtil;
import com.example.chat.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.chat.util.Utility.handleAccountResponse;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button loginButton;
    private Button signUpButton;
    private CheckBox remember_passwordCheckBox;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    private TextView ttt;
    private ImageView xxx;
    String account;
    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accountEdit=(EditText)findViewById(R.id.Account);
        passwordEdit=(EditText)findViewById(R.id.Password);
        ttt=(TextView)findViewById(R.id.ttt);
        xxx=(ImageView)findViewById(R.id.xxx);
        remember_passwordCheckBox=(CheckBox)findViewById(R.id.remember_password);
        loginButton=(Button)findViewById(R.id.login);
        loginButton.setOnClickListener(this);
        signUpButton=(Button)findViewById(R.id.sign_up);
        signUpButton.setOnClickListener(this);
        pref=getSharedPreferences("data",MODE_PRIVATE);
        editor=getSharedPreferences("data",MODE_PRIVATE).edit();
        Boolean isRememberPassword=pref.getBoolean("remember_password",false);
        if(isRememberPassword){
            accountEdit.setText(pref.getString("account",""));
            passwordEdit.setText(pref.getString("password",""));
            remember_passwordCheckBox.setChecked(true);
        }
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.login:
                account=accountEdit.getText().toString();
                password=passwordEdit.getText().toString();
                if(remember_passwordCheckBox.isChecked()){
                    editor.putString("account",account);
                    editor.putString("password",password);
                    editor.putBoolean("remember_password",true);
                }else{
                    editor.clear();
                }
                editor.apply();
                HttpUtil.sendOkHttpRequest("http://192.168.1.108/account.json",new okhttp3.Callback(){
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
                });
                break;
            case R.id.sign_up:
                Glide.with(this).load("http://192.168.1.108/1/apple_pic.png").into(xxx);
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
                break;
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

}
