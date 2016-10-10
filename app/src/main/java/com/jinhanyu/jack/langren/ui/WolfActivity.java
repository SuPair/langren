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

public class WolfActivity extends CommonActivity implements ActionPerformer{

    private TextView time_label,action_done_label;
    private GameRoleCommonAdapter wolfAdapter;
    private ListView listView;


    private TickTimer tickTimer;

    private String toKillUserId;



    @Override
    protected void prepareViews() {
        setContentView(R.layout.wolf);

        time_label = (TextView) findViewById(R.id.time_label);

        action_done_label = (TextView) findViewById(R.id.action_done_label);
        wolfAdapter = new GameRoleCommonAdapter(this,MainApplication.roomInfo.getUsers(), GameRole.Type.Wolf);
        listView = (ListView) findViewById(R.id.wolf_listView);
        listView.setAdapter(wolfAdapter);


        Toast.makeText(WolfActivity.this, "今晚你想刀谁？开始吧", Toast.LENGTH_SHORT).show();
        tickTimer = new TickTimer(time_label, Constants.WOLF_SECONDS,wolfAdapter){
            @Override
            protected void onTimeEnd() {
                super.onTimeEnd();
                MainApplication.socket.emit("wolf",MainApplication.roomInfo.getRoomId(), Me.getUserId(),toKillUserId);
            }
        };
        tickTimer.startTick();
    }

    @Override
    protected void prepareSocket() {
        MainApplication.socket.on("wolfResult", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    String finalUserId = (String) args[0];
                    JSONArray array = (JSONArray) args[1];
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = (JSONObject) array.get(i);
                        String fromUserId = (String) obj.get("fromUserId");
                        String toUserId = (String) obj.get("toUserId");
                        MainApplication.roomInfo.getVoteResults().add(new VoteResult(MainApplication.roomInfo.findUserInRoom(fromUserId),MainApplication.roomInfo.findUserInRoom(toUserId)));
                    }
                    Intent intent =new Intent(WolfActivity.this, VoteResultActivity.class).putExtra("type",RoomInfo.VOTE_KILL);
                    if(finalUserId!=null)
                        intent.putExtra("finalUserName",MainApplication.roomInfo.findUserInRoom(finalUserId).getNickname());
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
        MainApplication.socket.off("wolfResult");
    }



    @Override
    public void doAction(Object... params) {
        toKillUserId = (String) params[0];
        action_done_label.setText("击杀完成，等待其他人...");
    }
}
