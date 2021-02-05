package com.saqcess.qicpic.app.utils;

public class Contrants {

    public static int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION=0101;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 0102;
    public static int REQUEST_CAMERA_PERMISSION=0103;

    public static String getValidString (String data) {
        return data == null ? "" : data;
    }
}
