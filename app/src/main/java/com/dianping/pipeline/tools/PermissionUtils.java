package com.dianping.pipeline.tools;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by songjunmin on 2019/1/14.
 */

public class PermissionUtils {
    private Activity mContext;
    public PermissionUtils(Activity activity){
        mContext = activity;
    }

    public void checkPermisson(){
        List<String> permissionList=new ArrayList<>();
        if (ContextCompat.checkSelfPermission(mContext, Manifest.
                permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(mContext, Manifest.
                permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(mContext, Manifest.
                permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(mContext, Manifest.
                permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(mContext,permissions,1);
        }else {
            Toast.makeText(mContext,"未授予任何权限",Toast.LENGTH_SHORT).show();
        }
    }
}
