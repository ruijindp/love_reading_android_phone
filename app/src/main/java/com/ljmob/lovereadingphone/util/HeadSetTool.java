package com.ljmob.lovereadingphone.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by london on 15/11/11.
 * 耳机工具箱
 */
public class HeadSetTool {
    public static boolean isHeadSetConnected(Context context) {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        Intent iStatus = context.registerReceiver(null, iFilter);
        if (iStatus == null) {
            return false;
        }
        return iStatus.getIntExtra("state", 0) == 1;
    }
}
