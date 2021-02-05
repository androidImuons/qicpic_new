package com.saqcess.qicpic.application;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.multidex.BuildConfig;
import androidx.multidex.MultiDexApplication;



import java.lang.reflect.Field;



/**
 * @author Manish Kumar
 * @since 28/9/18
 */

public abstract class BaseApplication extends MultiDexApplication{

    public static BaseApplication instance;



    @Override
    public void onCreate () {
        super.onCreate();
        instance = this;


    }

    public boolean isDebugBuild () {

        try {
            String packageName = getPackageName();

            Bundle bundle = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA).metaData;
            String manifest_pkg = null;
            if (bundle != null) {
                manifest_pkg = bundle.getString("manifest_pkg", null);
            }
            if (manifest_pkg != null) {
                packageName = manifest_pkg;
            }
            final Class<?> buildConfig = Class.forName(packageName + ".BuildConfig");
            final Field DEBUG = buildConfig.getField("DEBUG");
            DEBUG.setAccessible(true);
            return DEBUG.getBoolean(null);
        } catch (final Throwable t) {
            final String message = t.getMessage();
            if (message != null && message.contains("BuildConfig")) {
                return false;
            } else {
                return BuildConfig.DEBUG;
            }
        }
    }

}
