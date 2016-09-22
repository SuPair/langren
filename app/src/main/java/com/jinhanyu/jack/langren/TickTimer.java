package com.jinhanyu.jack.langren;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by anzhuo on 2016/9/20.
 */
public class TickTimer extends Timer {

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(totalTime==0){     //时间到还未投票就直接谁都不投，发消息给服务器
                cancel();
                timeLabel.setText("时间到");
                onTimeEnd();
                if(actionPerformer!=null)
                   actionPerformer.doAction();
            }else{
                timeLabel.setText(totalTime--+"秒");
            }
        }
    };

    protected void onTimeEnd(){

    }



    private ActionPerformer actionPerformer;
    private int totalTime;
    private TextView timeLabel;

    public TickTimer(TextView timeLabel,int totalTime,ActionPerformer actionPerformer) {
        this.actionPerformer = actionPerformer;
        this.totalTime = totalTime;
        this.timeLabel = timeLabel;
    }



    public void startTick(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
        schedule(task,0,1000);
    }
}
