package com.jinhanyu.jack.langren.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.GalleryAdapter;
import com.jinhanyu.jack.langren.adapter.WaitRoomAdapter;
import com.jinhanyu.jack.langren.entity.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class GameMainActivity extends AppCompatActivity implements View.OnClickListener{
    Gallery gallery;
    private ImageView gameRule,voiceLevel;
    private TextView expand,identification;
    private List<UserInfo> list;
    private GalleryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main);
        list=new ArrayList<>();
        gallery= (Gallery) findViewById(R.id.gallery_players_head);
        gameRule= (ImageView) findViewById(R.id.iv_gameStage_gameRule);
        voiceLevel= (ImageView) findViewById(R.id.iv_playStage_voiceLevel);
        expand= (TextView) findViewById(R.id.tv_playStage_expand);
        identification= (TextView) findViewById(R.id.tv_playStage_identification);
        adapter=new GalleryAdapter(this,list);
        gallery.setAdapter(adapter);
        gameRule.setOnClickListener(this);
        expand.setOnClickListener(this);
        identification.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_gameStage_gameRule:
                break;
            case R.id.tv_playStage_expand:
                break;
            case R.id.tv_playStage_identification:
                break;
        }
    }
}
