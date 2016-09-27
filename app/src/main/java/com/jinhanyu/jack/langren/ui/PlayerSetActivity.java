package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.SoundEffectManager;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by anzhuo on 2016/9/24.
 */
public class PlayerSetActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView player_set_back;//返回
    private TextView game_number;//游戏账号
    private EditText game_nickname;//游戏昵称
    private com.facebook.drawee.view.SimpleDraweeView game_head;//游戏头像
    private TextView game_head_choose;//修改头像
    private TextView player_set_complete;//设置完成


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_set);

        player_set_back = (ImageView) findViewById(R.id.player_set_back);
        game_number = (TextView) findViewById(R.id.game_number);
        game_nickname = (EditText) findViewById(R.id.game_nickname);
        game_head = (SimpleDraweeView) findViewById(R.id.game_head);
        game_head_choose = (TextView) findViewById(R.id.game_head_choose);
        player_set_complete = (TextView) findViewById(R.id.player_set_complete);

        player_set_back.setOnClickListener(this);
        game_head_choose.setOnClickListener(this);
        player_set_complete.setOnClickListener(this);

        game_number.setText(MainApplication.userInfo.getUsername());
        game_nickname.setText(MainApplication.userInfo.getNickname());
        game_head.setImageURI(MainApplication.userInfo.getHead());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.player_set_back://返回
                SoundEffectManager.play(R.raw.back);
                finish();
                break;
            case R.id.game_head_choose://修改头像
                startActivity(new Intent(PlayerSetActivity.this, UserHeadActivity.class));
                break;
            case R.id.player_set_complete://完成
                final String nickname = game_nickname.getText().toString();//昵称
                ParseUser.getCurrentUser().put("nickname", nickname);
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e!=null){
                            e.printStackTrace();
                            Toast.makeText(PlayerSetActivity.this, "修改失败"+" code:"+e.getCode(), Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(PlayerSetActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                            MainApplication.userInfo.setNickname(nickname);
                            setResult(RESULT_OK);
                            finish();
                        }
                        SoundEffectManager.play(R.raw.complete);
                    }
                });
                break;

        }


    }
}
