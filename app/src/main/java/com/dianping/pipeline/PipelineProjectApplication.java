package com.dianping.pipeline;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.baidu.mapapi.SDKInitializer;
import com.dianping.pipeline.tools.PipelineCrashUncaughtExceptionHandler;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class PipelineProjectApplication extends Application {
    private static final String TAG = "PipelineProjectApplicat";
    private RefWatcher mRefWatcher;
    private Context context;
    private static PipelineProjectApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        Log.d(TAG, "Application onCreate");
        SDKInitializer.initialize(this);
        MultiDex.install(this);

        //自定义CrashHandler
        PipelineCrashUncaughtExceptionHandler exceptionHandler = PipelineCrashUncaughtExceptionHandler.getsInstance();
        exceptionHandler.init(this);

        if (LeakCanary.isInAnalyzerProcess(context)) {
            return;
        }
        mRefWatcher = LeakCanary.install(this);
    }

    public static PipelineProjectApplication instance() {
        if (instance == null) {
            throw new IllegalStateException("Application has not been created");
        }

        return instance;
    }}
