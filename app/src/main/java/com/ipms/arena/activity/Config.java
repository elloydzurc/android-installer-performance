package com.ipms.arena.activity;

import android.content.res.Resources;

/**
 * Created by t-ebcruz on 9/9/2016.
 */
public class Config
{    // Web Service
    public static final String WS = "https://pldtqa.pldt.com.ph/ipmsmobileapp/MobileAppWS.asmx?wsdl";
    public static final String WS_KEYWORD = "P@ssword";
    public static final int TIMEOUT = 15000;

    // Update Checker
    static final String UPDATE_URL = "http://hype.ph/android/";
    static final String APK_FILE = "app-debug.apk";
    static final String VERSION_FILE = "version.txt";

    static final String UPDATE_YES = "DOWNLOAD UPDATE";
    static final String UPDATE_NO = "CANCEL";

    // Value in DP
    public static final int ITEM_HEIGHT = 112;
    public static final int MAP_CHART_HEIGHT = 350;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}