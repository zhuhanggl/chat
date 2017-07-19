package com.example.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText accountEdit;
    private EditText passwordEdit;
    private Button loginButton;
    private CheckBox remember_passwordCheckBox;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accountEdit=(EditText)findViewById(R.id.Account);
        passwordEdit=(EditText)findViewById(R.id.Password);
        remember_passwordCheckBox=(CheckBox)findViewById(R.id.remember_password);
        loginButton=(Button)findViewById(R.id.login);
        loginButton.setOnClickListener(this);
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
                String account=accountEdit.getText().toString();
                String password=passwordEdit.getText().toString();
                if(remember_passwordCheckBox.isChecked()){
                    editor.putString("account",account);
                    editor.putString("password",password);
                    editor.putBoolean("remember_password",true);
                }else{
                    editor.clear();
                }
                editor.apply();
                if(account.equals("zhuhanggl")&&password.equals("19930307")) {//不能用==！！
                    Intent intent = new Intent(this, FriendChoose.class);
                    startActivity(intent);
                }
                break;
        }

    }
}
