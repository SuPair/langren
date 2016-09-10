package com.jinhanyu.jack.langren.entity;

/**
 * Created by anzhuo on 2016/9/10.
 */
public class UserInfo {
private String head,name,userId;
    private int vote_police;
    private int vote_kill;
    private int vote_wolf;
    private int score;
    private int sign_type;
    private boolean isDead;
    private boolean isSpeaking;
    private boolean isReady;
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public boolean isSpeaking() {
        return isSpeaking;
    }

    public void setSpeaking(boolean speaking) {
        isSpeaking = speaking;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSign_type() {
        return sign_type;
    }

    public void setSign_type(int sign_type) {
        this.sign_type = sign_type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getVote_kill() {
        return vote_kill;
    }

    public void setVote_kill(int vote_kill) {
        this.vote_kill = vote_kill;
    }

    public int getVote_police() {
        return vote_police;
    }

    public void setVote_police(int vote_police) {
        this.vote_police = vote_police;
    }

    public int getVote_wolf() {
        return vote_wolf;
    }

    public void setVote_wolf(int vote_wolf) {
        this.vote_wolf = vote_wolf;
    }


}
