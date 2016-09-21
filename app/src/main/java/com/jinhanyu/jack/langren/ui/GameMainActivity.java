package com.jinhanyu.jack.langren.ui;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.GalleryAdapter;
import com.jinhanyu.jack.langren.util.RoundBitmapUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;


public class GameMainActivity extends CommonActivity implements View.OnClickListener {
    Gallery gallery;
    private ImageView gameRule, voiceLevel;
    private TextView identification;
    private GalleryAdapter adapter;
    private View gameRuleBg;
    private DrawerLayout drawerLayout;//侧滑（显示玩家详细信息）

    private View game_bg;
    private TextView identification_label;

    private boolean click = true;


    @Override
    protected void prepareViews() {
        setContentView(R.layout.game_main);
        gallery = (Gallery) findViewById(R.id.gallery_players_head);
        gameRule = (ImageView) findViewById(R.id.iv_gameStage_gameRule);
        voiceLevel = (ImageView) findViewById(R.id.iv_playStage_voiceLevel);
        identification = (TextView) findViewById(R.id.tv_playStage_identification);
        identification_label = (TextView) findViewById(R.id.identification_label);
        adapter = new GalleryAdapter(this, MainApplication.roomInfo.getUsers());
        gallery.setAdapter(adapter);
        gameRule.setOnClickListener(this);
        identification.setOnClickListener(this);
        gameRuleBg = findViewById(R.id.game_rule_bg);
        game_bg = findViewById(R.id.game_bg);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerView.setClickable(true);  //动态设置Clickbale可以解决穿透点击事件
        }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        BitmapDrawable bitmapDrawable = (BitmapDrawable) gameRuleBg.getBackground();
        gameRuleBg.setBackground(new BitmapDrawable(RoundBitmapUtils.getRoundedCornerBitmap(bitmapDrawable.getBitmap())));
    }

    protected void prepareSocket() {
        MainApplication.socket
                .on("start", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        int type = (int) args[0];
                        MainApplication.userInfo.getGameRole().setType(type);
                        Log.i("你的身份是", MainApplication.userInfo.getGameRole().getType().getName());
                        identification_label.post(new Runnable() {
                            @Override
                            public void run() {
                                identification_label.setText("您的身份是: "+MainApplication.userInfo.getGameRole().getType().getName());
                            }
                        });

                    }
                })
                .on("company", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        try {
                            JSONArray array = (JSONArray) args[0];
                            final List<String> companyNames = new ArrayList<String>();
                            for (int i = 0; i < array.length(); i++) {
                                String userId = (String) array.get(i);
                                companyNames.add(MainApplication.roomInfo.findUserInRoom(userId).getNickname());
                            }

                            Log.i("你的同伴是", companyNames.toString());
                            identification_label.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(GameMainActivity.this, "你的同伴是"+companyNames.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });

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
                                  Toast.makeText(GameMainActivity.this, "天黑了,兄弟们搞事情吧", Toast.LENGTH_SHORT).show();
                                  game_bg.setBackgroundResource(R.color.dark);
                              }
                          });
                    }
                })
                .on("roomInfo", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        boolean hasSaved = (boolean) args[0];
                        boolean hasPoisoned = (boolean) args[1];
                        String lastGuardedUserId = (String) args[2];
                        MainApplication.roomInfo.setHasSaved(hasSaved);
                        MainApplication.roomInfo.setHasPoisoned(hasPoisoned);
                        MainApplication.roomInfo.setLastGuardedUserId(lastGuardedUserId);
                    }
                })
                .on("light", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(GameMainActivity.this,GameMainActivity.class));
                                Toast.makeText(GameMainActivity.this, "天亮了,快别睡了", Toast.LENGTH_SHORT).show();
                                game_bg.setBackgroundResource(R.color.light);
                            }
                        });
                    }
                })
        .on("votePolice", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //开始选警长。
                identification_label.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GameMainActivity.this, "开始选警长。。。", Toast.LENGTH_SHORT).show();
                    }
                });
                startActivity(new Intent(GameMainActivity.this,VoteActivity.class).putExtra("type",0));
            }
        }).on("voteWolf", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                //开始票坏人。
                identification_label.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GameMainActivity.this, "开始票坏人。。。", Toast.LENGTH_SHORT).show();
                    }
                });
                startActivity(new Intent(GameMainActivity.this,VoteActivity.class).putExtra("type",1));
            }
        })
        .on("action", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                switch (MainApplication.userInfo.getGameRole().getType()){
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
                final StringBuilder sb = new StringBuilder();
                if(userId1==null&&userId2==null){
                    identification_label.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GameMainActivity.this, "今晚是平安夜", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    if(userId1!=null)
                        sb.append(MainApplication.roomInfo.findUserInRoom(userId1).getUsername()+"被杀  ");
                    if(userId2!=null)
                        sb.append(MainApplication.roomInfo.findUserInRoom(userId2).getUsername()+"被杀  ");
                    identification_label.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GameMainActivity.this, sb.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).on("startSpeak", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                identification_label.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GameMainActivity.this, "现在发言开始", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).on("speak", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                final String userId = (String) args[0];
                identification_label.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GameMainActivity.this, "现在"+MainApplication.roomInfo.findUserInRoom(userId).getUsername()+"开始发言", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).on("youSpeak", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                identification_label.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GameMainActivity.this, "现在轮到你发言", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).on("endSpeak", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                identification_label.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GameMainActivity.this, "发言结束", Toast.LENGTH_SHORT).show();
                    }
                });
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
        switch (v.getId()) {
            case R.id.iv_gameStage_gameRule:
                if (click) {
                    gameRuleBg.setVisibility(View.VISIBLE);
                    click = false;
                } else {
                    onBackPressed();
                }
                break;
            case R.id.tv_playStage_identification:
                Identification();
                break;
        }
    }

    //【游戏规则】是否可见
    @Override
    public void onBackPressed() {
        gameRuleBg.setVisibility(View.INVISIBLE);
        click = true;
    }

    //标记玩家身份
    public void Identification() {
        final String[] identity = new String[]{"狼人", "女巫", "猎人", "预言家", "守卫", "村民", "未知"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(GameMainActivity.this);
        builder.setTitle("请标记玩家身份");
        builder.setSingleChoiceItems(identity, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(GameMainActivity.this, "成功标记为:" + identity[which], Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(GameMainActivity.this, "未标记", Toast.LENGTH_SHORT).show();
                dialog.dismiss();//消失对话框
            }
        });
        builder.show();
    }


}

