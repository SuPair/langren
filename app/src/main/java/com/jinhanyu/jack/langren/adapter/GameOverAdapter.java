package com.jinhanyu.jack.langren.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.entity.UserInfo;

import java.util.List;

/**
 * Created by anzhuo on 2016/9/22.
 */
public class GameOverAdapter extends CommonAdapter<UserInfo> {

    public GameOverAdapter(Context context, List<UserInfo> data) {
        super(context, data);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder  viewHolder;
        if(view==null){
            view = LayoutInflater.from(context).inflate(R.layout.game_over_item,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_head = (SimpleDraweeView) view.findViewById(R.id.iv_head);
            viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            viewHolder.tv_score = (TextView) view.findViewById(R.id.tv_score);
            viewHolder.identification_label= (TextView) view.findViewById(R.id.identification_label);
            view.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) view.getTag();
        UserInfo info = data.get(i);
        viewHolder.iv_head.setImageURI(info.getHead());
        viewHolder.tv_name.setText(info.getNickname());
        viewHolder.identification_label.setText(info.getGameRole().getType().getName());
        viewHolder.tv_score.setText(info.getGameRole().getScore()+"");

        return view;
    }


    class ViewHolder{
        SimpleDraweeView iv_head;
        TextView tv_name;
        TextView identification_label;
        TextView tv_score;
    }
}
