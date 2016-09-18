package com.jinhanyu.jack.langren.ui;

import android.widget.Toast;

import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;

import io.socket.emitter.Emitter;

public class PredictorActivity extends CommonActivity {


    @Override
    protected void prepareViews() {
        setContentView(R.layout.predictor);
    }

    @Override
    protected void prepareSocket() {

        MainApplication.socket.on("predictorMsg", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                   int type = (int) args[0];
                   if(type==0){
                       Toast.makeText(PredictorActivity.this, "您要验的人的身份是好人", Toast.LENGTH_SHORT).show();

                   }else if(type==1){
                       Toast.makeText(PredictorActivity.this, "您要验的人的身份是坏人", Toast.LENGTH_SHORT).show();
                   }
            }
        });

    }

    @Override
    protected void unbindSocket() {
        MainApplication.socket.off("predictorMsg");
    }
}
