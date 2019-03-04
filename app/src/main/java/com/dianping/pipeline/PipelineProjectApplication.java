package com.dianping.pipeline;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;

public class PipelineProjectApplication extends Application {
    private static final String TAG = "PipelineProjectApplicat";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate");
        SDKInitializer.initialize(this);
        MultiDex.install(this);
    }
}
