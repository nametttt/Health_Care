package com.tanya.health_care.code;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;

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

    private static final String[] SCOPES = {"https://www.googleapis.com/auth/firebase.messaging"};
    private GoogleCredentials googleCredentials;
    private InputStream jasonfile;
    private String beaerertoken;
    private String BEARERTOKEN;

    public void send(ArrayList<String> token, String text, Context context, CompletionCallback callback) {
        jasonfile = context.getResources().openRawResource(context.getResources().getIdentifier("serviceaccount", "raw", context.getPackageName()));

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
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

                    ArrayList<Map<String, Object>> da = new ArrayList<>();
                    da.add(form);

                    Map<String, Object> body = new HashMap<>();
                    body.put("modelUri", "gpt://b1gpel67poamsv8n7e04/yandexgpt/latest");
                    body.put("completionOptions", mapa);
                    body.put("messages", da);

                    RequestBody requestBody = RequestBody.create(MediaType.get("application/json"), new Gson().toJson(body));

                    // Request to get credentials
                    Request request = new Request.Builder()
                            .url("https://llm.api.cloud.yandex.net/foundationModels/v1/completion")
                            .post(requestBody)
                            .addHeader("Authorization", "Bearer " + "YOUR_ACCESS_TOKEN") // Add your access token here
                            .build();

                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        callback.onComplete(responseBody);
                    } else {
                        callback.onError(new Exception("Error sending request: " + response.code() + " " + response.message()));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onError(e);
                }
                return null;
            }
        }.execute();
    }

    // Interface for completion callback
    public interface CompletionCallback {
        void onComplete(String result);
        void onError(Exception e);
    }
}
