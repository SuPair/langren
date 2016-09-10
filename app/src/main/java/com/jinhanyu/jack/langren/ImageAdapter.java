package com.jinhanyu.jack.langren;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by anzhuo on 2016/9/7.
 */
public class ImageAdapter extends BaseAdapter {


    Context context;
    List<ParseObject>  users;

    public ImageAdapter(Context context, List<ParseObject> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
