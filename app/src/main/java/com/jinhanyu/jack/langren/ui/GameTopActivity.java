package com.jinhanyu.jack.langren.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jinhanyu.jack.langren.R;

public class GameTopActivity extends AppCompatActivity implements View.OnClickListener{
   private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_top);

        back = (Button) findViewById(R.id.back);

        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
        }
    }
}
