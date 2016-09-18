package com.jinhanyu.jack.langren.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        view = LayoutInflater.from(context).inflate(R.layout.vote_result_item,null);
        TextView fromUserNameTextView = (TextView) view.findViewById(R.id.fromUserName);
        TextView toUserNameTextView = (TextView) view.findViewById(R.id.toUserName);

        VoteResult voteResult = data.get(i);
        fromUserNameTextView.setText(voteResult.getFromUserName());
        toUserNameTextView.setText(voteResult.getToUserName());

        return view;
    }
}
