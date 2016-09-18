package com.jinhanyu.jack.langren.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.baoyz.actionsheet.ActionSheet;

/**
 * Created by anzhuo on 2016/9/12.
 */
public abstract class CommonActivity extends AppCompatActivity implements ActionSheet.ActionSheetListener {

    private ActionSheet.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareViews();
        prepareSocket();


        builder = ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle("Cancel")
                .setOtherButtonTitles("排行榜", "切换账号", "退出游戏")
                .setCancelableOnTouchOutside(true)
                .setListener(this);

    }

    private Handler handler= new Handler();

    protected void refreshUI(final BaseAdapter adapter){
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
           switch (index){
               case 0:
                   startActivity(new Intent(this,GameTopActivity.class));
                   break;
               case 1:
                   startActivity(new Intent(this,LoginActivty.class));
                   break;
               case 2:
                   break;
           }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_MENU){
            if(!isShowing) {
                builder.show();
                isShowing = true;
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(CommonActivity.this, "请不要退出游戏", Toast.LENGTH_SHORT).show();
    }
}
