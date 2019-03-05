package com.dianping.pipeline;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.dianping.pipeline.tools.PipelineCrashUncaughtExceptionHandler;

public class PipelineProjectApplication extends Application {
    private static final String TAG = "PipelineProjectApplicat";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Application onCreate");
        SDKInitializer.initialize(this);
        MultiDex.install(this);

        //自定义CrashHandler
        PipelineCrashUncaughtExceptionHandler exceptionHandler = PipelineCrashUncaughtExceptionHandler.getsInstance();
        exceptionHandler.init(this);
    }
}
