package com.jinhanyu.jack.langren.util;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by anzhuo on 2016/9/19.
 */
public class ScreenUtils {

    public static int getScreenWidth(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }

    public static int getScreenHeight(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

}
