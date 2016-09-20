package com.jinhanyu.jack.langren.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.entity.UserInfo;

import java.util.List;
import java.util.Timer;

/**
 * Created by anzhuo on 2016/9/9.
 */
public class WolfAdapter extends CommonAdapter<UserInfo> {


//    private String [] types={"村民","狼人","预言家","女巫","守卫","猎人"};  //0: 村民，1：狼人，2：预言家，3：女巫，4：守卫，5：猎人
    private ArrayAdapter arrayAdapter;
    private Timer timer;
    private boolean timerCanceled;

    private boolean hasVoted;



    public void notifyTimerCancel(){
         this.timerCanceled = true;
    }

    public WolfAdapter(Context context, List<UserInfo> data) {
        super(context, data);
        arrayAdapter=new ArrayAdapter(context,android.R.layout.simple_list_item_1,context.getResources().getStringArray(R.array.sign_type));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public WolfAdapter(Context context, List<UserInfo> data, Timer timer){
        this(context,data);
        this.timer = timer;


    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.wolf_item,null);
            viewHolder=new ViewHolder();
            viewHolder.head= (SimpleDraweeView) view.findViewById(R.id.gameWolf_item_head);
            viewHolder.username= (TextView) view.findViewById(R.id.gameWolf_item_name);
            viewHolder.state= (TextView) view.findViewById(R.id.gameWolf_item_state);
            viewHolder.choose= (TextView) view.findViewById(R.id.gameWolf_item_choose);
            viewHolder.type= (Spinner) view.findViewById(R.id.gameWolf_item_marker);
            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
        final UserInfo info=data.get(i);
        viewHolder.head.setImageURI(info.getHead());
        viewHolder.username.setText(info.getName());
        if(info.isDead()){
            viewHolder.state.setText(R.string.isDead);
        }else{
            viewHolder.state.setText(R.string.isLiving);
        }
        viewHolder.type.setAdapter(arrayAdapter);
        viewHolder.type.setSelection(info.getSign_type());
        viewHolder.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                info.setSign_type(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        viewHolder.choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hasVoted) {
                    Toast.makeText(context, "你已经击杀过一次了", Toast.LENGTH_SHORT).show();
                }else if(timerCanceled){
                    Toast.makeText(context, "时间已经到啦，等待结果吧", Toast.LENGTH_SHORT).show();
                }
                else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("请做出您的选择：");
                    dialog.setMessage("您确定要击杀该玩家吗?");
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            WolfAdapter.this.timer.cancel();
                            viewHolder.choose.setEnabled(false);
                            if(!timerCanceled)
                                MainApplication.socket.emit("wolf",MainApplication.roomInfo.getRoomId(),MainApplication.userInfo.getUserId(),info.getUserId());
                            hasVoted = true;
                        }
                    });
                    dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });
        return view;
    }

    class ViewHolder{
        SimpleDraweeView head;
        TextView username,state,choose;
        Spinner type;
    }
}
