package com.jinhanyu.jack.langren.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.SoundEffectManager;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public   class LoginActivty extends CommonActivity implements View.OnClickListener {
    private EditText game_number;
    private EditText game_password;
    private TextView forget_password;
    private ImageButton game_login;
    private TextView game_register;

    View modify_ip,cannot_connect;
    private AlertDialog modify_ip_dialog;


    private void restartApplication() {
        System.exit(0);
    }

    @Override
    protected void prepareViews() {
        setContentView(R.layout.login);
        watchNetworkState();

        modify_ip = getLayoutInflater().inflate(R.layout.modify_ip, null);
        final EditText et_new_ip_address = (EditText) modify_ip.findViewById(R.id.et_new_ip_address);
        et_new_ip_address.setText(MainApplication.ServerHost);
        modify_ip_dialog = new AlertDialog.Builder(LoginActivty.this).setTitle("修改ip,确定后重新点开应用").setView(modify_ip)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String new_ip_address = et_new_ip_address.getText().toString().trim();
                        if (!new_ip_address.matches("([0-9]{1,3}\\.){3}[0-9]{1,3}")) {
                            Toast.makeText(LoginActivty.this, "格式不对", Toast.LENGTH_SHORT).show();
                        } else {
                            getSharedPreferences("ip", MODE_APPEND).edit().putString("ip", new_ip_address).commit();
                            restartApplication();
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
        cannot_connect = findViewById(R.id.cannot_connect);
        cannot_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modify_ip_dialog.show();
            }
        });

        game_number = (EditText) findViewById(R.id.game_account);
        game_password = (EditText) findViewById(R.id.game_password);
        forget_password = (TextView) findViewById(R.id.forget_password);
        game_login = (ImageButton) findViewById(R.id.game_login);
        game_register = (TextView) findViewById(R.id.game_register);

        forget_password.setOnClickListener(this);
        game_login.setOnClickListener(this);
        game_register.setOnClickListener(this);
    }

    @Override
    protected void prepareSocket() {

    }

    @Override
    protected void unbindSocket() {

    }


    private boolean hasLogined;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forget_password:
                final Toast toast = Toast.makeText(LoginActivty.this, "", Toast.LENGTH_SHORT);
                SoundEffectManager.play(R.raw.forget_password);//音效
                toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 100);
                toast.setText("请联系管理员！");
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.game_login:

                final String username = game_number.getText().toString();
                final String password = game_password.getText().toString();
                showProgress("正在登陆...");
                ParseUser.logInInBackground(username, password, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        hideProgress();
                        hasLogined = true;
                        if (e != null) {
                            SoundEffectManager.play(R.raw.login_failure);//音效
                            e.printStackTrace();
                            Toast toast1 = Toast.makeText(LoginActivty.this, "登录失败", Toast.LENGTH_SHORT);
                            toast1.setGravity(Gravity.CENTER_HORIZONTAL, 0, 100);
                            toast1.setDuration(Toast.LENGTH_SHORT);
                            toast1.show();

                        } else {
                            SoundEffectManager.play(R.raw.enter_hall);//音效
                            Intent intent1 = new Intent(LoginActivty.this, SelectRoomActivity.class);
                            startActivity(intent1);
                            finish();
                        }
                    }
                });
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!hasLogined) {
                            Toast.makeText(LoginActivty.this, "没连接上,ip可能有问题", Toast.LENGTH_SHORT).show();
                            hideProgress();
                        }
                    }
                },5000);

                break;
            case R.id.game_register:
                Intent intent = new Intent(this, UserNameActivity.class);
                startActivity(intent);
                break;
        }
    }


}
