package com.jinhanyu.jack.langren.entity;

/**
 * Created by anzhuo on 2016/9/20.
 */
public class GameRole {
    private Type type = Type.Citizen;
    public  Type getType() {
        return type;
    }

    public void setType(int type) {
        this.type = Type.values()[type];
    }

    /**
     *  标记类型，只供客户端使用
     */
    private int     sign_type;
    private boolean isDead;
    private boolean isReady;
    private int score;

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


    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }




    public int getSign_type() {
        return sign_type;
    }

    public void setSign_type(int sign_type) {
        this.sign_type = sign_type;
    }


    public enum Type{
        Citizen("村民"),Wolf("狼人"),Predictor("预言家"),Wizard("女巫"),Guard("守卫"),Hunter("猎人");

        Type(String name){
            this.name = name;
        }

        private String name;

        public String getName(){
            return name;
        }


    }
}
