package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences =getSharedPreferences(MainApplication.login_preference_name,MODE_PRIVATE);
                boolean hasLogin = preferences.getBoolean("login",false);
                if(hasLogin)
                    ParseUser.logInInBackground(preferences.getString("username",""), preferences.getString("password",""), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (e != null) {
                                e.printStackTrace();
                                Toast.makeText(SplashActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                            } else {
                                MainApplication.userInfo.setUserId(user.getObjectId());
                                ParseFile file = (ParseFile) user.get("head");
                                if(file!=null)
                                  MainApplication.userInfo.setHead(file.getUrl());
                                else{
                                    MainApplication.userInfo.setHead("res://com.jinhanyu.jack.langren/"+R.mipmap.d_doge);
                                }
                                MainApplication.userInfo.setName(user.getUsername());
                                MainApplication.userInfo.setScore((Integer) user.get("score"));
                                Intent intent1 = new Intent(SplashActivity.this, SelectRoomActivity.class);
                                startActivity(intent1);
                                finish();
                            }
                        }
                    });
                else
                {
                    startActivity(new Intent(SplashActivity.this,LoginActivty.class));
                    finish();
                }
            }
        }, 2000);

    }
}
