package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
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
                if(ParseUser.getCurrentUser()!=null){
                    ParseUser user = ParseUser.getCurrentUser();
                    MainApplication.userInfo.setUserId(user.getObjectId());
                    ParseFile file = (ParseFile) user.get("head");
                    MainApplication.userInfo.setHead(file.getUrl());
                    MainApplication.userInfo.setName(user.getUsername());
                    MainApplication.userInfo.setScore((Integer) user.get("score"));
                    startActivity(new Intent(SplashActivity.this,SelectRoomActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(SplashActivity.this,LoginActivty.class));
                    finish();
                }
            }
        },2000);

    }
}
