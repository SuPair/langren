package com.jinhanyu.jack.langren.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by anzhuo on 2016/9/10.
 */
public class WaitRoomAdapter extends CommonAdapter<UserInfo> {
    UserInfo userInfo;
    public WaitRoomAdapter(Context context, List<UserInfo> data) {
        super(context, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderForUser viewHolder;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.room_item,null);
            viewHolder=new ViewHolderForUser();
            viewHolder.portrait= (ImageView) convertView.findViewById(R.id.iv_waitRoom_item_portrait);
            viewHolder.userName= (TextView) convertView.findViewById(R.id.tv_waitRoom_item_userId);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolderForUser) convertView.getTag();
        }
        userInfo=data.get(position);
        Picasso.with(context).load(userInfo.getHead()).into(viewHolder.portrait);
        viewHolder.userName.setText(userInfo.getName());
        return convertView;
    }
}
class ViewHolderForUser{
    ImageView portrait;
    TextView userName;
}