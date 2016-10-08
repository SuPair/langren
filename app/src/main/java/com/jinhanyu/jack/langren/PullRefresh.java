package com.jinhanyu.jack.langren;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Created by anzhuo on 2016/9/26.
 */
public class PullRefresh extends FrameLayout implements PtrUIHandler {
    private ImageView iv_icon;
    private TextView tv_hint;
    private AnimationDrawable drawable;
    

    public PullRefresh(Context context) {
        this(context, null);
    }

    public PullRefresh(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefresh(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    //Content 重新回到顶部， Header 消失，整个下拉刷新过程完全结束以后，重置 View。
    @Override
    public void onUIReset(PtrFrameLayout frame) {


    }


    //准备刷新，Header 将要出现时调用。
    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        tv_hint.setText("下拉刷新");
        iv_icon.setImageResource(R.mipmap.z_arrow_down);

    }


    //开始刷新，Header 进入刷新状态之前调用。
    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        tv_hint.setText("正在加载");
        iv_icon.setImageDrawable(drawable);
        drawable.start();
    }


    //刷新结束，Header 开始向上移动之前调用。
    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        tv_hint.setText("加载完成");
        iv_icon.setImageDrawable(drawable);
        drawable.stop();
    }


    //下拉过程中位置变化回调。
    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        int otr = frame.getOffsetToRefresh();//刷新的分界线  默认为head的1.2倍
        int current = ptrIndicator.getCurrentPosY();//下拉框的最顶端位置
        int last = ptrIndicator.getLastPosY();//下拉框顶端最后出现的位置
        if (current < otr && last >= otr) {
            if (isUnderTouch&&status==PtrFrameLayout.PTR_STATUS_PREPARE){
                tv_hint.setText("下拉刷新");
                iv_icon.setImageResource(R.mipmap.z_arrow_down);
            }
        } else if (current > otr && last <= otr) {
            tv_hint.setText("松开刷新");
            iv_icon.setImageResource(R.mipmap.z_arrow_up);
        }
    }


    public void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.prtfrmart, this);
        iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        tv_hint = (TextView) view.findViewById(R.id.tv_hint);
        drawable = (AnimationDrawable) getResources().getDrawable(R.drawable.animation_list);
    }
}

