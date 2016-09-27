package com.jinhanyu.jack.langren.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.jinhanyu.jack.langren.R;
import com.jinhanyu.jack.langren.entity.VoteResult;

import java.util.List;

/**
 * Created by anzhuo on 2016/9/18.
 */
public class VoteResultAdapter extends CommonAdapter<VoteResult> {

    public VoteResultAdapter(Context context, List<VoteResult> data) {
        super(context, data);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.vote_result_item, null);
            viewHolder = new ViewHolder();
            viewHolder.fromUserName = (TextView) view.findViewById(R.id.fromUserName);
            viewHolder.toUserName = (TextView) view.findViewById(R.id.toUserName);
            viewHolder.fromUserHead = (SimpleDraweeView) view.findViewById(R.id.fromUserHead);
            viewHolder.toUserHead = (SimpleDraweeView) view.findViewById(R.id.toUserHead);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        VoteResult voteResult = data.get(i);
        viewHolder.fromUserName.setText(voteResult.getFromUser().getNickname());
        viewHolder.toUserName.setText(voteResult.getToUser().getNickname());
        viewHolder.fromUserHead.setImageURI(voteResult.getFromUser().getHead());
        viewHolder.toUserHead.setImageURI(voteResult.getToUser().getHead());
        return view;
    }

    class ViewHolder {
        public SimpleDraweeView fromUserHead, toUserHead;
        public TextView fromUserName, toUserName;
    }
}
