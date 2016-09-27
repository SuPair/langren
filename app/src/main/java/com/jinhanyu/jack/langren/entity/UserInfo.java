package com.jinhanyu.jack.langren.entity;

import com.jinhanyu.jack.langren.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

/**
 * Created by anzhuo on 2016/9/10.
 */
public class UserInfo {
    private String head ="res://com.jinhanyu.jack.langren/"+ R.mipmap.user_head_bg;
    private String username;
    private String userId;
    private String nickname;


    private GameRole gameRole = new GameRole();

    public void resetGameRole(){
        gameRole = new GameRole();
    }

    public GameRole getGameRole() {
        return gameRole;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    private int score;




    public void populateFromParseServer(ParseUser parseUser){
        setUserId(parseUser.getObjectId());
        ParseFile file = (ParseFile) parseUser.get("head");
        if(file!=null)
           setHead(file.getUrl());
        setUsername(parseUser.getUsername());
        setScore((Integer) parseUser.get("score"));
        setTitle((String) parseUser.get("title"));
        setNickname((String) parseUser.get("nickname"));
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }




}
