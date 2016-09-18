
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
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.jinhanyu.jack.langren.ui.GameMainActivity;

import java.util.List;

/**
 * Created by anzhuo on 2016/9/9.
 */
public class HunterAdapter extends CommonAdapter<UserInfo> {
    private String[] type = {"村民", "狼人", "预言家", "女巫", "守卫", "猎人"};
    private ArrayAdapter arrayAdapter;


    public HunterAdapter(Context context, List<UserInfo> data) {
        super(context, data);
        arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, type);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.hunter_item, null);
            viewHolder = new ViewHolder();

            viewHolder.head = (SimpleDraweeView) view.findViewById(R.id.gameHunter_item_head);
            viewHolder.name = (TextView) view.findViewById(R.id.gameHunter_item_name);
            viewHolder.state = (TextView) view.findViewById(R.id.gameHunter_item_state);
            viewHolder.choose = (TextView) view.findViewById(R.id.gameHunter_item_shoot);
            viewHolder.type = (Spinner) view.findViewById(R.id.gameHunter_item_marker);

            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) view.getTag();
        final UserInfo userInfo = data.get(i);

        viewHolder.head.setImageURI(userInfo.getHead());
        viewHolder.name.setText(userInfo.getName());
        if (userInfo.isDead()) {
            viewHolder.state.setText(R.string.isDead);
        } else {
            viewHolder.state.setText(R.string.isLiving);
        }
        viewHolder.type.setAdapter(arrayAdapter);
        viewHolder.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userInfo.setType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        viewHolder.choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.mipmap.warning);
                builder.setTitle("请谨慎选择！");
                builder.setMessage("确定开枪击毙这位玩家吗？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "操作成功！", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        Intent intent = new Intent(context, GameMainActivity.class);
                        context.startActivity(intent);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "已取消！", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
        return view;
    }

    class ViewHolder {
        SimpleDraweeView head;
        TextView name, state, choose;
        Spinner type;

    }


}

