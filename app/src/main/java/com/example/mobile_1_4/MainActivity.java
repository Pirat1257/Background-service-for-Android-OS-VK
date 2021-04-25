package com.example.mobile_1_4;

import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;
import com.vk.sdk.util.VKUtil;

public class MainActivity extends Activity {

    private Button start_button;
    private Button stop_button;
    private String[] scope = new String[] {VKScope.WALL};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);
        // Получение отпечатка с помощью SDK
        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
        // Привязываем кнопки
        start_button = findViewById(R.id.start_button);
        stop_button = findViewById(R.id.stop_button);
        // Авторизация пользователя
        VKSdk.login(this, scope);
        // Действие при нажатии на кнопку start
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Запуск сервиса
                /*
                    Запущенная служба будет выполняться независимо от состояния активности, несмотря на то, что эти
                    компоненты находятся в одном приложении: если её завершить, служба все равно останется работать.
                */
                startService(new Intent(MainActivity.this, MyService.class));
            }
        });
        // Действие при нажатии на кнопку stop
        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Остановка сервиса
                stopService(new Intent(MainActivity.this, MyService.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // User passed Authorization
            }
            @Override
            public void onError(VKError error) {
                // User didn't pass Authorization
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}