package com.BreakthroughGames.OrigamiWars.utils;

import android.util.Log;

public class Logger {
    private int iLogLevel = 3;
    private String LOG_TAG = "";

    public Logger(String sLogTag) { LOG_TAG = sLogTag;}
    public Logger() { LOG_TAG = setInitClassName(); }

    public void setLogTag(String sTag) { LOG_TAG = sTag; }
    public String getLogTag() { return LOG_TAG; }

    public int getLogLevel() { return this.iLogLevel; }
    public void setLogLevel(int iLevel){ this.iLogLevel = iLevel; }

    // METHOD for logging
    public void d(String sLog){ d("", sLog); }
    public void e(String sLog){ e("", sLog); }
    public void w(String sLog){ w("", sLog); }
    public void d(String filter, String sLog) { Log.d(LOG_TAG + filter, sLog);  }
    public void e(String filter, String sLog){ Log.e(LOG_TAG + filter, sLog);  }
    public void w(String filter, String sLog){ Log.w(LOG_TAG + filter, sLog); }

    private String setInitClassName(){
        try {
            String sCallingClass = Thread.currentThread().getStackTrace()[3].getClassName();
            String sSplitClass[] = sCallingClass.split("\\.");
            return  sSplitClass[sSplitClass.length - 1];
        } catch (NullPointerException e){}
        return "";
    }


}
