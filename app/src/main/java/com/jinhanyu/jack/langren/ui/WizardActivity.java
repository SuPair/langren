package com.jinhanyu.jack.langren.ui;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jinhanyu.jack.langren.ActionPerformer;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.TickTimer;
import com.jinhanyu.jack.langren.adapter.GameRoleCommonAdapter;
import com.jinhanyu.jack.langren.entity.GameRole;

import io.socket.emitter.Emitter;

public class WizardActivity extends CommonActivity implements ActionPerformer{
    private ListView listView;
    private GameRoleCommonAdapter adapter;

    private TickTimer tickTimer;
    private TextView time_label,action_done_label;
    private ImageView iv_antidote,iv_poison;

    private String saveUserId;
    private String poisonUserId;

    private AlertDialog.Builder dialog;


    @Override
    protected void prepareViews() {
        setContentView(R.layout.wizard);
        listView= (ListView) findViewById(R.id.wizard_listView);
        time_label = (TextView) findViewById(R.id.time_label);
        action_done_label = (TextView) findViewById(R.id.action_done_label);
        iv_antidote = (ImageView) findViewById(R.id.iv_antidote);
        iv_poison = (ImageView) findViewById(R.id.iv_poison);
        if(MainApplication.roomInfo.isHasSaved())
            iv_antidote.setImageResource(R.mipmap.noantidote);
        if(MainApplication.roomInfo.isHasPoisoned())
            iv_poison.setImageResource(R.mipmap.nopoison);
        adapter=new GameRoleCommonAdapter(this, MainApplication.roomInfo.getUsers(), GameRole.Type.Wizard);
        listView.setAdapter(adapter);

        tickTimer = new TickTimer(time_label,15,adapter){
            @Override
            protected void onTimeEnd() {
                super.onTimeEnd();
                MainApplication.socket.emit("wizard",MainApplication.roomInfo.getRoomId(),saveUserId,poisonUserId);
            }
        };
        tickTimer.startTick();
        dialog = new AlertDialog.Builder(WizardActivity.this);
    }

    @Override
    protected void prepareSocket() {
        MainApplication.socket.on("wizard", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                  final String userId = (String) args[0];
                  runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          if(userId==null)
                              Toast.makeText(WizardActivity.this, "狼人今晚没有杀人，您不需要使用解药", Toast.LENGTH_SHORT).show();
                          else{
                              dialog.setTitle("救人：");
                              if(MainApplication.roomInfo.isHasSaved()) {
                                  dialog.setMessage("很遗憾，您的解药用完了，不能救他");
                                  dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which) {
                                          dialog.dismiss();
                                      }
                                  });
                              }else{
                                  dialog.setMessage("狼人杀了 "+ MainApplication.roomInfo.findUserInRoom(userId).getNickname()+",您要救他吗？");
                                  dialog.setPositiveButton("救", new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which) {
                                          saveUserId = userId;
                                      }
                                  });
                                  dialog.setNegativeButton("算了", new DialogInterface.OnClickListener() {
                                      @Override
                                      public void onClick(DialogInterface dialog, int which) {
                                          dialog.dismiss();
                                      }
                                  });
                              }

                              dialog.show();
                          }
                      }
                  });
            }
        });
    }

    @Override
    protected void unbindSocket() {
          MainApplication.socket.off("wizard");
    }

    @Override
    public void doAction(Object... params) {
        poisonUserId = (String) params[0];
        action_done_label.setText("毒人成功");
    }
}
