package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baoyz.actionsheet.ActionSheet;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.NetWorkStateReceiver;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.entity.RoomInfo;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.parse.ParseUser;

import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by anzhuo on 2016/9/12.
 */
public abstract class CommonActivity extends AppCompatActivity implements ActionSheet.ActionSheetListener, SensorEventListener {

    private ActionSheet.Builder builder;
    private SensorManager sensorManager;
    private NetWorkStateReceiver receiver;
    private TextView network_state,socket_state;
    private boolean watchNetworkState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        prepareViews();
        prepareSocket();


        builder = ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("Cancel")
                .setOtherButtonTitles("排行榜", "切换账号", "退出游戏")
                .setCancelableOnTouchOutside(true)
                .setListener(this);

    }

    protected void watchNetworkState() {
        network_state = (TextView) findViewById(R.id.network_state);
        receiver = new NetWorkStateReceiver(network_state);
        registerReceiver(receiver,new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        watchNetworkState =true;
        socket_state = (TextView) findViewById(R.id.socket_state);
        socket_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socket_state.setEnabled(false);
                MainApplication.socket.connect();
                MainApplication.socket.emit("login", MainApplication.userInfo.getUserId());
            }
        });
        MainApplication.socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        socket_state.setVisibility(View.VISIBLE);
                        socket_state.setText("连接已断开,点击重连");
                        socket_state.setEnabled(true);
                    }
                });
            }
        }).on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        socket_state.setVisibility(View.VISIBLE);
                        socket_state.setText("正在重连...");
                    }
                });
            }
        }).on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        socket_state.setVisibility(View.VISIBLE);
                        socket_state.setText("重连成功");
                        socket_state.setVisibility(View.GONE);
                    }
                });
            }
        }).on(Socket.EVENT_RECONNECT_FAILED, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        socket_state.setVisibility(View.VISIBLE);
                        socket_state.setText("重连失败,点击重连");
                    }
                });
            }
        }).on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        socket_state.setVisibility(View.VISIBLE);
                        socket_state.setText("连接成功");
                        socket_state.setVisibility(View.GONE);
                    }
                });
            }
        }).on("reJoinGame", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject room = (JSONObject) args[0];
                MainApplication.roomInfo = JSON.parseObject(room.toString(), RoomInfo.class);
                startActivity(new Intent(CommonActivity.this,GameMainActivity.class));
            }
        });

    }

    private Handler handler = new Handler();

    protected void refreshUI(final BaseAdapter adapter) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }


    protected abstract void prepareViews();

    protected  abstract void prepareSocket();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver!=null)
           unregisterReceiver(receiver);
        unbindSocket();
        if(watchNetworkState)
        MainApplication.socket.off(Socket.EVENT_DISCONNECT).off(Socket.EVENT_RECONNECTING).off(Socket.EVENT_RECONNECT).off(Socket.EVENT_RECONNECT_FAILED).off(Socket.EVENT_CONNECT);
    }

    protected  abstract void unbindSocket();



    private boolean isShowing = false;

    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
        isShowing = false;
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index) {
            case 0:
                startActivity(new Intent(this, GameTopActivity.class));
                break;
            case 1:
                ParseUser.getCurrentUser().logOut();
                MainApplication.socket.disconnect();
                MainApplication.userInfo = new UserInfo();
                startActivity(new Intent(this, LoginActivty.class));
                finish();
                break;
            case 2:
                //退出游戏
                MainApplication.socket.disconnect();
                System.exit(0);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (!isShowing) {
                builder.show();
                isShowing = true;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //当传感器精度改变时回调该方法，Do nothing.
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        int sensorType = event.sensor.getType();
        //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if ((Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17 || Math.abs(values[2]) > 17)) {
                if (!isShowing) {
                    builder.show();
                    isShowing = true;
                }
            }
        }
    }

}
