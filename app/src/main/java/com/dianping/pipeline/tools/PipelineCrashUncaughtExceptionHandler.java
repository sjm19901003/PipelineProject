package com.dianping.pipeline.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by songjunmin on 2019/3/5.
 */

public class PipelineCrashUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "PipelineCrash";

    private Context mContext;
    private static final String FILE_NAME = "crash";
    private static final String FILE_NAME_SUFFIX = ".trace";
    private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;
    private static final String PATH = Environment.getDownloadCacheDirectory().getAbsolutePath() + "/crashHandler/";
    private static final PipelineCrashUncaughtExceptionHandler sInstance = new PipelineCrashUncaughtExceptionHandler();

    private PipelineCrashUncaughtExceptionHandler() {
    }

    public static PipelineCrashUncaughtExceptionHandler getsInstance() {
        return sInstance;
    }

    public void init(Context context) {
        mDefaultExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    //捕获程序运行当中的crash，保存到本地txt
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            dumpExceptionToSDCard(e);

        } catch (IOException exception) {
            exception.printStackTrace();
        }

        if (mDefaultExceptionHandler != null) {
            mDefaultExceptionHandler.uncaughtException(t, e);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    private void dumpExceptionToSDCard(Throwable e) throws IOException {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(PATH);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            long current = System.currentTimeMillis();
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
            File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
            try {
                PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
                pw.println(time);
                dumpPhoneInfo(pw);
                pw.println();
                e.printStackTrace(pw);
                pw.close();

            } catch (Exception ex) {
                Log.e(TAG, "dump crash info failed");
            }
        }
    }

    private void dumpPhoneInfo(PrintWriter printWriter) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo packageInfo = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        printWriter.print("App Version:");
        printWriter.print(packageInfo.versionName);

        printWriter.print("OS Version:");
        printWriter.print(Build.VERSION.SDK_INT);
        //制造商
        printWriter.write("Vender:");
        printWriter.print(Build.MANUFACTURER);

        //手机型号
        printWriter.write("Model:");
        printWriter.write(Build.MODEL);
    }
}
