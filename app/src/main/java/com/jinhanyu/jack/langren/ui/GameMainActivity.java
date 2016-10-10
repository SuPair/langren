package com.jinhanyu.jack.langren.ui;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.langren.Constants;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.Me;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.SoundEffectManager;
import com.jinhanyu.jack.langren.TickTimer;
import com.jinhanyu.jack.langren.VoiceManager;
import com.jinhanyu.jack.langren.adapter.GalleryAdapter;
import com.jinhanyu.jack.langren.adapter.GameDetailAdapter;
import com.jinhanyu.jack.langren.entity.GameRole;
import com.jinhanyu.jack.langren.entity.RoomInfo;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.jinhanyu.jack.langren.util.RoundBitmapUtils;
import com.jinhanyu.jack.langren.util.ScreenUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class GameMainActivity extends CommonActivity implements View.OnClickListener {
    //玩家画廊
    private Gallery gallery;
    private GalleryAdapter adapter;


    //游戏规则
    private ImageView gameRule;
    private PopupWindow popupWindow;


    //发言特效
    private ImageView voiceLevel;
    private AnimationDrawable speakAnim;

    //标记玩家身份
    private TextView mark;
    //游戏背景
    private View game_bg;
    //玩家身份
    private TextView identification_label;
    //玩家大头像
    private SimpleDraweeView bigHead;

    //侧滑（显示玩家详细信息）
    private DrawerLayout drawerLayout;
    private ListView gameDetailListview;
    private GameDetailAdapter gameDetailAdapter;



    //发言按钮
    private View bt_endSpeak;
    //狼人自爆按钮
    private View bt_wolf_destroy;
    //游戏提示、时间面板
    private TextView time_label, tv_game_hint;
    private View speak_time_label;
    //定时器
    private TickTimer tickTimer;


    private VoiceManager voiceManager = VoiceManager.getInstance(MainApplication.socket);

    //游戏逻辑
    private boolean isLeavingWords = false;
    private boolean isFromDark = false;
    private UserInfo currentUser = MainApplication.roomInfo.findMeInRoom();


    //狼人聊天室
    private ImageView toggle_chatRoom;
    private View wolf_chatRoom;
    private ScrollView msg_scroll;
    private TextView tv_content;
    private EditText et_msg;
    private View sendMsg;
    private StringBuilder sb= new StringBuilder();
    private RotateAnimation openChatRoomAnimation;
    private RotateAnimation closeChatRoomAnimation;

    private void finishSpeak() {
        bt_endSpeak.setEnabled(false);
        bt_wolf_destroy.setEnabled(false);
        voiceManager.stopRecord();
        if (tickTimer != null)
            tickTimer.cancel();
        speak_time_label.setVisibility(View.INVISIBLE);
        tv_game_hint.setVisibility(View.VISIBLE);
    }

    private void updateRoomInfoIfReJoinGame(Intent intent) {
        if (intent.getBooleanExtra("reJoinGame", false)) {
            MainApplication.socket.emit("reJoinGame", MainApplication.roomInfo.getRoomId(), Me.getUserId());
        }

    }


    private void clearTopActivities() {
        startActivity(new Intent(GameMainActivity.this, GameMainActivity.class));
    }

    @Override
    protected void prepareViews() {
        setContentView(R.layout.game_main);

        openChatRoomAnimation = new RotateAnimation(-90,0, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        openChatRoomAnimation.setDuration(300);
        openChatRoomAnimation.setFillAfter(true);
        closeChatRoomAnimation = new RotateAnimation(0,-90, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        closeChatRoomAnimation.setDuration(300);
        closeChatRoomAnimation.setFillAfter(true);

        //杂项: 计时器相关、游戏背景、声音动画、身份、标记
        time_label = (TextView) findViewById(R.id.time_label);
        tv_game_hint = (TextView) findViewById(R.id.tv_game_hint);
        speak_time_label = findViewById(R.id.speak_time_label);
        speak_time_label.setVisibility(View.INVISIBLE);
        game_bg = findViewById(R.id.game_bg);
        voiceLevel = (ImageView) findViewById(R.id.iv_playStage_voiceLevel);
        speakAnim = (AnimationDrawable) voiceLevel.getDrawable();
        mark = (TextView) findViewById(R.id.tv_playStage_identification);
        identification_label = (TextView) findViewById(R.id.identification_label);
        mark.setOnClickListener(this);

        //画廊
        gallery = (Gallery) findViewById(R.id.gallery_players_head);
        adapter = new GalleryAdapter(this, MainApplication.roomInfo.getUsers());
        gallery.setAdapter(adapter);
        gallery.setSelection(MainApplication.roomInfo.findMyIndexInRoom());

        //大头像
        bigHead = (SimpleDraweeView) findViewById(R.id.iv_playStage_bigHead);
        bigHead.setImageURI(MainApplication.roomInfo.findMeInRoom().getHead());

        //游戏规则
        gameRule = (ImageView) findViewById(R.id.iv_gameStage_gameRule);
        gameRule.setOnClickListener(this);
        View gameRuleBg = getLayoutInflater().inflate(R.layout.game_rule, null);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) gameRuleBg.getBackground();
        gameRuleBg.setBackground(new BitmapDrawable(RoundBitmapUtils.getRoundedCornerBitmap(bitmapDrawable.getBitmap())));
        popupWindow = new PopupWindow(gameRuleBg, ScreenUtils.getScreenWidth(this) * 3 / 4, ScreenUtils.getScreenHeight(this) * 2 / 3);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        //狼人聊天室
        wolf_chatRoom = findViewById(R.id.wolf_chatRoom);
        toggle_chatRoom = (ImageView) findViewById(R.id.toggle_chatRoom);
        tv_content = (TextView)findViewById(R.id.tv_content);
        et_msg = (EditText)findViewById(R.id.et_msg);
        msg_scroll = (ScrollView)findViewById(R.id.msg_scroll);
        sendMsg = findViewById(R.id.sendMsg);
        sendMsg.setOnClickListener(this);
        toggle_chatRoom.setOnClickListener(this);

        //结束发言按钮
        bt_endSpeak = findViewById(R.id.bt_endSpeak);
        bt_endSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishSpeak();
                if (isLeavingWords) {
                    MainApplication.socket.emit("leaveWordsFinished", MainApplication.roomInfo.getRoomId(), isFromDark);
                } else {
                    MainApplication.socket.emit("pass", MainApplication.roomInfo.getRoomId());
                }
            }
        });

        //自爆按钮
        bt_wolf_destroy = findViewById(R.id.bt_wolf_destroy);
        bt_wolf_destroy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(GameMainActivity.this);
                dialog.setTitle("请做出您的选择：");
                dialog.setMessage("您确定要自爆吗?自爆可以终止发言阶段,直接进入天黑");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SoundEffectManager.play(R.raw.wolf_self_destruction);//狼人自爆音效
                        finishSpeak();
                        MainApplication.socket.emit("wolfDestroy", MainApplication.roomInfo.getRoomId(), Me.getUserId());
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


        //侧边栏
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            private boolean hasRefreshed;

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (!hasRefreshed) {
                    hasRefreshed = true;
                    gameDetailAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                drawerView.setClickable(true);  //动态设置Clickbale可以解决穿透点击事件
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                hasRefreshed = false;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        gameDetailListview = (ListView) findViewById(R.id.gameDetail_listView);
        gameDetailAdapter = new GameDetailAdapter(this, MainApplication.roomInfo.getUsers());
        gameDetailListview.setAdapter(gameDetailAdapter);
        updateRoomInfoIfReJoinGame(getIntent());
    }

    protected void prepareSocket() {

        MainApplication.socket
                .on("langrenMsg", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        String userId = (String) args[0];
                        Log.i("userid", userId);
                        String msg = (String) args[1];
                        sb.append(MainApplication.roomInfo.findUserInRoom(userId).getNickname()).append(" 说: ").append(msg).append("\n");
                        tv_content.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_content.setText(sb.toString());
                                msg_scroll.fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
                            }
                        });
                    }
                })
                .on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(GameMainActivity.this).setTitle("!socket已断开")
                                        .setMessage("点击重连.")
                                        .setPositiveButton("重连", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                showProgress("正在重连...");
                                                MainApplication.socket.connect();
                                            }
                                        }).show();
                            }
                        });
                    }
                })
                .on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.i("connected and In Game", "haha");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                hideProgress();
                            }
                        });
                        MainApplication.socket.emit("reJoinGame", MainApplication.roomInfo.getRoomId(), Me.getUserId());
                    }
                })
                .on("reJoinGame", new Emitter.Listener() {
                            @Override
                            public void call(Object... args) {
                                try {
                                    Log.i("reJoinGameInGameMain", "nice");

                                    //房间的整体信息、玩家的存活状态
                                    JSONObject room = (JSONObject) args[0];
                                    MainApplication.roomInfo.setHasPoisoned(room.getBoolean("hasPoisoned"));
                                    MainApplication.roomInfo.setHasSaved(room.getBoolean("hasSaved"));
                                    try {
                                        MainApplication.roomInfo.setPoliceId(room.getString("policeId"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    JSONArray users = room.getJSONArray("users");
                                    for (int i = 0; i < users.length(); i++) {
                                        JSONObject user = users.getJSONObject(i);
                                        String userId = (String) user.get("userId");
                                        MainApplication.roomInfo.findUserInRoom(userId).getGameRole().setDead(user.getBoolean("dead"));
                                    }

                                    //是否有同伴、同伴的Id
                                    JSONArray companys = (JSONArray) args[1];
                                    for (int i = 0; i < companys.length(); i++) {
                                        String userId = (String) companys.get(i);
                                        MainApplication.roomInfo.findUserInRoom(userId).getGameRole().setType(1);
                                    }

                                    //天黑还是天亮
                                    boolean isFromDark = (boolean) args[2];
                                    if (isFromDark) {
                                        game_bg.setBackgroundResource(R.color.dark);
                                    } else {
                                        game_bg.setBackgroundResource(R.mipmap.day);
                                    }

                                    //我的身份
                                    int type = (int) args[3];
                                    UserInfo me = MainApplication.roomInfo.findMeInRoom();
                                    me.getGameRole().setType(type);
                                    identification_label.setText("您的身份是: " + me.getGameRole().getType().getName());
                                    if (me.getGameRole().getType() == GameRole.Type.Wolf)
                                        bt_wolf_destroy.setVisibility(View.VISIBLE);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(GameMainActivity.this, "重连成功", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                )
                .on("start", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        final int type = (int) args[0];
                        final UserInfo me = MainApplication.roomInfo.findMeInRoom();
                        me.getGameRole().setType(type);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                identification_label.setText(me.getGameRole().getType().getName());
                                if (me.getGameRole().getType() == GameRole.Type.Wolf) {
                                    bt_wolf_destroy.setVisibility(View.VISIBLE);
                                    toggle_chatRoom.setVisibility(View.VISIBLE);
                                }
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
                                MainApplication.roomInfo.findUserInRoom(userId).getGameRole().setType(1);
                                companyNames.add(MainApplication.roomInfo.findUserInRoom(userId).getNickname());
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(GameMainActivity.this, "你的同伴是" + companyNames.toString(), Toast.LENGTH_SHORT).show();
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clearTopActivities();
                                tv_game_hint.setText("天黑!  请闭眼....");
                                game_bg.setBackgroundResource(R.color.dark);
                                SoundEffectManager.play(R.raw.dark);//天黑音效
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
                                clearTopActivities();
                                tv_game_hint.setText("天亮了");
                                SoundEffectManager.play(R.raw.light);//天亮音效
                                game_bg.setBackgroundResource(R.mipmap.day);
                            }
                        });
                    }
                })
                .on("votePolice", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        //开始选警长。
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SoundEffectManager.stop();//声音停止
                                tv_game_hint.setText("开始选警长");
                                if (MainApplication.roomInfo.findMeInRoom().getGameRole().isDead())
                                    return;
                                startActivity(new Intent(GameMainActivity.this, VoteActivity.class).putExtra("type", RoomInfo.VOTE_POLICE));
                            }
                        });
                    }
                })
                .on("voteWolf", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        //开始票坏人。
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_game_hint.setText("开始票坏人");
                                if (MainApplication.roomInfo.findMeInRoom().getGameRole().isDead())
                                    return;
                                startActivity(new Intent(GameMainActivity.this, VoteActivity.class).putExtra("type", RoomInfo.VOTE_WOLF));
                            }
                        });
                    }
                })
                .on("come back", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clearTopActivities();
                                tv_game_hint.setText("投票结束");
                            }
                        });
                    }
                })
                .on("action", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        switch (MainApplication.roomInfo.findMeInRoom().getGameRole().getType()) {
                            case Wolf:
                                startActivity(new Intent(getApplicationContext(), WolfActivity.class));
                                break;
                            case Guard:
                                startActivity(new Intent(getApplicationContext(), GuardActivity.class));
                                break;
                            case Wizard:
                                startActivity(new Intent(getApplicationContext(), WizardActivity.class));
                                break;
                            case Predictor:
                                startActivity(new Intent(getApplicationContext(), PredictorActivity.class));
                                break;
                        }
                    }
                })
                .on("darkResult", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        clearTopActivities();
                        String userId1 = (String) args[0];
                        String userId2 = (String) args[1];
                        final StringBuilder sb = new StringBuilder();
                        if (userId1 == null && userId2 == null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SoundEffectManager.stop();
                                    tv_game_hint.setText("今晚是平安夜");
                                }
                            });
                        } else {
                            if (userId1 != null) {
                                UserInfo info = MainApplication.roomInfo.findUserInRoom(userId1);
                                info.getGameRole().setDead(true);
                                sb.append(info.getNickname() + "被杀  ");
                            }

                            if (userId2 != null) {
                                UserInfo info = MainApplication.roomInfo.findUserInRoom(userId2);
                                info.getGameRole().setDead(true);
                                sb.append(info.getNickname() + "被杀  ");
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(GameMainActivity.this, "昨晚" + sb.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                            SoundEffectManager.play(R.raw.kill);//被杀音效
                        }
                    }
                })
                .on("hunter", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_game_hint.setText("等待死亡玩家发动技能,这是专为猎人设计的");
                            }
                        });
                    }
                })
                .on("youHunterDead", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                clearTopActivities();
                                tv_game_hint.setText("猎人，你死了，你可以带走一个人");
                                startActivity(new Intent(GameMainActivity.this, HunterActivity.class));
                            }
                        });
                    }
                })
                .on("hunterFinished", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        final String huntedId = (String) args[0];
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MainApplication.roomInfo.findUserInRoom(huntedId).getGameRole().setDead(true);
                                Toast.makeText(GameMainActivity.this, MainApplication.roomInfo.findUserInRoom(huntedId).getNickname() + "被枪杀", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .on("changePolice", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_game_hint.setText("警长被杀，请等待警长移交警徽");
                            }
                        });
                    }
                })
                .on("deliverPolice", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_game_hint.setText("警长，你被杀了，请移交警徽");
                                startActivity(new Intent(GameMainActivity.this, PoliceActivity.class));
                            }
                        });
                    }
                })
                .on("deliverPoliceFinished", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        final String userId = (String) args[0];
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (userId == null) {
                                    Toast.makeText(GameMainActivity.this, "警徽没了", Toast.LENGTH_SHORT).show();
                                    MainApplication.roomInfo.setPoliceId(null);
                                    return;
                                }
                                final UserInfo police = MainApplication.roomInfo.findUserInRoom(userId);
                                MainApplication.roomInfo.setPoliceId(userId);
                                Toast.makeText(GameMainActivity.this, "警徽传给了" + police.getNickname(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .on("leaveWords", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String userId = (String) args[0];
                                speakAnim.start();
                                tv_game_hint.setText("下面请" + MainApplication.roomInfo.findUserInRoom(userId).getNickname() + "发表遗言");
                            }
                        });
                    }
                })
                .on("youLeaveWords", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isLeavingWords = true;
                                voiceManager.startRecord();
                                isFromDark = (boolean) args[0];
                                tv_game_hint.setText("你死了,请发表遗言");
                                Toast.makeText(GameMainActivity.this, "你死了,请发表遗言", Toast.LENGTH_SHORT).show();
                                tv_game_hint.setVisibility(View.INVISIBLE);
                                speak_time_label.setVisibility(View.VISIBLE);
                                bt_endSpeak.setEnabled(true);
                                bigHead.setImageURI(MainApplication.roomInfo.findMeInRoom().getHead());
                                gallery.setSelection(MainApplication.roomInfo.findMyIndexInRoom());
                                tickTimer = new TickTimer(time_label, Constants.SPEAK_SECONDS, null) {
                                    @Override
                                    protected void onTimeEnd() {
                                        super.onTimeEnd();
                                        MainApplication.socket.emit("leaveWordsFinished", MainApplication.roomInfo.getRoomId(), isFromDark);
                                    }
                                };
                                tickTimer.startTick();
                            }
                        });
                    }
                })
                .on("leaveWordsFinished", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        isLeavingWords = false;
                        speakAnim.stop();
                    }
                })
                .on("startSpeak", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        identification_label.post(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(GameMainActivity.this, GameMainActivity.class));
                                tv_game_hint.setText("现在发言开始");
                            }
                        });

                    }
                })
                .on("speak", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        final String userId = (String) args[0];
                        currentUser = MainApplication.roomInfo.findUserInRoom(userId);
                        identification_label.post(new Runnable() {
                            @Override
                            public void run() {
                                voiceManager.startPlay();
                                speakAnim.start();
                                bigHead.setImageURI(MainApplication.roomInfo.findUserInRoom(userId).getHead());
                                gallery.setSelection(MainApplication.roomInfo.findUserIndexInRoom(userId));
                                tv_game_hint.setText("现在" + MainApplication.roomInfo.findUserInRoom(userId).getNickname() + "开始发言");
                            }
                        });

                    }
                })
                .on("youSpeak", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        currentUser = MainApplication.roomInfo.findMeInRoom();
                        identification_label.post(new Runnable() {
                            @Override
                            public void run() {
                                isLeavingWords = false;
                                speakAnim.stop();
                                voiceManager.startRecord();
                                tv_game_hint.setText("现在轮到你发言");
                                Toast.makeText(GameMainActivity.this, "现在轮到你发言", Toast.LENGTH_SHORT).show();
                                tv_game_hint.setVisibility(View.INVISIBLE);
                                speak_time_label.setVisibility(View.VISIBLE);
                                bt_endSpeak.setEnabled(true);
                                bigHead.setImageURI(MainApplication.roomInfo.findMeInRoom().getHead());
                                gallery.setSelection(MainApplication.roomInfo.findMyIndexInRoom());
                                tickTimer = new TickTimer(time_label, 40, null) {
                                    @Override
                                    protected void onTimeEnd() {
                                        super.onTimeEnd();
                                        finishSpeak();
                                        MainApplication.socket.emit("pass", MainApplication.roomInfo.getRoomId());
                                    }
                                };
                                tickTimer.startTick();
                                if (MainApplication.roomInfo.findMeInRoom().getGameRole().getType() == GameRole.Type.Wolf)
                                    bt_wolf_destroy.setEnabled(true);

                            }
                        });

                    }
                })
                .on("endSpeak", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        identification_label.post(new Runnable() {
                            @Override
                            public void run() {
                                speakAnim.stop();
                                tv_game_hint.setText("发言结束");
                            }
                        });
                    }
                })
                .on("blob", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        byte[] buffer = (byte[]) args[0];
                        voiceManager.speak(voiceManager.decode(buffer));
                    }
                })
                .on("gameOver", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject gameResult = (JSONObject) args[0];
                                    int victory = gameResult.getInt("victory");
                                    Intent intent = new Intent(GameMainActivity.this, GameOverActivity.class);
                                    if (victory == 0) {
                                        intent.putExtra("victory", "狼人胜利");
                                    } else if (victory == 1) {
                                        intent.putExtra("victory", "好人胜利");
                                    }
                                    JSONArray array = gameResult.getJSONArray("returnResults");
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject obj = (JSONObject) array.get(i);
                                        String userId = obj.getString("userId");
                                        int type = obj.getInt("type");
                                        int score = obj.getInt("score");
                                        GameRole gameRole = MainApplication.roomInfo.findUserInRoom(userId).getGameRole();
                                        gameRole.setType(type);
                                        gameRole.setScore(score);

                                    }
                                    startActivity(intent);
                                    finish();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }
                })
                .on("wolfDestroy", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        final String wolfId = (String) args[0];
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                UserInfo wolf = MainApplication.roomInfo.findUserInRoom(wolfId);
                                wolf.getGameRole().setType(1);
                                wolf.getGameRole().setDead(true);
                                tv_game_hint.setText("狼人 " + wolf.getNickname() + " 自爆了");
                                Toast.makeText(GameMainActivity.this, "狼人 " + wolf.getNickname() + " 自爆了", Toast.LENGTH_SHORT).show();
                                finishSpeak();
                            }
                        });
                    }
                });

    }


    @Override
    protected void unbindSocket() {

        voiceManager.stopPlay();
        voiceManager.stopRecord();
        MainApplication.socket.off("start").off("company").off("dark").off("darkResult").off("roomInfo").off("light").off("action").off(Socket.EVENT_DISCONNECT)
                .off("votePolice").off("voteWolf").off("come back").off("wolfDestroy").off("gameOver")
                .off("endSpeak").off("youSpeak").off("speak").off("startSpeak")
                .off("youLeaveWords").off("leaveWords")
                .off("deliverPolice").off("changePolice").off("deliverPoliceFinished")
                .off("hunterDead").off("youHunterDead").off("hunterFinished").off("blob");
    }



    //弹出游戏规则；标记身份
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_gameStage_gameRule:
                SoundEffectManager.play(R.raw.user_detail);
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
            case R.id.toggle_chatRoom:
                boolean isChatRoomVisible = wolf_chatRoom.getVisibility()==View.VISIBLE;
                if(isChatRoomVisible) {
                    toggle_chatRoom.startAnimation(closeChatRoomAnimation);
                    wolf_chatRoom.setVisibility(View.INVISIBLE);
                }
                else {
                    toggle_chatRoom.startAnimation(openChatRoomAnimation);
                    wolf_chatRoom.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.sendMsg:
                String msg = et_msg.getText().toString();
                if(TextUtils.isEmpty(msg))
                    return;
                et_msg.setText("");
                MainApplication.socket.emit("langrenMsg",MainApplication.roomInfo.getRoomId(),Me.getUserId(),msg);
                break;
            case R.id.tv_playStage_identification:
                SoundEffectManager.play(R.raw.user_detail);
                Identification();
                break;
        }
    }


    @Override
    public void onBackPressed() {

    }

    //标记玩家身份
    public void Identification() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(GameMainActivity.this);
        builder.setTitle("请标记玩家身份");
        builder.setSingleChoiceItems(getResources().getStringArray(R.array.sign_type), 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                currentUser.getGameRole().setSign_type(which);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//消失对话框
            }
        });
        builder.show();
    }


}

