package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.parse.ParseUser;

/**
 * Created by anzhuo on 2016/9/12.
 */
public abstract class CommonActivity extends AppCompatActivity implements ActionSheet.ActionSheetListener, SensorEventListener {

    private ActionSheet.Builder builder;
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        prepareViews();
        prepareSocket();


        builder = ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("Cancel")
                .setOtherButtonTitles("排行榜", "切换账号", "退出游戏")
                .setCancelableOnTouchOutside(true)
                .setListener(this);

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
                MainApplication.userInfo = new UserInfo();
                startActivity(new Intent(this, LoginActivty.class));
                finish();
                break;
            case 2:
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
