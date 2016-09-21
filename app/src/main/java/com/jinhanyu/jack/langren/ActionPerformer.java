package com.jinhanyu.jack.langren;

/**
 * Created by anzhuo on 2016/9/20.
 * 一个简单的接口，方便activity和adapter之间通信的
 */
public interface ActionPerformer {

    void doAction(Object... params);
}
