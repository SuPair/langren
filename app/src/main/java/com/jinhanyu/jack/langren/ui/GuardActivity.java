package com.jinhanyu.jack.langren.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.langren.ActionPerformer;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.Me;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.TickTimer;
import com.jinhanyu.jack.langren.adapter.GameRoleCommonAdapter;
import com.jinhanyu.jack.langren.entity.GameRole;

public class GuardActivity extends AppCompatActivity implements ActionPerformer{
    private GameRoleCommonAdapter adapter;
    private ListView listView;
    private TickTimer tickTimer;
    private TextView time_label,action_done_label;
    private String guardUserId;
    private SimpleDraweeView iv_last_guard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guard);
        listView = (ListView) findViewById(R.id.guard_listView);
        time_label = (TextView) findViewById(R.id.time_label);
        action_done_label = (TextView) findViewById(R.id.action_done_label);
        iv_last_guard = (SimpleDraweeView) findViewById(R.id.iv_last_guard);
        String lastGuadedUserId = MainApplication.roomInfo.getLastGuardedUserId();
        if(lastGuadedUserId!=null)
            iv_last_guard.setImageURI(MainApplication.roomInfo.findUserInRoom(lastGuadedUserId).getHead());
        adapter = new GameRoleCommonAdapter(this, MainApplication.roomInfo.getUsers(), GameRole.Type.Guard);
        listView.setAdapter(adapter);

        tickTimer = new TickTimer(time_label,15,adapter){
            @Override
            protected void onTimeEnd() {
                super.onTimeEnd();
                MainApplication.socket.emit("guard",MainApplication.roomInfo.getRoomId(),guardUserId);
            }
        };
        tickTimer.startTick();
    }

    @Override
    public void doAction(Object... params) {
        guardUserId = (String) params[0];
        action_done_label.setText("守卫完成");
    }
}
