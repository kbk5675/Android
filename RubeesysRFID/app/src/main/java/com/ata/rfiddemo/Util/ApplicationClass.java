package com.ata.rfiddemo.Util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wsmr.lib.dev.ATRfidManager;
import com.wsmr.lib.dev.ATRfidReader;
import com.wsmr.lib.dev.ATScanManager;
import com.wsmr.lib.dev.ATScanner;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class ApplicationClass extends Application implements Application.ActivityLifecycleCallbacks {

    private static boolean isBackground;
    private static ATRfidReader mReader;
    private static ATScanner mScanner;

    private static boolean asciiFlag = false;

    public static boolean getAsciiFlag() {
        return asciiFlag;
    }

    public static void setAsciiFlag(boolean asciiFlag) {
        ApplicationClass.asciiFlag = asciiFlag;
    }



    @Override
    public void onCreate() {
        super.onCreate();

        isBackground = true;
        mReader      = ATRfidManager.getInstance();
        mScanner     = ATScanManager.getInstance();

        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(0)
                .build();

        Realm.setDefaultConfiguration(config);

        // 앱이 foreground로 돌아오는 것을 감지(Activity LifeCycle 체크)
        registerActivityLifecycleCallbacks(this);


    }





    // 앱이 background일 때 감지
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            isBackground = true;
            ATRfidManager.sleep();
            ATScanManager.sleep();
        }
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        if (isBackground) {
            isBackground = false;
            ATRfidManager.wakeUp();
            ATScanManager.wakeUp();
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    public static ATRfidReader getmReader() {
        return mReader;
    }

    public static String getRfidVersion() {
        return  ATRfidManager.getVersion();
    }

    public static ATScanner getmScanner() {
        return mScanner;
    }
}
