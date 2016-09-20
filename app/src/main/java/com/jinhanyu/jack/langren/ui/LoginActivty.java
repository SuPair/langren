package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivty extends AppCompatActivity implements View.OnClickListener {
    private EditText game_number;
    private EditText game_password;
    private TextView forget_password;
    private ImageButton game_login;
    private TextView game_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        game_number = (EditText) findViewById(R.id.game_number);
        game_password = (EditText) findViewById(R.id.game_password);
        forget_password = (TextView) findViewById(R.id.forget_password);
        game_login = (ImageButton) findViewById(R.id.game_login);
        game_register = (TextView) findViewById(R.id.game_register);


        forget_password.setOnClickListener(this);
        game_login.setOnClickListener(this);
        game_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forget_password:

                break;
            case R.id.game_login:

                final String username = game_number.getText().toString();
                final String password = game_password.getText().toString();
                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e != null) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivty.this, "登录失败", Toast.LENGTH_SHORT).show();
                        } else {
                            MainApplication.userInfo.populateFromParseServer(user);
                            Intent intent1 = new Intent(LoginActivty.this, SelectRoomActivity.class);
                            startActivity(intent1);
                            finish();
                        }
                    }
                });


                break;
            case R.id.game_register:
                Intent intent = new Intent(this, UserNameActivity.class);
                startActivity(intent);
                break;
        }
    }
}
