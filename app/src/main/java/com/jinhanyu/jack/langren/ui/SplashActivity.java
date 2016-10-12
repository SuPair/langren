package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.jinhanyu.jack.langren.Me;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.SoundEffectManager;


public class SplashActivity extends AppCompatActivity {


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


    }
}
