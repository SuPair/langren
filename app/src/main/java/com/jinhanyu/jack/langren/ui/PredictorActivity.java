package com.jinhanyu.jack.langren.ui;

import android.widget.ListView;
import android.widget.TextView;

import com.jinhanyu.jack.langren.ActionPerformer;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.Me;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.TickTimer;
import com.jinhanyu.jack.langren.adapter.GameRoleCommonAdapter;
import com.jinhanyu.jack.langren.entity.GameRole;

import io.socket.emitter.Emitter;

public class PredictorActivity extends CommonActivity implements ActionPerformer{

    private GameRoleCommonAdapter adapter;
    private ListView listView;
    private TextView time_label,action_done_label;
    private TickTimer tickTimer;
    private boolean hasChecked;
    private String toCheckUserId;

    @Override
    protected void prepareViews() {
        setContentView(R.layout.predictor);
        listView = (ListView) findViewById(R.id.predictor_listView);
        adapter = new GameRoleCommonAdapter(this,MainApplication.roomInfo.getUsers(), GameRole.Type.Predictor);
        action_done_label = (TextView) findViewById(R.id.action_done_label);
        time_label = (TextView) findViewById(R.id.time_label);
        listView.setAdapter(adapter);
        tickTimer = new TickTimer(time_label,15,adapter){
            @Override
            protected void onTimeEnd() {
                super.onTimeEnd();
                if(hasChecked) return;
                MainApplication.socket.emit("predictor",MainApplication.roomInfo.getRoomId(), Me.getUserId(),null);
            }
        };
        tickTimer.startTick();


    }

    @Override
    protected void prepareSocket() {

        MainApplication.socket.on("predictorMsg", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                final int type = (int) args[0];

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(type==0){
                            action_done_label.setText(MainApplication.roomInfo.findUserInRoom(toCheckUserId).getNickname()+"是好人");
                        }else if(type==1){
                            action_done_label.setText(MainApplication.roomInfo.findUserInRoom(toCheckUserId).getNickname()+"是狼人");
                            MainApplication.roomInfo.findUserInRoom(toCheckUserId).getGameRole().setType(1);
                        }

                    }
                });

            }
        });

    }

    @Override
    protected void unbindSocket() {
        MainApplication.socket.off("predictorMsg");
    }

    @Override
    public void doAction(Object... params) {
        toCheckUserId = (String) params[0];
        MainApplication.socket.emit("predictor",MainApplication.roomInfo.getRoomId(),toCheckUserId);
        hasChecked =true;
        action_done_label.setText("等待法官确认...");
    }
}
