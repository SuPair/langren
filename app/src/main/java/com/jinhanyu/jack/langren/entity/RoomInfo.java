package com.jinhanyu.jack.langren.entity;

import com.jinhanyu.jack.langren.Me;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anzhuo on 2016/9/10.
 */
public class RoomInfo{

    private String roomId;
    private int maxCount;
    private int currentCount;
    private String name;
    private boolean hasSaved;
    private boolean hasPoisoned;
    private String lastGuardedUserId;
    private String policeId;
    private List<GameRole.Type> types = new ArrayList<>();
    private String typesString;

    public List<GameRole.Type> getTypes() {
        return types;
    }

    public String getPoliceId() {
        return policeId;
    }

    public void setPoliceId(String policeId) {
        this.policeId = policeId;
    }

    public UserInfo findUserInRoom(String userId) {
        for (UserInfo info : users) {
            if (info.getUserId().equals(userId)) {
                return info;
            }
        }
        throw new RuntimeException("客户端：  用户未找到");
    }

    public UserInfo findMeInRoom() {
        for (UserInfo info : users) {
            if (info.getUserId().equals(Me.getUserId())) {
                return info;
            }
        }
        throw new RuntimeException("客户端：  房间里没有我");
    }

    public int findUserIndexInRoom(String userId) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(userId)) {
                return i;
            }
        }
        throw new RuntimeException("客户端：  用户未找到");
    }

    public int findMyIndexInRoom() {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(Me.getUserId())) {
                return i;
            }
        }
        throw new RuntimeException("客户端：  房间里没有我");
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
    private List<UserInfo> users = new ArrayList<UserInfo>();


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

    public void changePeopleNum(int diff) {
        this.currentCount += diff;
    }


    public static final int VOTE_POLICE = 0;
    public static final int VOTE_WOLF = 1;
    public static final int VOTE_KILL = 2;

    public List<UserInfo> getAliveUsers() {
        List<UserInfo> userInfos = new ArrayList<>();
        for (UserInfo userInfo : users) {
            if (!userInfo.getGameRole().isDead())
                userInfos.add(userInfo);
        }
        return userInfos;
    }


    public void update() {
        try {
            for (UserInfo info : users)
                info.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void resetRoom() {
        policeId = null;
        for(UserInfo userInfo : users){
            GameRole gameRole = userInfo.getGameRole();
            gameRole.setDead(false);
            gameRole.setType(6);
        }
    }

    public String getTypesString() {
        if(typesString==null){
            StringBuilder sb = new StringBuilder("身份信息: ");
            String divider = " ";
            int citizenCount= 0;
            int wolfCount = 0;
            for(GameRole.Type type : types){
                if(type== GameRole.Type.Citizen){
                   citizenCount++;
                }else if(type== GameRole.Type.Wolf){
                    wolfCount++;
                }else {
                    sb.append(type.getName()).append(divider);
                }
            }
            if(citizenCount!=0){
                sb.append(GameRole.Type.Citizen.getName()).append(citizenCount).append(divider);
            }
            if(wolfCount!=0){
                sb.append(GameRole.Type.Wolf.getName()).append(wolfCount).append(divider);
            }
            typesString = sb.toString();
        }
        return typesString;

    }
}
