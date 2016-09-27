
package com.jinhanyu.jack.langren.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.langren.ActionPerformer;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.entity.GameRole;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.jinhanyu.jack.langren.ui.GameMainActivity;

import java.util.List;

/**
 * Created by anzhuo on 2016/9/9.
 */
public class HunterAdapter extends CommonAdapter<UserInfo> implements ActionPerformer{

    private boolean hasShot;
    private boolean timerCanceled;

    public HunterAdapter(Context context, List<UserInfo> data) {
        super(context, data);
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.hunter_item, null);
            viewHolder = new ViewHolder();

            viewHolder.head = (SimpleDraweeView) view.findViewById(R.id.gameHunter_item_head);
            viewHolder.name = (TextView) view.findViewById(R.id.gameHunter_item_name);
            viewHolder.state = (TextView) view.findViewById(R.id.gameHunter_item_state);
            viewHolder.choose = (TextView) view.findViewById(R.id.gameHunter_item_shoot);
            viewHolder.type = (Spinner) view.findViewById(R.id.gameHunter_item_marker);

            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final UserInfo userInfo = data.get(i);

        viewHolder.head.setImageURI(userInfo.getHead());
        viewHolder.name.setText(userInfo.getUsername());
        if (userInfo.getGameRole().isDead()) {
            viewHolder.state.setText(R.string.isDead);
        } else {
            viewHolder.state.setText(R.string.isLiving);
        }

        viewHolder.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userInfo.getGameRole().setType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        viewHolder.type.setSelection(userInfo.getGameRole().getSign_type());
        if (userInfo.getGameRole().getType() != GameRole.Type.Unknown)
            viewHolder.type.setEnabled(false);
        viewHolder.choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasShot) {
                    Toast.makeText(context, "你已经开过一次枪了", Toast.LENGTH_SHORT).show();
                } else if (timerCanceled) {
                    Toast.makeText(context, "时间已经到啦，等待结果吧", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setIcon(R.mipmap.warning);
                    builder.setTitle("请谨慎选择！");
                    builder.setMessage("确定开枪击毙这位玩家吗？");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            viewHolder.choose.setEnabled(false);
                            if (!timerCanceled)
                                ((ActionPerformer) context).doAction(userInfo.getUserId());
                            hasShot = true;
                            dialog.dismiss();

                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
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
        TextView name, state, choose;
        Spinner type;

    }


}

