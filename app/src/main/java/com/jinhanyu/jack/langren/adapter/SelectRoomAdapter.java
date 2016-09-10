package com.jinhanyu.jack.langren.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.entity.RoomInfo;
import com.jinhanyu.jack.langren.ui.RoomActivity;

import java.util.List;

/**
 * Created by anzhuo on 2016/9/9.
 */
public class SelectRoomAdapter extends CommonAdapter<RoomInfo> {


    public SelectRoomAdapter(Context context, List<RoomInfo> data) {
        super(context, data);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.select_room_item, null);
            viewHolder = new ViewHolder();
            viewHolder.roomName = (TextView) view.findViewById(R.id.tv_room_name);
            viewHolder.peopleNum = (TextView) view.findViewById(R.id.tv_room_peopleNum);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, RoomActivity.class);
                context.startActivity(intent);
            }
        });
        RoomInfo roomInfo = data.get(i);
        viewHolder.peopleNum.setText(roomInfo.getPeopleNum()+"");
        viewHolder.roomName.setText(roomInfo.getRoomName());
        return view;
    }
}

class ViewHolder {
    TextView roomName;
    TextView peopleNum;
}