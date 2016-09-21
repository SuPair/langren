package com.jinhanyu.jack.langren.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.langren.ActionPerformer;
import com.jinhanyu.jack.langren.MainApplication;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.entity.UserInfo;

import java.util.List;

/**
 * Created by anzhuo on 2016/9/9.
 */
public class GuardAdapter extends CommonAdapter<UserInfo> implements ActionPerformer{

    private boolean timerCanceled;

    private boolean hasGuarded;

    public GuardAdapter(Context context, List<UserInfo> data) {
        super(context, data);

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.guard_item,null);
            viewHolder=new ViewHolder();
            viewHolder.head= (SimpleDraweeView) view.findViewById(R.id.gameGuard_item_head);
            viewHolder.username= (TextView) view.findViewById(R.id.gameGuard_item_name);
            viewHolder.state= (TextView) view.findViewById(R.id.gameGuard_item_state);
            viewHolder.choose= (TextView) view.findViewById(R.id.gameGuard_item_choose);
            viewHolder.type= (Spinner) view.findViewById(R.id.gameGuard_item_marker);
            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
        final UserInfo info=data.get(i);
        viewHolder.head.setImageURI(info.getHead());
        viewHolder.username.setText(info.getUsername());
        if(info.getGameRole().isDead()){
            viewHolder.state.setText(R.string.isDead);
        }else{
            viewHolder.state.setText(R.string.isLiving);
        }
        viewHolder.choose.setText(R.string.guardSkill);

        viewHolder.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                info.getGameRole().setType(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        viewHolder.choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasGuarded) {
                    Toast.makeText(context, "你已经守卫过一次了", Toast.LENGTH_SHORT).show();
                }else if(info.getUserId().equals(MainApplication.roomInfo.getLastGuardedUserId())){
                    Toast.makeText(context, "不能连续两个夜晚守卫同一个人！", Toast.LENGTH_SHORT).show();
                }else if (timerCanceled) {
                    Toast.makeText(context, "时间已经到啦，等待结果吧", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("请做出您的选择：");
                    dialog.setMessage("您确定要验证该玩家的身份吗?");
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            viewHolder.choose.setEnabled(false);
                            viewHolder.choose.setBackgroundResource(R.drawable.button_clicked);
                            ((ActionPerformer) context).doAction(info.getUserId());
                            hasGuarded = true;
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

    @Override
    public void doAction(Object... params) {
        timerCanceled = true;
    }

    class ViewHolder{
        SimpleDraweeView head;
        TextView username,state,choose;
        Spinner type;

    }
}
