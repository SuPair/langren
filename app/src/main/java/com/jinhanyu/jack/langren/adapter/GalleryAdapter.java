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
 * Created by anzhuo on 2016/9/10.
 */
public class GalleryAdapter extends CommonAdapter<UserInfo> {


    public GalleryAdapter(Context context, List<UserInfo> data) {
        super(context, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderForGallery viewHolder;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.game_main_item,null);
            viewHolder=new ViewHolderForGallery();
            viewHolder.portrait= (SimpleDraweeView) convertView.findViewById(R.id.iv_waitRoom_item_portrait);
            viewHolder.userName= (TextView) convertView.findViewById(R.id.tv_waitRoom_item_userId);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolderForGallery) convertView.getTag();
        }
        UserInfo info=data.get(position);
        viewHolder.portrait.setImageURI(info.getHead());
        viewHolder.userName.setText(info.getNickname());
        return convertView;
    }


    class ViewHolderForGallery{
        SimpleDraweeView portrait;
        TextView userName;
    }
}
