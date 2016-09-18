package com.jinhanyu.jack.langren.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.User;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.jinhanyu.jack.langren.ui.GameMainActivity;

import java.util.List;

/**
 * Created by anzhuo on 2016/9/9.
 */
public class PredictorAdapter extends CommonAdapter<UserInfo> {
    private  UserInfo info;
    private String [] types={"村民","狼人","预言家","女巫","守卫","猎人"};
    private ArrayAdapter arrayAdapter;
    public PredictorAdapter(Context context, List<UserInfo> data) {
        super(context, data);
        arrayAdapter=new ArrayAdapter(context,android.R.layout.simple_list_item_1,types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.predictor_item,null);
            viewHolder=new ViewHolder();
            viewHolder.head= (SimpleDraweeView) view.findViewById(R.id.gamePredictor_item_head);
            viewHolder.username= (TextView) view.findViewById(R.id.gamePredictor_item_name);
            viewHolder.state= (TextView) view.findViewById(R.id.gamePredictor_item_state);
            viewHolder.choose= (TextView) view.findViewById(R.id.gamePredictor_item_test);
            viewHolder.type= (Spinner) view.findViewById(R.id.gamePredictor_item_marker);
            view.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) view.getTag();
        }
        info=data.get(i);
        viewHolder.head.setImageURI(info.getHead());
        viewHolder.username.setText(info.getName());
        if(info.isDead()){
            viewHolder.state.setText(R.string.isDead);
        }else{
            viewHolder.state.setText(R.string.isLiving);
        }
        viewHolder.choose.setText(R.string.predictorSkill);
        viewHolder.type.setAdapter(arrayAdapter);
        viewHolder.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                info.setType(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        viewHolder.choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(context);
                dialog.setTitle("请做出您的选择：");
                dialog.setMessage("您确定要守卫该玩家吗?");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(context, GameMainActivity.class);
                        context.startActivity(intent);
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
        });
        return view;
    }

    class ViewHolder{
        SimpleDraweeView head;
        TextView username,state,choose;
        Spinner type;
    }
}
