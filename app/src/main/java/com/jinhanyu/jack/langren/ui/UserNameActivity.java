package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jinhanyu.jack.langren.R;

public class UserNameActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText game_number;
    private EditText game_password;
    private Button next;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_name);


        game_number = (EditText) findViewById(R.id.game_number);
        game_password = (EditText) findViewById(R.id.game_password);
        next = (Button) findViewById(R.id.next);

        next.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next://注册页面的 账号及密码 保存到数据库
                String username = game_number.getText().toString();
                String password = game_password.getText().toString();


                Intent intent = new Intent(UserNameActivity.this, UserHeadActivity.class);
                startActivity(intent);
                break;
        }
    }
}
