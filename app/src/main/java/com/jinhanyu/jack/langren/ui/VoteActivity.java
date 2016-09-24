package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.langren.ActionPerformer;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.TickTimer;
import com.jinhanyu.jack.langren.adapter.VoteAdapter;
import com.jinhanyu.jack.langren.entity.RoomInfo;
import com.jinhanyu.jack.langren.entity.VoteResult;

import org.json.JSONArray;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class VoteActivity extends CommonActivity implements ActionPerformer{

    private int type = -1;

    private ListView listView;
    private VoteAdapter adapter;

    private String toVoteUserId;

    private TickTimer tickTimer;

    private TextView time_label;

    @Override
    protected void prepareViews() {
        type = getIntent().getIntExtra("type",-1);
        setContentView(R.layout.vote);
        listView = (ListView) findViewById(R.id.vote_listView);
        adapter = new VoteAdapter(this, MainApplication.roomInfo.getUsers());
        listView.setAdapter(adapter);
        time_label = (TextView) findViewById(R.id.time_label);

        tickTimer = new TickTimer(time_label,15,adapter){
            @Override
            protected void onTimeEnd() {
                super.onTimeEnd();
                if(type== RoomInfo.VOTE_POLICE)
                     MainApplication.socket.emit("votePolice",MainApplication.roomInfo.getRoomId(),MainApplication.userInfo.getUserId(),toVoteUserId);
                else if(type== RoomInfo.VOTE_WOLF)
                    MainApplication.socket.emit("voteWolf",MainApplication.roomInfo.getRoomId(),MainApplication.userInfo.getUserId(),toVoteUserId);
                else
                    throw new RuntimeException("in VoteActivity unknown type.");
            }
        };
        tickTimer.startTick();
    }

    @Override
    protected void prepareSocket() {
        MainApplication.socket
                .on("policeResult", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        try {
                            String policeUserId = (String) args[0];
                            JSONArray array = (JSONArray) args[1];
                            MainApplication.roomInfo.getVoteResults().clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = (JSONObject) array.get(i);
                                String fromUserId = (String) obj.get("fromUserId");
                                String toUserId = (String) obj.get("toUserId");
                                MainApplication.roomInfo.getVoteResults().add(new VoteResult(MainApplication.roomInfo.findUserInRoom(fromUserId), MainApplication.roomInfo.findUserInRoom(toUserId)));
                            }
                            Intent intent =new Intent(VoteActivity.this, VoteResultActivity.class).putExtra("type",type);
                            if(policeUserId!=null) {
                                MainApplication.roomInfo.setPolice(policeUserId);
                                intent.putExtra("finalUserName", MainApplication.roomInfo.findUserInRoom(policeUserId).getUsername());
                                startActivity(intent);
                                finish();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                })
                .on("continueVotePolice", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        adapter.reset();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(VoteActivity.this, "没有任何人投票,必须投出警长", Toast.LENGTH_SHORT).show();
                                tickTimer = new TickTimer(time_label,10,adapter){
                                    @Override
                                    protected void onTimeEnd() {
                                        super.onTimeEnd();
                                        MainApplication.socket.emit("votePolice",MainApplication.roomInfo.getRoomId(),MainApplication.userInfo.getUserId(),toVoteUserId);
                                    }
                                };
                                tickTimer.startTick();
                            }
                        });
                    }
                })
                .on("lightResult", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        try {
                            String voteOutUserId = (String) args[0];
                            JSONArray array = (JSONArray) args[1];
                            MainApplication.roomInfo.getVoteResults().clear();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = (JSONObject) array.get(i);
                                String fromUserId = (String) obj.get("fromUserId");
                                String toUserId = (String) obj.get("toUserId");
                                MainApplication.roomInfo.getVoteResults().add(new VoteResult(MainApplication.roomInfo.findUserInRoom(fromUserId), MainApplication.roomInfo.findUserInRoom(toUserId)));
                            }
                            Intent intent =new Intent(VoteActivity.this, VoteResultActivity.class).putExtra("type",type);
                            if(voteOutUserId!=null) {
                                MainApplication.roomInfo.findUserInRoom(voteOutUserId).getGameRole().setDead(true);
                                intent.putExtra("finalUserName", MainApplication.roomInfo.findUserInRoom(voteOutUserId).getUsername());
                            }
                            startActivity(intent);
                            finish();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    protected void unbindSocket() {
            MainApplication.socket.off("policeResult").off("lightResult").off("continueVotePolice");
    }

    @Override
    public void doAction(Object... params) {
        toVoteUserId = (String) params[0];
        time_label.setText("投票完成，等待其他人...");
    }
}
