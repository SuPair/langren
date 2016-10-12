package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.jinhanyu.jack.langren.Constants;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.Me;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.SoundEffectManager;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.parse.Parse;
import com.parse.ParseObject;

import java.net.URISyntaxException;

import io.socket.client.IO;


public class SplashActivity extends AppCompatActivity {


    public void init() {

        MainApplication.ServerHost = getSharedPreferences("ip",MODE_PRIVATE).getString("ip",null);

        if(MainApplication.ServerHost==null)
            MainApplication.ServerHost = Constants.ServerHost;
        Fresco.initialize(getApplicationContext());

        ParseObject.registerSubclass(UserInfo.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("langrensha")
                .clientKey("")
                .server(Constants.makeNewIpAddress(MainApplication.ServerHost) + "/parse")
                .enableLocalDataStore()
                .build()
        );


        initSocket();

        SoundEffectManager.init(this);
    }


    private void initSocket() {
        try {
            if (MainApplication.socket == null) {
                IO.Options options = new IO.Options();
                options.reconnection = false;
                MainApplication.socket = IO.socket(Constants.makeNewIpAddress(MainApplication.ServerHost) + "/msg",options);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Me.get() != null) {
                    Me.update();
                    startActivity(new Intent(SplashActivity.this, SelectRoomActivity.class));
                    finish();
                } else {
                    SoundEffectManager.play(R.raw.dark);
                    startActivity(new Intent(SplashActivity.this, LoginActivty.class));
                    finish();
                }
            }
        }, 3000);

        init();

    }
}
