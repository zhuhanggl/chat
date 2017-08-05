package com.example.chat.util;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.example.chat.MainActivity;
import com.example.chat.R;
import com.example.chat.gson.UserAccount;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SignUpActivity extends AppCompatActivity {
    private Button ok;
    private EditText account;
    private EditText password;
    private EditText name;
    private EditText AvatarName;
    private UserAccount userAccount=new UserAccount();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ok=(Button)findViewById(R.id.ok);
        account=(EditText)findViewById(R.id.Account);
        password=(EditText)findViewById(R.id.Password);
        name=(EditText)findViewById(R.id.Name);
        AvatarName=(EditText)findViewById(R.id.Avatar_name);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userAccount.setAccount(account.getText().toString());
                userAccount.setPassword(password.getText().toString());
                userAccount.setName(name.getText().toString());
                userAccount.setAvatar(AvatarName.getText().toString());
                userAccount.setIp("");
                Intent intent=new Intent();
                intent.putExtra("User",userAccount);
                setResult(RESULT_OK,intent);
                HttpUtil.sendOkHttpSignUp("http://"+ HttpUtil.localIP+":8080/okhttp3_test/LoginServlet", userAccount
                        , new Callback() {//不能用localhost
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseData=response.body().string();
                                showResponse(responseData);
                            }
                            @Override
                            public void onFailure(Call call, IOException e) {
                            }
                        });
                finish();
            }
        });
    }
    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SignUpActivity.this,response,Toast.LENGTH_SHORT).show();
            }
        });
    }
}
