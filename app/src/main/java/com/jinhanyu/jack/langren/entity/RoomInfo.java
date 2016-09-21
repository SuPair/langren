package com.jinhanyu.jack.langren.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anzhuo on 2016/9/10.
 */
public class RoomInfo implements Serializable{


    //voteRecord:[{fromUserId,toUserId}], vote:{userId:voteCount}, voteCounter // 投票计数器，都投完票就计算投票结果
    // users:[{userId, socket, dead, type}],   const hasSaved, const hasPoisoned, const lastGuardedUserId

    private  String roomId;
    private int maxCount;
    private int currentCount;
    private String name;
    private boolean hasSaved;
    private boolean hasPoisoned;
    private String  lastGuardedUserId;


    public  UserInfo findUserInRoom(String userId){
        for (UserInfo info : users) {
            if (info.getUserId().equals(userId)) {
                return info;
            }
        }
        throw new RuntimeException("客户端：  用户未找到");
    }


    public boolean isHasPoisoned() {
        return hasPoisoned;
    }

    public void setHasPoisoned(boolean hasPoisoned) {
        this.hasPoisoned = hasPoisoned;
    }

    public String getLastGuardedUserId() {
        return lastGuardedUserId;
    }

    public void setLastGuardedUserId(String lastGuardedUserId) {
        this.lastGuardedUserId = lastGuardedUserId;
    }

    public boolean isHasSaved() {
        return hasSaved;
    }

    public void setHasSaved(boolean hasSaved) {
        this.hasSaved = hasSaved;
    }

    private List<VoteResult> voteResults = new ArrayList<>();
    private List<UserInfo> users= new ArrayList<UserInfo>();

    public void resetUsers(){
         users = new ArrayList<>();
    }

    public void resetVoteResults(){
        voteResults = new ArrayList<>();
    }

    public List<UserInfo> getUsers() {
        return users;
    }

    public List<VoteResult> getVoteResults() {
        return voteResults;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }



    public int getCurrentCount() {
        return currentCount;
    }

    public void setCurrentCount(int currentCount) {
        this.currentCount = currentCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void changePeopleNum(int diff){
        this.currentCount +=diff;
    }
}
