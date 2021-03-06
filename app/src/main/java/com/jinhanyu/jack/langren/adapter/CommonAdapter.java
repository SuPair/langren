package com.jinhanyu.jack.langren.adapter;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by anzhuo on 2016/9/9.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    protected Context context;
    protected List<T> data;

    public CommonAdapter(Context context, List<T> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data==null? 0 :data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


}
