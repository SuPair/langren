package com.jinhanyu.jack.langren;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.jinhanyu.jack.langren.util.NetworkStateUtils;

/**
 * Created by anzhuo on 2016/9/19.
 */
public class NetWorkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = NetworkStateUtils.isConnected(context);
        String msg = isConnected ? ":) 网络已连接上√" : "/(ㄒoㄒ)/~~网络已断开连接×";
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
