package com.jinhanyu.jack.langren.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.entity.UserInfo;

import java.util.List;

/**
 * Created by anzhuo on 2016/9/23.
 */
public class GameDetailAdapter extends CommonAdapter<UserInfo> {
    public GameDetailAdapter(Context context, List<UserInfo> data) {
        super(context, data);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view==null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.game_detail_item,null);
            holder = new ViewHolder();
            holder.head = (SimpleDraweeView) view.findViewById(R.id.gameDetail_item_head);
            holder.name = (TextView) view.findViewById(R.id.gameDetail_item_name);
            holder.mark = (Spinner) view.findViewById(R.id.gameDetail_item_marker);
            view.setTag(holder);
        }
        holder = (ViewHolder) view.getTag();
        final UserInfo info = data.get(i);
        holder.head.setImageURI(info.getHead());
        if(info.getGameRole().isDead()){
            holder.head.getHierarchy().setOverlayImage(context.getResources().getDrawable(R.mipmap.dead));
        }
        holder.name.setText(info.getNickname());
        holder.mark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                info.getGameRole().setSign_type(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        holder.mark.setSelection(info.getGameRole().getSign_type());
        return view;
    }

    class ViewHolder{
        SimpleDraweeView head;
        TextView name;
        Spinner mark;
    }
}
