package com.jinhanyu.jack.langren.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.RoundImageViewByXfermode;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by kinpowoo on 9/13/16.
 */
public class GameTopAdapter extends CommonAdapter {
    private UserInfo userInfo;
    public GameTopAdapter(Context context, List data) {
        super(context, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.game_top_item,null);
            viewHolder=new ViewHolder();
            viewHolder.portrait= (RoundImageViewByXfermode) convertView.findViewById(R.id.game_top_item_head);
            viewHolder.playerName= (TextView) convertView.findViewById(R.id.game_top_item_name);
            viewHolder.score= (TextView) convertView.findViewById(R.id.game_top_item_score);
            viewHolder.title= (TextView) convertView.findViewById(R.id.game_top_item_title);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
       userInfo= (UserInfo) data.get(position);
        Picasso.with(context).load(userInfo.getHead()).into(viewHolder.portrait);
        viewHolder.playerName.setText(userInfo.getName());
        viewHolder.score.setText(userInfo.getScore()+"");
        int score=userInfo.getScore();
        if(score<10){
            viewHolder.title.setText("默默无名");
        }else if (score < 20) {
            viewHolder.title.setText("初为人知");
        }else if(score<30){
            viewHolder.title.setText("小有名气");
        }else if(score<40){
            viewHolder.title.setText("受到尊敬");
        }else if(score<50){
            viewHolder.title.setText("耳熟能详");
        }else if(score<60){
            viewHolder.title.setText("广为人知");
        }else if(score<80){
            viewHolder.title.setText("远近驰名");
        }else if(score<100){
            viewHolder.title.setText("不可企及");
        }else if (score<150){
            viewHolder.title.setText("传说");
        }else{
            viewHolder.title.setText("上帝的存在");
        }

        return convertView;
    }

    class ViewHolder{
    RoundImageViewByXfermode portrait;
        TextView playerName,score,title;
    }
}
