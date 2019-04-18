package com.jack.applockscreen.util;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.jack.applockscreen.activity.LockScreenActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Parser {


    public static String getError(String jsonString) {
        String error = null;
        try {
            JSONObject obj = new JSONObject(jsonString);
            error = obj.optString("error");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return error;
    }

    public static String getErrno(String jsonString) {
        String error = null;
        try {
            JSONObject obj = new JSONObject(jsonString);
            error = obj.optString("errno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return error;
    }


    public static boolean isNetworkAvailable(Context context) {
        if (null == context) {
            return false;
        }
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static final List<LockScreenActivity> KEY_GUARD_INSTANCES = new ArrayList<LockScreenActivity>();
    public static int sPhoneCallState = TelephonyManager.CALL_STATE_IDLE;

    /**
     * 用于杀掉锁屏
     */
    public static final void killBackgroundProcess(Context context) {
        if (context != null) {
            try {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                am.killBackgroundProcesses("com.huaqian");
                am.killBackgroundProcesses("com.huaqian:remote");
                am.killBackgroundProcesses("com.huaqian:bdservice_v1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
