package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.GalleryAdapter;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.jinhanyu.jack.langren.util.RoundBitmap;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;


public class GameMainActivity extends CommonActivity implements View.OnClickListener {
    Gallery gallery;
    private ImageView gameRule, voiceLevel;
    private TextView expand, identification;
    private GalleryAdapter adapter;
    private View gameRuleBg;
    private View game_bg;

    @Override
    protected void prepareViews() {
        setContentView(R.layout.game_main);
        gallery = (Gallery) findViewById(R.id.gallery_players_head);
        gameRule = (ImageView) findViewById(R.id.iv_gameStage_gameRule);
        voiceLevel = (ImageView) findViewById(R.id.iv_playStage_voiceLevel);
        expand = (TextView) findViewById(R.id.tv_playStage_expand);
        identification = (TextView) findViewById(R.id.tv_playStage_identification);
        adapter = new GalleryAdapter(this, MainApplication.currentRoomUsers);
        gallery.setAdapter(adapter);
        gameRule.setOnClickListener(this);
        expand.setOnClickListener(this);
        identification.setOnClickListener(this);
        gameRuleBg = findViewById(R.id.game_rule_bg);
        game_bg = findViewById(R.id.game_bg);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) gameRuleBg.getBackground();
        gameRuleBg.setBackground(new BitmapDrawable(RoundBitmap.getRoundedCornerBitmap(bitmapDrawable.getBitmap())));
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
                })
                .on("dark", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                          game_bg.post(new Runnable() {
                              @Override
                              public void run() {
                                  game_bg.setBackgroundResource(R.color.dark);
                              }
                          });
                    }
                })
                .on("light", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        game_bg.post(new Runnable() {
                            @Override
                            public void run() {
                                game_bg.setBackgroundResource(R.color.light);
                            }
                        });
                    }
                })
        .on("wolf_in", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Toast.makeText(GameMainActivity.this, "狼人开始活动，预言家、守卫开始行动。。。", Toast.LENGTH_SHORT).show();
            }
        })
        .on("wizard_in", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Toast.makeText(GameMainActivity.this, "女巫开始活动...", Toast.LENGTH_SHORT).show();
            }
        })
        .on("select_police", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //开始选警长。
                Toast.makeText(GameMainActivity.this, "开始选警长。。。", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(GameMainActivity.this,VoteActivity.class).putExtra("type",0));
            }
        })
        .on("action", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                switch (MainApplication.userInfo.getType()){
                    case Wolf:
                        startActivity(new Intent(getApplicationContext(),WolfActivity.class));
                        break;
                    case Guard:
                        startActivity(new Intent(getApplicationContext(),GuardActivity.class));
                        break;
                    case Wizard:
                        startActivity(new Intent(getApplicationContext(),WizardActivity.class));
                        break;
                    case Predictor:
                        startActivity(new Intent(getApplicationContext(),PredictorActivity.class));
                        break;
                }
            }
        }).on("darkResult", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String userId1 = (String) args[0];
                String userId2 = (String) args[1];
                StringBuilder sb = new StringBuilder();
                if(userId1==null&&userId2==null)
                    Toast.makeText(GameMainActivity.this, "今晚是平安夜", Toast.LENGTH_SHORT).show();
                else {
                    if(userId1!=null)
                        sb.append(MainApplication.findUserInRoom(userId1).getName()+"被杀  ");
                    if(userId2!=null)
                        sb.append(MainApplication.findUserInRoom(userId2).getName()+"被杀  ");

                    Toast.makeText(GameMainActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        })
        ;
    }





    @Override
    protected void unbindSocket() {
        MainApplication.socket.off("start").off("company");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_gameStage_gameRule:
                gameRuleBg.setVisibility(View.VISIBLE);
                break;
            case R.id.tv_playStage_expand:
                break;
            case R.id.tv_playStage_identification:
                break;
        }
    }


    @Override
    public void onBackPressed() {
        gameRuleBg.setVisibility(View.INVISIBLE);
    }
}

