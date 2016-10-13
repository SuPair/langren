package com.jinhanyu.jack.langren.entity;

/**
 * Created by anzhuo on 2016/10/13.
 */
public class GameResult implements Comparable<GameResult>{

    private String head;
    private String nickname;
    private int score;
    private GameRole.Type type = GameRole.Type.Unknown;


    public GameRole.Type getType() {
        return type;
    }

    public void setType(int type) {
        this.type = GameRole.Type.values()[type];
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int compareTo(GameResult userInfo) {
        if(score> userInfo.score)
            return 1;
        else
            return -1;
    }
}
