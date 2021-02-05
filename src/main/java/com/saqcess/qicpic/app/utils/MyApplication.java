package com.saqcess.qicpic.app.utils;

import android.app.Application;

import com.saqcess.qicpic.application.BaseApplication;

public class MyApplication extends BaseApplication {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}