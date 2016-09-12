package com.jinhanyu.jack.langren.ui;

import android.util.Log;
import android.view.View;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.GalleryAdapter;
import com.jinhanyu.jack.langren.entity.UserInfo;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class GameMainActivity extends CommonActivity implements View.OnClickListener {
    Gallery gallery;
    private ImageView gameRule, voiceLevel;
    private TextView expand, identification;
    private List<UserInfo> list;
    private GalleryAdapter adapter;


    @Override
    protected void prepareViews() {
        setContentView(R.layout.game_main);
        list = new ArrayList<>();
        gallery = (Gallery) findViewById(R.id.gallery_players_head);
        gameRule = (ImageView) findViewById(R.id.iv_gameStage_gameRule);
        voiceLevel = (ImageView) findViewById(R.id.iv_playStage_voiceLevel);
        expand = (TextView) findViewById(R.id.tv_playStage_expand);
        identification = (TextView) findViewById(R.id.tv_playStage_identification);
        adapter = new GalleryAdapter(this, list);
        gallery.setAdapter(adapter);
        gameRule.setOnClickListener(this);
        expand.setOnClickListener(this);
        identification.setOnClickListener(this);

    }

    protected void prepareSocket() {
        MainApplication.socket
                .on("start", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        int type = (int) args[0];
                        MainApplication.userInfo.setType(type);
                        Log.i("你的身份是", MainApplication.userInfo.getType().getName());
                    }
                })
                .on("company", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        try {
                            JSONArray array = (JSONArray) args[0];
                            List<String> companyNames = new ArrayList<String>();
                            for (int i = 0; i < array.length(); i++) {
                                String userId = (String) array.get(i);
                                for (UserInfo info : MainApplication.currentRoomUsers) {
                                    if (info.getUserId().equals(userId)) {
                                        info.setType(1);
                                        companyNames.add(info.getName());
                                        break;
                                    }
                                }
                            }

                            Log.i("你的同伴是", companyNames.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_gameStage_gameRule:
                break;
            case R.id.tv_playStage_expand:
                break;
            case R.id.tv_playStage_identification:
                break;
        }
    }
}
