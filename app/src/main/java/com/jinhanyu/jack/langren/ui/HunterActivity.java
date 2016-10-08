package com.jinhanyu.jack.langren.ui;

import android.os.Bundle;


import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.jinhanyu.jack.langren.ActionPerformer;
import com.jinhanyu.jack.langren.Constants;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.TickTimer;
import com.jinhanyu.jack.langren.adapter.GameRoleCommonAdapter;
import com.jinhanyu.jack.langren.entity.GameRole;

public class HunterActivity extends AppCompatActivity implements ActionPerformer {
    private ListView listView;
    private GameRoleCommonAdapter adapter;
    private String huntedUserId;
    private TextView time_label,action_done_label;
    private TickTimer tickTimer;

    private boolean isFromDark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hunter);
        action_done_label = (TextView) findViewById(R.id.action_done_label);
        isFromDark = getIntent().getBooleanExtra("isFromDark", false);
        listView = (ListView) findViewById(R.id.hunter_listView);
        adapter = new GameRoleCommonAdapter(this, MainApplication.roomInfo.getAliveUsers(), GameRole.Type.Hunter);
        listView.setAdapter(adapter);
        time_label = (TextView) findViewById(R.id.time_label);
        tickTimer = new TickTimer(time_label, Constants.HUNTER_SECONDS, adapter) {
            @Override
            protected void onTimeEnd() {
                super.onTimeEnd();
                MainApplication.socket.emit("hunter", MainApplication.roomInfo.getRoomId(), huntedUserId, isFromDark);
                finish();
            }
        };
        tickTimer.startTick();

    }

    @Override
    public void doAction(Object... params) {
        huntedUserId = (String) params[0];
        action_done_label.setText("枪毙成功");
    }
}
