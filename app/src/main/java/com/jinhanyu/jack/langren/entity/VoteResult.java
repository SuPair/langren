package com.jinhanyu.jack.langren.entity;

/**
 * Created by anzhuo on 2016/9/18.
 */
public class VoteResult {


    private UserInfo fromUser,toUser;

    public VoteResult(UserInfo fromUser, UserInfo toUser) {
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    public UserInfo getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserInfo fromUser) {
        this.fromUser = fromUser;
    }

    public UserInfo getToUser() {
        return toUser;
    }

    public void setToUser(UserInfo toUser) {
        this.toUser = toUser;
    }

    public VoteResult() {

    }


}
