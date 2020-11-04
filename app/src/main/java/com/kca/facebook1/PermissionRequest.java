package com.kca.facebook1;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class PermissionRequest {

    public static boolean checkStore(Activity activity, int requestCode){
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(activity,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
            Toast.makeText(activity, R.string.permission_denied,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
