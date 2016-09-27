package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.SoundEffectManager;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class UserNameActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText game_number;
    private EditText game_password;
    private EditText game_name;
    private View next;
    private ImageView showPassword;
    private boolean click;//判断是否显示密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_name);


        game_number = (EditText) findViewById(R.id.game_number);
        game_password = (EditText) findViewById(R.id.game_password);
        game_name = (EditText) findViewById(R.id.game_name);
        next =  findViewById(R.id.next);
        showPassword = (ImageView) findViewById(R.id.showPassword);

        next.setOnClickListener(this);
        showPassword.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next://注册页面的 账号及密码 保存到数据库
                final String username = game_number.getText().toString();
                final String password = game_password.getText().toString();
                final String nickname = game_name.getText().toString();
                final ParseUser user = new ParseUser();
                user.setUsername(username);
                user.setPassword(password);
                user.put("nickname",nickname);
                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e!=null){
                            e.printStackTrace();
                            Toast.makeText(UserNameActivity.this, "注册失败"+" code:"+e.getCode(), Toast.LENGTH_SHORT).show();
                        }else{
                           MainApplication.userInfo.populateFromParseServer(user);
                           Intent intent = new Intent(UserNameActivity.this, UserHeadActivity.class);
                           startActivity(intent);
                            SoundEffectManager.play(R.raw.complete);
                           finish();
                        }
                    }
                });

                break;
            case R.id.showPassword://点击显示密码
                if (click) {
                    game_password.setTransformationMethod(null);//显示密码
                    click = false;
                } else {
                    game_password.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
                    click = true;
                }
                break;
        }
    }
}
