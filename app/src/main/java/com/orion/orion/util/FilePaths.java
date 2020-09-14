package com.orion.orion.util;

import android.os.Environment;

public class FilePaths {
    public String ROOT_DIR= Environment.getExternalStorageDirectory().getPath();

             public  String PICTURS =ROOT_DIR +"/Pictures";
            public String CAMERA =ROOT_DIR+ "/DCIM/camera";

            public String FIREBASE_IMAGE_STORAGE ="photos/users/";
    public String FIREBASE_CONTEST_STORAGE ="Contests/users/";
}
