package com.jinhanyu.jack.langren.ui;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.langren.ActionPerformer;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.TickTimer;
import com.jinhanyu.jack.langren.adapter.PredictorAdapter;

import io.socket.emitter.Emitter;

public class PredictorActivity extends CommonActivity implements ActionPerformer{

    private PredictorAdapter adapter;
    private ListView listView;
    private TextView time_label;
    private TickTimer tickTimer;
    @Override
    protected void prepareViews() {
        setContentView(R.layout.predictor);
        listView = (ListView) findViewById(R.id.predictor_listView);
        adapter = new PredictorAdapter(this,MainApplication.roomInfo.getUsers());
        time_label = (TextView) findViewById(R.id.time_label);
        listView.setAdapter(adapter);
        tickTimer = new TickTimer(time_label,15,adapter){
            @Override
            protected void onTimeEnd() {
                super.onTimeEnd();
                MainApplication.socket.emit("predictor",MainApplication.roomInfo.getRoomId(),MainApplication.userInfo.getUserId(),null);
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
                            Toast.makeText(PredictorActivity.this, "您要验的人的身份是好人", Toast.LENGTH_SHORT).show();

                        }else if(type==1){
                            Toast.makeText(PredictorActivity.this, "您要验的人的身份是坏人", Toast.LENGTH_SHORT).show();
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
        tickTimer.cancel();
        time_label.setText("验证完成");
    }
}
