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
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.entity.GameRole;
import com.jinhanyu.jack.langren.entity.UserInfo;

import java.util.List;

/**
 * Created by anzhuo on 2016/9/26.
 */
public class PoliceAdapter extends CommonAdapter<UserInfo> implements ActionPerformer{
    private boolean hasDelivered;
    private boolean timerCanceled;

    public PoliceAdapter(Context context, List<UserInfo> data) {
        super(context, data);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.police_item, null);
            viewHolder = new ViewHolder();
            viewHolder.head = (SimpleDraweeView) view.findViewById(R.id.gamePredictor_item_head);
            viewHolder.username = (TextView) view.findViewById(R.id.gamePredictor_item_name);
            viewHolder.state = (TextView) view.findViewById(R.id.gamePredictor_item_state);
            viewHolder.choose = (TextView) view.findViewById(R.id.gamePredictor_item_test);
            viewHolder.type = (Spinner) view.findViewById(R.id.gamePredictor_item_marker);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final UserInfo info = data.get(i);
        viewHolder.head.setImageURI(info.getHead());
        viewHolder.username.setText(info.getUsername());
        if (info.getGameRole().isDead()) {
            viewHolder.state.setText(R.string.isDead);
        } else {
            viewHolder.state.setText(R.string.isLiving);
        }
        viewHolder.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                info.getGameRole().setType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        viewHolder.type.setSelection(info.getGameRole().getSign_type());
        if(info.getGameRole().getType()!= GameRole.Type.Unknown)
            viewHolder.type.setEnabled(false);
        viewHolder.choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasDelivered) {
                    Toast.makeText(context, "您的警徽已经移交", Toast.LENGTH_SHORT).show();
                } else if (timerCanceled) {
                    Toast.makeText(context, "时间已经到啦，等待结果吧", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle("请做出您的选择：");
                    dialog.setMessage("您确定要将警徽传给该玩家吗?");
                    dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            viewHolder.choose.setEnabled(false);
                            if (!timerCanceled)
                                ((ActionPerformer) context).doAction(info.getUserId());
                            hasDelivered = true;
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

    class ViewHolder {
        SimpleDraweeView head;
        TextView username, state, choose;
        Spinner type;
    }
}