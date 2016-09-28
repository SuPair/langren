package com.jinhanyu.jack.langren;

import com.jinhanyu.jack.langren.entity.UserInfo;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

/**
 * Created by anzhuo on 2016/9/28.
 */
public class Me {

    public static void update(){
        try {
            get().fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public static ParseUser get(){
        return ParseUser.getCurrentUser();
    }

    public static String getNickname() {
        return get().getString("nickname");
    }

    public static String getTitle() {
        return get().getString("title");
    }

    public static String getUsername() {
        return get().getString("username");
    }

    public static int getScore() {
        return get().getInt("score");
    }

    public static String getHead() {
        ParseFile parseFile = get().getParseFile("head");
        if(parseFile==null)
            return UserInfo.default_head;
        return parseFile.getUrl();
    }

    public static String getUserId() {
        return get().getObjectId();
    }
}
