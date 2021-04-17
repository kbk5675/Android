package com.ata.rfiddemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1000;
    private static final String[] permissionList = new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 권한 확인
        checkPermission();
    }

    private void checkPermission() {
        ArrayList<String> permissions = new ArrayList<String>();

        int grantResult = 0;

        for (String permission : permissionList) {
            grantResult = ContextCompat.checkSelfPermission(this, permission);

            if (grantResult == PackageManager.PERMISSION_DENIED) {
                permissions.add(permission);
            }
        }

        if (permissions.size() != 0) {
            String[] requestPermissions = new String[permissions.size()];
            ActivityCompat.requestPermissions(this, permissions.toArray(requestPermissions), REQUEST_PERMISSION_CODE);
        } else {
            startMain();
        }
    }

    private void startMain() {
        SharedPreferences sharedpreferences = getSharedPreferences("session", Context.MODE_PRIVATE);

        // 언어 설정 확인
        String language = sharedpreferences.getString("language", "EN");

        if (language != null) {
            Locale mLocale = new Locale(language);
            Configuration config = new Configuration();
            config.setLocale(mLocale);
            getResources().updateConfiguration(config, null);
        }

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 요청 수락
                    startMain();
                } else {
                    // 거부 되었을 경우 권한 재요청
                    checkPermission();
                }
        }
    }
}
