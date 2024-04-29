package com.tanya.health_care.code;

import android.os.AsyncTask;
import android.util.Log;

public class SendMessageTask extends AsyncTask<Void, Void, Void> {
    private String deviceToken;
    private String name;
    private String title;
    private String notificationBody;

    public SendMessageTask(String deviceToken, String name, String title, String notificationBody) {
        this.deviceToken = deviceToken;
        this.name = name;
        this.title = title;
        this.notificationBody = notificationBody;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            FirebaseMessaging firebaseMessaging = new FirebaseMessaging();
            firebaseMessaging.send(deviceToken, name, title, notificationBody);
        } catch (Exception e) {
            Log.e("SendMessageTask", "Ошибка отправки уведомления: " + e.getMessage());
        }
        return null;
    }
}

