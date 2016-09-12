package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jinhanyu.jack.langren.R;

public class UserHeadActivity extends AppCompatActivity implements View.OnClickListener {
    private SurfaceView suifaceview;
    private ImageView camera;
    private ImageView photo;
    private Button complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_head);

        suifaceview = (SurfaceView) findViewById(R.id.surfaceview);
        camera = (ImageView) findViewById(R.id.camera);
        photo = (ImageView) findViewById(R.id.photo);
        complete = (Button) findViewById(R.id.complete);

        camera.setOnClickListener(this);
        photo.setOnClickListener(this);
        complete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera://摄像头


                break;
            case R.id.photo://相册


                break;
            case R.id.complete://完成 跳转到主页面(可以用线程做下计时)
                Intent intent = new Intent(this, LoginActivty.class);
                startActivity(intent);
                break;
        }

    }
}
