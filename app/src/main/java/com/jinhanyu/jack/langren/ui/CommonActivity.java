package com.jinhanyu.jack.langren.ui;

import android.app.ProgressDialog;
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
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baoyz.actionsheet.ActionSheet;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.NetWorkStateReceiver;
import com.jinhanyu.jack.langren.R;
import com.parse.ParseUser;

/**
 * Created by anzhuo on 2016/9/12.
 */
public abstract class CommonActivity extends AppCompatActivity implements ActionSheet.ActionSheetListener, SensorEventListener {

    private ActionSheet.Builder builder;
    private SensorManager sensorManager;
    private NetWorkStateReceiver receiver;
    private TextView network_state;
    private ProgressDialog progressDialog;


    protected void showProgress(String msg){
        progressDialog.setMessage(msg);
        progressDialog.show();
    }

    protected void hideProgress(){
        progressDialog.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        progressDialog = new ProgressDialog(this);
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
        registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
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

    protected abstract void prepareSocket();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null)
            unregisterReceiver(receiver);
        unbindSocket();
    }

    protected abstract void unbindSocket();


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
                startActivity(new Intent(this, LoginActivty.class));
                finish();
                break;
            case 2:
                android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
                System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
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
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
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
