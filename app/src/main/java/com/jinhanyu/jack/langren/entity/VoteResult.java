package com.jinhanyu.jack.langren.entity;

/**
 * Created by anzhuo on 2016/9/18.
 */
public class VoteResult {
    public VoteResult(String fromUserName, String toUserName) {
        this.fromUserName = fromUserName;
        this.toUserName = toUserName;
    }

    private String fromUserName,toUserName;

    public VoteResult() {
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }
}
