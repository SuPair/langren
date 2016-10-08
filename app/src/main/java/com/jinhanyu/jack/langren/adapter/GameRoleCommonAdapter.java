package com.jinhanyu.jack.langren.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
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
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.entity.GameRole;
import com.jinhanyu.jack.langren.entity.UserInfo;

import java.util.List;

/**
 * Created by anzhuo on 2016/9/27.
 */
public class GameRoleCommonAdapter extends CommonAdapter<UserInfo> implements ActionPerformer {

    private boolean timerCanceled;
    private boolean hasActioned;
    private String actionStr;

    public GameRoleCommonAdapter(Context context, List<UserInfo> data,GameRole.Type type){
        super(context, data);
        Resources resources = context.getResources();
        switch (type){
            case Wolf:
                actionStr = resources.getString(R.string.wolfSkill);
                break;
            case Wizard:
                actionStr = resources.getString(R.string.wizardPoison);
                break;
            case Predictor:
                actionStr = resources.getString(R.string.predictorSkill);
                break;
            case Hunter:
                actionStr = resources.getString(R.string.hunterSkill);
                break;
            case Guard:
                actionStr = resources.getString(R.string.guardSkill);
                break;
            case Citizen:
                actionStr = resources.getString(R.string.citizenSkill);
                break;
            case Police:
                actionStr = resources.getString(R.string.policeSkill);
                break;
        }
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.game_role_common, null);
            viewHolder = new ViewHolder();
            viewHolder.head = (SimpleDraweeView) view.findViewById(R.id.item_head);
            viewHolder.username = (TextView) view.findViewById(R.id.item_name);
            viewHolder.state = (TextView) view.findViewById(R.id.item_state);
            viewHolder.action = (TextView) view.findViewById(R.id.item_action);
            viewHolder.type = (Spinner) view.findViewById(R.id.item_type);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final UserInfo info = data.get(i);
        viewHolder.head.setImageURI(info.getHead());
        viewHolder.username.setText(info.getNickname());
        if (info.getGameRole().isDead()) {
            viewHolder.state.setText(R.string.isDead);
        } else {
            viewHolder.state.setText(R.string.isLiving);
        }
        viewHolder.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                info.getGameRole().setSign_type(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        viewHolder.type.setSelection(info.getGameRole().getSign_type());
        if(info.getGameRole().getType()!= GameRole.Type.Unknown)
            viewHolder.type.setEnabled(false);
        viewHolder.action.setText(actionStr);
        viewHolder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasActioned) {
                    Toast.makeText(context, "你已经"+actionStr+"过一次了", Toast.LENGTH_SHORT).show();
                } else if (timerCanceled) {
                    Toast.makeText(context, "时间已经到啦，等待结果吧", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("请做出您的选择：");
                    dialog.setMessage("您确定要"+actionStr+"该玩家吗?");
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            viewHolder.action.setEnabled(false);
                            if (!timerCanceled)
                                ((ActionPerformer) context).doAction(info.getUserId());
                            hasActioned = true;
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

    public void reset(){
        timerCanceled = false;
    }

    class ViewHolder{
        SimpleDraweeView head;
        TextView username,state,action;
        Spinner type;
    }
}
