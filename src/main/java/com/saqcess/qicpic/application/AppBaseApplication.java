package com.saqcess.qicpic.application;

/**
 * @author Manish Kumar
 * @since 29/9/18
 */

public abstract class AppBaseApplication extends BaseApplication {

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1100;
    public String currency_symbol = "";
    String deviceId;

    public static AppBaseApplication getInstance() {
        return (AppBaseApplication) instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
