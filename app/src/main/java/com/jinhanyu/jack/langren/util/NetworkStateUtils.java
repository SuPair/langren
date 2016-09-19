package com.jinhanyu.jack.langren.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by anzhuo on 2016/8/10.
 */
public class NetworkStateUtils {
    public static boolean isConnected(Context context){
         return  isWifiConnected(context)|| isMobileConnected(context);
    }

    public static boolean isWifiConnected(Context context){
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return  wifiNetInfo.isConnected();
    }

    public static boolean isMobileConnected(Context context){
        ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return mobNetInfo.isConnected();
    }
}
