package com.dianping.pipeline.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;


public class ProjectUtils {

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String getDeviceIMEI(Context context) {
        String imei = "";
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);

        if (telephonyManager.getPhoneCount() == 1) {
            imei = telephonyManager.getDeviceId();
        } else if (telephonyManager.getPhoneCount() > 1) {
            imei = telephonyManager.getDeviceId(0);
        }

        return imei;
    }
}
