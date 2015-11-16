package com.ljmob.lovereadingphone.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Created by london on 15/11/16.
 * 权限检查器
 */
public class PermissionUtil {
    public static final int REQUEST_PERMISSIONS = 0x52;
    private static Context context;
    private static String[] permissions;

    public static void init(Context context, String[] permissions) {
        PermissionUtil.context = context;
        PermissionUtil.permissions = permissions;
    }

    public static boolean isAllPermissionAllowed() {
        for (String p : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(context, p);
            boolean isAllowed = permissionCheck == PackageManager.PERMISSION_GRANTED;
            if (!isAllowed) {
                return false;
            }
        }
        return true;
    }

    public static void request(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                permissions,
                REQUEST_PERMISSIONS);
    }

    public static void request(Fragment fragment) {
        request(fragment.getActivity());
    }
}
