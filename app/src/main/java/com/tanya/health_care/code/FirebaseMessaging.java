package com.tanya.health_care.code;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FirebaseMessaging {
    public void send(String deviceToken, String callID, String name) throws IOException, URISyntaxException {
        OkHttpClient client = new OkHttpClient();

        // Constructing JSON payload
        Map<String, Object> json = new HashMap<>();
        json.put("type", "service_account");
        json.put("project_id", "health-care-16fe6");
        json.put("private_key_id", "51ff4f9602e05407732733291ce1825a141c15fe");
        json.put("private_key", "-----BEGIN PRIVATE KEY-----\\nMIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCeNWCRPaaAXgip\\nC8hy4UUW5yYcs28hslevZaI7hDrMvJNq93pHV1+v4rwya0nUtuLcn0303ycJ2zZj\\nKQsVCWD9NnzhjrvjAz802HZqyoRRkJzgIgBTd0piuteTe1yNuIv2UwmdhLI2D29p\\nZBrt5Fnux84Wxx7ZuCwBozXhIH/n0w3l3psZC95YUrkSB+G8T4TvsO3ROobp0Enz\\nn+cEKzIuEldLEGByU1igRiU8cabt1OkKf+f/UfwWq38GE2BeqeSlto2isXmgj13l\\nsm4+NNT7WDr+ukx6vVJJ9Me9Jk8mQhklRHeOlUzcU0kyVzTvOVTq6qta2HCYgnQr\\nV44pYNORAgMBAAECggEABE6nN6USSqXkvfd2aFxGNYKrCLaA5M22ce3GLjEiKa9L\\noyqkL1uLXR5C6et+WdZ11DRZGrzVkwDfGPGF3feAprwsR7bLWjmdJ6rqBGvJkO/r\\nOXXrhNo3UftMwbTm1uvqkZT+qsVxVaPzcE7bpdTk9umFLJ0Us7A+MHg5UA0CtnS/\\nUP3s8RUaBVPimBdTox5GFe/qrjsjzB0NMBRLSheltD+8PZ/pwVq8EyOoWCG+dM+E\\no7vkvDmWKYbwQggYj3UBqL6zLDihhqzRfBrmcz/n3hPfpfaVg6DSiHFFVMv+zlc+\\nl4djvo5jWRDrcoqzblKvf/UaIcbyXplNR3Hlqwy0/QKBgQDSC4l++aW7llXx37xD\\njCxBmUrPJVgKDfmfLUQq3ZcV0qNG4SoFZDI8ARFBSJl6hT2IgUHtlaURptzwRsSl\\neAQrIMgcw8K++J5HH+UxQVjyp+pIjCUObqiJu0cFsNfMO1lxteWeKhd12Xe4FB2z\\n60ZmJzbkWr+mPt27no5HiJRabQKBgQDA0oObK8SHfT0/Ja5DDT/kuDbmFYvAXVAd\\nEmdyfcAUexRQ5B7nMcj0d1u0GNlYMoEAcQ+2B1jao00HM3lCDJ+cNrhu8fizgo1i\\nijQIM8uwr10zEmI9Q3N9zahfysMk2wwaSRm5ukWuoPevwY16aXqCM/6o7Ft8ivmk\\nLTRv/kGnNQKBgQCjD1vNXAciaqIL5JHlqmMNBfECgM4o+BJqVRzPXjkVMvGYa+JQ\\nTymEVZvMn4yuhdNwLlA9HegInQ515apxfotV49wWII8F7EILmyI1K7LepiaF7W4z\\nFwr16ZJIQ7IzlXtpgDlioPCXh5yeJdetPh/n29eMTTrs128RYRS5SblJvQKBgQC7\\nCDcRv219ArTy2IjkjyiHoUvW99U7vQdle6GOA33aH9x/7hYvqTHENzZjRaskirBo\\njkJwDUZZpQalUn9u2rHDI6zYKKtU6/BaQbXgsuUDt8YOMOnLhO60e1Szvgh1amed\\n44QpjA3lKku2Vmb18vQa8XY1wjWnY2JsplBzxUtHdQKBgQDKXbbFu1zRTN/VG9ff\\nzAjVQvuZPRbtOR2rBRvU/zHfrLEuBto9Zm7a9KzqQBOvjp+2WjrDNhvaZTeldSRA\\nrCazbTZMPO66nZ920dG9bhiVjpPEFrn94oleq7W96dhSKsyw63dqPTonglVobNNE\\nieR9c66FO1euJeKA44ySXcpuIw==\\n-----END PRIVATE KEY-----");
        json.put("client_email", "firebase-adminsdk-cr2l6@health-care-16fe6.iam.gserviceaccount.com");
        json.put("client_id", "102825907373396888813");
        json.put("auth_uri", "https://accounts.google.com/o/oauth2/auth");
        json.put("token_uri", "https://oauth2.googleapis.com/token");
        json.put("auth_provider_x509_cert_url", "https://www.googleapis.com/oauth2/v1/certs");
        json.put("client_x509_cert_url", "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-cr2l6%40health-care-16fe6.iam.gserviceaccount.com");
        json.put("universe_domain", "googleapis.com");

        String scope = "https://www.googleapis.com/auth/firebase.messaging";

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());

        // Request to get credentials
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/auth/firebase.messaging")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();

        // Get access token
        String accessToken = response.body().string();

        // Constructing message body
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        Map<String, Object> notification = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        notification.put("title", "");
        notification.put("body", "");

        data.put("call_id", callID);
        data.put("callerName", name);

        message.put("token", deviceToken);
        message.put("notification", notification);
        message.put("data", data);

        body.put("message", message);

        // Constructing request URL
        String projectId = "federalschool-47496";
        String url = "https://fcm.googleapis.com/v1/projects/" + projectId + "/messages:send";

        // Constructing headers
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + accessToken);
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String jsonRequest = "Some request";

        RequestBody rer = RequestBody.create(JSON, jsonRequest);


        // Making request to send message
        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder().url(url).post(rer);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }
        try (Response resp = client.newCall(request).execute()) {
            if (!resp.isSuccessful()) {
                throw new IOException("Запрос к серверу не был успешен: " +
                        resp.code() + " " + resp.message());
            }
            System.out.println(resp.body().string());
        } catch (IOException e) {
            System.out.println("Ошибка подключения: " + e);
        }
    }


    public static void main(String[] args) throws IOException, URISyntaxException {
        FirebaseMessaging firebaseMessaging = new FirebaseMessaging();
        firebaseMessaging.send("deviceToken", "callID", "name");
    }

}

