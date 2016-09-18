package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.adapter.WolfAdapter;
import com.jinhanyu.jack.langren.entity.VoteResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import io.socket.emitter.Emitter;

public class WolfActivity extends CommonActivity {
    private TextView tv_content;
    private EditText et_msg;
    private TextView time_label;
    private WolfAdapter wolfAdapter;
    private ListView listView;
    private StringBuilder sb= new StringBuilder();
    Timer timer = new Timer();

    int time = 15; //计时器的秒数
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(time==0){     //时间到还未投票就直接谁都不投，发消息给服务器
                timer.cancel();
                MainApplication.socket.emit("wolf",MainApplication.roomInfo.getRoomId(),MainApplication.userInfo.getUserId(),null);
            }else{
                time_label.setText(time--+"");
            }
        }
    };

    TimerTask task = new TimerTask() {

        @Override
        public void run() {
            // 需要做的事:发送消息
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };


    @Override
    protected void prepareViews() {
        setContentView(R.layout.wolf);
        tv_content = (TextView) findViewById(R.id.tv_content);
        et_msg = (EditText) findViewById(R.id.et_msg);
        time_label = (TextView) findViewById(R.id.time_label);
        wolfAdapter = new WolfAdapter(this,null);
        listView = (ListView) findViewById(R.id.wolf_listView);
        listView.setAdapter(wolfAdapter);


        Toast.makeText(WolfActivity.this, "开始进行投票", Toast.LENGTH_SHORT).show();
        timer.schedule(task,0,1000);
    }

    @Override
    protected void prepareSocket() {
        MainApplication.socket.on("langrenMsg", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                 String userId= (String) args[0];
                 String msg = (String) args[1];
                 sb.append(MainApplication.findUserInRoom(userId).getName()+" 说"+msg);
            }
        }).on("wolfResult", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    String finalUserId = (String) args[0];
                    JSONArray array = (JSONArray) args[1];
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = (JSONObject) array.get(i);
                        String fromUserId = (String) obj.get("fromUserId");
                        String toUserId = (String) obj.get("toUserId");
                        MainApplication.voteResults.add(new VoteResult(MainApplication.findUserInRoom(fromUserId).getName(),MainApplication.findUserInRoom(toUserId).getName()));
                    }
                    startActivity(new Intent(WolfActivity.this, VoteActivity.class).putExtra("finalUserName",MainApplication.findUserInRoom(finalUserId).getName()));
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void unbindSocket() {
        MainApplication.socket.off("langrenMsg");
    }

    public void sendMsg(View view) {
        String msg = et_msg.getText().toString();
        if(TextUtils.isEmpty(msg))
            return;
        MainApplication.socket.emit("langrenMsg",MainApplication.roomInfo.getRoomId(),MainApplication.userInfo.getUserId(),msg);
    }
}
