package com.jinhanyu.jack.langren.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.RoundImageViewByXfermode;
import com.jinhanyu.jack.langren.User;
import com.jinhanyu.jack.langren.entity.UserInfo;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by anzhuo on 2016/9/10.
 */
public class GalleryAdapter extends BaseAdapter {
    private List<UserInfo> list;
    Context context;
    UserInfo info;
    public GalleryAdapter(Context context,List<UserInfo> list){
        this.list=list;
        this.context=context;
    }
    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderForGallery viewHolder;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.room_item,null);
            viewHolder=new ViewHolderForGallery();
            viewHolder.portrait= (RoundImageViewByXfermode) convertView.findViewById(R.id.iv_waitRoom_item_portrait);
            viewHolder.userName= (TextView) convertView.findViewById(R.id.tv_waitRoom_item_userId);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolderForGallery) convertView.getTag();
        }
        info=list.get(position);
        Picasso.with(context).load(info.getHead()).into(viewHolder.portrait);
        viewHolder.userName.setText(info.getName());
        return convertView;
    }
}
class ViewHolderForGallery{
    RoundImageViewByXfermode portrait;
    TextView userName;
}