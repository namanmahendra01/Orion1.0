package com.orion.orion.util;

import android.Manifest;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class Permissions  {

    public static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA


    };
    public static final String[] CAMERA_PERMISSION = {
            Manifest.permission.CAMERA

    };
    public static final String[] WRITE_STORAGE_PERMISSION = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };
    public static final String[] READ_STORAGE_PERMISSION = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
}
