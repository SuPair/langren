package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.langren.ActionPerformer;
import com.jinhanyu.jack.langren.Constants;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.Me;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.TickTimer;
import com.jinhanyu.jack.langren.adapter.GameRoleCommonAdapter;
import com.jinhanyu.jack.langren.entity.GameRole;
import com.jinhanyu.jack.langren.entity.RoomInfo;
import com.jinhanyu.jack.langren.entity.VoteResult;

import org.json.JSONArray;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class VoteActivity extends CommonActivity implements ActionPerformer{

    private int type = -1;

    private ListView listView;
    private GameRoleCommonAdapter adapter;

    private String toVoteUserId;

    private TickTimer tickTimer;

    private TextView time_label,tv_vote_title;

    @Override
    protected void prepareViews() {
        type = getIntent().getIntExtra("type",-1);
        setContentView(R.layout.vote);
        listView = (ListView) findViewById(R.id.vote_listView);
        adapter = new GameRoleCommonAdapter(this, MainApplication.roomInfo.getUsers(), GameRole.Type.Citizen);
        listView.setAdapter(adapter);
        time_label = (TextView) findViewById(R.id.time_label);
        tv_vote_title = (TextView) findViewById(R.id.tv_vote_title);
        if(type== RoomInfo.VOTE_POLICE)
            tv_vote_title.setText("投票选警长");
        else if(type== RoomInfo.VOTE_WOLF)
            tv_vote_title.setText("开始票坏人");
        else
            throw new RuntimeException("in VoteActivity unknown type.");

        tickTimer = new TickTimer(time_label, Constants.VOTE_SECONDS,adapter){
            @Override
            protected void onTimeEnd() {
                super.onTimeEnd();
                if(type== RoomInfo.VOTE_POLICE)
                     MainApplication.socket.emit("votePolice",MainApplication.roomInfo.getRoomId(), Me.getUserId(),toVoteUserId);
                else if(type== RoomInfo.VOTE_WOLF)
                    MainApplication.socket.emit("voteWolf",MainApplication.roomInfo.getRoomId(),Me.getUserId(),toVoteUserId);
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
                                MainApplication.roomInfo.setPoliceId(policeUserId);
                                intent.putExtra("finalUserName", MainApplication.roomInfo.findUserInRoom(policeUserId).getNickname());
                                startActivity(intent);
                                finish();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
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
                                intent.putExtra("finalUserName", MainApplication.roomInfo.findUserInRoom(voteOutUserId).getNickname());
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
            MainApplication.socket.off("policeResult").off("lightResult");
    }

    @Override
    public void doAction(Object... params) {
        toVoteUserId = (String) params[0];
        tv_vote_title.setText("投票完成，等待其他人...");
    }
}
