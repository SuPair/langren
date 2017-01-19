package com.jinhanyu.jack.langren;

/**
 * Created by anzhuo on 2016/10/8.
 */
public class Constants {

    public static String makeNewIpAddress(String newServerHost){
          return "http://"+newServerHost+":3000";
    }

    public static final String ServerHost = "118.89.55.172";

    /**
     * 设置各个分段的时间（发言计时、投票计时、发动技能计时、等等）
     */
    public static final int SPEAK_SECONDS  = 40;
    public static final int WOLF_SECONDS   = 15;
    public static final int WIZARD_SECONDS = 15;
    public static final int HUNTER_SECONDS = 10;
    public static final int POLICE_SECONDS = 10;
    public static final int VOTE_SECONDS   = 15;


}
