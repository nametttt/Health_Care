package com.tanya.health_care.code;

import android.content.Context;
import android.os.AsyncTask;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;

import org.apache.commons.logging.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class YaGPTAPI {

    private static final String[] SCOPES = { "https://www.googleapis.com/auth/firebase.messaging" };
    private GoogleCredentials googleCredentials;
    private InputStream jasonfile;
    private String beaerertoken;
    private String BEARERTOKEN;

    public void send(String text, Context context, ResponseCallback callback) throws IOException {

        jasonfile = context.getResources().openRawResource(context.getResources().getIdentifier("serviceaccount", "raw", context.getPackageName()));

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    googleCredentials = GoogleCredentials
                            .fromStream(jasonfile)
                            .createScoped(Arrays.asList(SCOPES));

                    googleCredentials.refreshAccessToken().getTokenValue();

                    beaerertoken = "y0_AgAAAAA8M1WcAATuwQAAAAEE1-eFAAC3AceNKadC7qc0fRbzbYfZaXA7og";
                    BEARERTOKEN = "y0_AgAAAAA8M1WcAATuwQAAAAEE1-eFAAC3AceNKadC7qc0fRbzbYfZaXA7og";

                    OkHttpClient client = new OkHttpClient();

                    Map<String, Object> mapa = new HashMap<>();
                    mapa.put("stream", true);
                    mapa.put("temperature", "0.3");
                    mapa.put("maxTokens", "100");

                    Map<String, Object> form = new HashMap<>();
                    form.put("role", "user");
                    form.put("text", text);

                    Map<String, Object> json = new HashMap<>();

                    RequestBody requestBody = RequestBody.create(MediaType.get("application/json"), json.toString());

                    // Request to get credentials
                    Request request = new Request.Builder()
                            .url("https://llm.api.cloud.yandex.net/foundationModels/v1/completion")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();

                    // Constructing message body
                    Map<String, Object> body = new HashMap<>();

                    Map<String, String> headers = new HashMap<>();

                    ArrayList<Map<String, Object>> da = new ArrayList<>();
                    da.add(form);

                    headers.put("Authorization", "Bearer " + "t1.9euelZqMk5OXmMmJkI7Kj5eQkZbImO3rnpWay4yXm52Rk5rNjo3Pz5TIlovl8_cvJHlM-e8YfGR9_t3z929Sdkz57xh8ZH3-zef1656VmszHyY7NnsqJkYqenoyRzsjP7_zF656VmszHyY7NnsqJkYqenoyRzsjP.n45MJ8uizvvKVZ8M3oMLzgnMvHzd8Ah4H0esQJX54rJq8PbexbiZHepHVlHJAi7t1gGhQSRN2Zdyo3fNgQYsCA");
                    body.put("modelUri", "gpt://b1gpel67poamsv8n7e04/yandexgpt/latest");
                    body.put("completionOptions", mapa);

                    body.put("messages", da);

                    okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder().url("https://llm.api.cloud.yandex.net/foundationModels/v1/completion");
                    for (Map.Entry<String, String> entry : headers.entrySet()) {
                        requestBuilder.addHeader(entry.getKey(), entry.getValue());
                    }
                    requestBuilder.post(RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(body)));
                    Response sendMessageResponse = client.newCall(requestBuilder.build()).execute();

                    if (sendMessageResponse.isSuccessful()) {
                        String responses = sendMessageResponse.body().string();
                        callback.onResponseReceived(responses);
                    } else {
                        callback.onResponseReceived("Error sending message: " + sendMessageResponse.body().string());
                    }
                } catch (IOException e) {
                    callback.onResponseReceived("Error: " + e.getMessage());
                }
            }
        }).start();
    }

    public interface ResponseCallback {
        void onResponseReceived(String response);
    }
}