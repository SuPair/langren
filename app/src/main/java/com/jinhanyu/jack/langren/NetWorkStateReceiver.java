package com.jinhanyu.jack.langren;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.langren.util.NetworkStateUtils;

/**
 * Created by anzhuo on 2016/9/19.
 */
public class NetWorkStateReceiver extends BroadcastReceiver {
    private TextView textView;

    public NetWorkStateReceiver(TextView textView){
        this.textView = textView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = NetworkStateUtils.isConnected(context);
        String msg = isConnected ? "网络已连接上" : "没网了";
        textView.setText(msg);
        if(isConnected)
            textView.setVisibility(View.GONE);
        else
            textView.setVisibility(View.VISIBLE);
    }
}
