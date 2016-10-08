package com.jinhanyu.jack.langren.entity;

import com.jinhanyu.jack.langren.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

/**
 * Created by anzhuo on 2016/9/10.
 *
 */
public class UserInfo extends ParseUser implements Comparable<UserInfo>{

    public static String default_head ="res://com.jinhanyu.jack.langren/"+ R.mipmap.user_head_bg;

    private GameRole gameRole = new GameRole();

    public GameRole getGameRole() {
        return gameRole;
    }

    public String getNickname() {
        return getString("nickname");
    }

    public String getTitle() {
        return getString("title");
    }

    public String getUsername() {
        return getString("username");
    }

    public int getScore() {
        return getInt("score");
    }

    public String getHead() {
        ParseFile parseFile = getParseFile("head");
        if(parseFile==null)
            return default_head;
        return parseFile.getUrl();
    }

    public String getUserId() {
        return getObjectId();
    }

    @Override
    public int compareTo(UserInfo userInfo) {
        if(gameRole.getScore()> userInfo.getGameRole().getScore())
            return 1;
        else
            return -1;

    }
}
