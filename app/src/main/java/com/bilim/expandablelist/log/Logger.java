package com.bilim.expandablelist.log;

import android.util.Log;

/**
 * Created by zhangdi on 12/6/18.
 */
public class Logger {

    private static final boolean DEBUG = true;

    public static void e(String tag, String msg) {
        if (DEBUG)
            Log.e(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (DEBUG)
            Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (DEBUG)
            Log.i(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (DEBUG)
            Log.v(tag, msg);
    }
}
