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
//    public void send(String deviceToken, String callID, String name) throws IOException, URISyntaxException {
//        OkHttpClient client = new OkHttpClient();
//
//        // Constructing JSON payload
//        Map<String, Object> json = new HashMap<>();
//        json.put("type", "service_account");
//        json.put("project_id", "federalschool-47496");
//        json.put("private_key_id", "867541a2568b9991a45f8044db8881d4808335f9");
//        json.put("private_key", "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDFddGDj/OqT/hH\nYABiWk9Cf26VRXHLJFXtU0HGOfep2mBf2rE7xEoDGOTCHzEyoXiOxpdGW7gz8rQB\n1K0fviiABsyAM5/2iuua8RsWZtF1m91QDcec+XylpWGTNMW+v/kydofQhH1CeAOj\nByF+8Zcsj1aIXkF20zdCsmjXS+cCV5oIs7eLKwkb6edFlXffxcFPqOKbnQObuoaW\nb/rHXqT7SQlp/iRPq1NNe2JSXGhuQEyx1LJt8yvJ9B5G/LMrJx8P3/NtO8DeBBy0\nvkvDiyWL31F6TzBHClAzeMFtR1uV/4NotEax9GCkN5lepYyESsBdlxnAC4F29kls\nq7ZHlcivAgMBAAECggEASVcEzeYqHQBd2XjFO8KKPMsi8gtchBSHW8H4JJ2EKNCk\nygVqnaW+zZ6x3I4EUmdZ5UHKjwXjCVxkUmfBM5CH72FXFGjYSZR4hNB3fJ8Mvi95\niFHN6bZafxXJg4jux3X7IyyWLjL/aTA1PZiY7tLhkNneTIEhtHYnyLyGJy0YbKwg\nrMnjh1M3gO39Lj+286WBwFsESDnBXQCcdKgccDFlsy1AKuG8ZJiAmm7vAq/7EAF6\nzAXdd57AzfIWMCMShCMQAxtdUWZmQR1w4s2q1mRzRplCIQaJDBjWnS4H486F11z8\nbPWRCCMmKdnXH/q+gZD1iuQubAFQOZb7bo5j0iKhuQKBgQDqvN40MBxrC047qdyl\nqo1yIvB9aQ4VYw+TdATzPTTils7IHMJCwD8FfMR44gS5I+uWRLdcDRbHAkQ7a1HY\n0Wl0dQN3okLKNh1rFWtiSBcwuhDSRBwTHkEoJlmpIilW9kpzBymrm2KxHSoKURlL\nl1VyzebI8Di2P7/7epxxJ7jzJwKBgQDXWI2/NK7kshTJ1kLCnizLUatExAYpOsmH\nRjurmhvQnbDO+Z02lOND30OwIV10uKIJKeCqMvu9jVWjYrFRvM90Uyb7Tb0kHZLy\nYjLKpU1qOcxtjPf1zaF2h3AoyoQdn77zw8lG3aDHmILcUW5sGD8i9Vq5keLfNaSV\n8Hvldh5TOQKBgCPdOWHl2+Gq94f8Gt8g4L2Igw/WJjW0TePsfPkg59yax/shEbkb\nIEXZWzdQ6QHUcCEkXJNu2IUNXplpezbSP/dwDViQ7P1yKSp8Okzo0Mo8E2fcyiFN\npQzaVyaVNpW3yYYrmP1EH18KIqsy2teGxqJkvRcERNXrhYyJni9Xr1VPAoGAU5lb\nHdlz8/B2RYzaSfdh6GSCGqYGxka+KbfIPmwLVEeDdjZNI/1U5OptupiZUVDEBs6t\nGyXDuOh/UHhl4hdsafpF7dVWEgkxHMumcCkQDqb1h6nsMQ5tGjimAA/ujhmP5c4h\n+1LaseGxG5q5RVl8WTPqzpOmAYUvqc28K25Zg3ECgYAyHkmNR4DMZv/yBAA+QuBM\naNlH2LhsTPmZo5QHWgimTJw15tm1xfbXn8PI79PTsX18eRCeduyOMOajJIrfAliP\nZgzOJwFzySdwSckagmI4yxlJpSu7ymZM6ry1o3P18qb5vgA1buy0eUjxGgYd8scG\nI9wsPuv7VrllifjN7dusMA==\n-----END PRIVATE KEY-----\n");
//        json.put("client_email", "firebase-adminsdk-4ry8l@federalschool-47496.iam.gserviceaccount.com");
//        json.put("client_id", "107470453864219028120");
//        json.put("auth_uri", "https://accounts.google.com/o/oauth2/auth");
//        json.put("token_uri", "https://oauth2.googleapis.com/token");
//        json.put("auth_provider_x509_cert_url", "https://www.googleapis.com/oauth2/v1/certs");
//        json.put("client_x509_cert_url", "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-4ry8l%40federalschool-47496.iam.gserviceaccount.com");
//        json.put("universe_domain", "googleapis.com");
//
//        String scope = "https://www.googleapis.com/auth/firebase.messaging";
//
//        RequestBody requestBody = RequestBody.create(MediaType.get("application/json"), json.toString());
//
//        // Request to get credentials
//        Request request = new Request.Builder()
//                .url("https://www.googleapis.com/auth/firebase.messaging")
//                .post(requestBody)
//                .build();
//
//        Response response = client.newCall(request).execute();
//
//        // Get access token
//        String accessToken = response.body().string();
//
//        // Constructing message body
//        Map<String, Object> body = new HashMap<>();
//        Map<String, Object> message = new HashMap<>();
//        Map<String, Object> notification = new HashMap<>();
//        Map<String, Object> data = new HashMap<>();
//
//        notification.put("title", "");
//        notification.put("body", "");
//
//        data.put("call_id", callID);
//        data.put("callerName", name);
//
//        message.put("token", deviceToken);
//        message.put("notification", notification);
//        message.put("data", data);
//
//        body.put("message", message);
//
//        // Constructing request URL
//        String projectId = "federalschool-47496";
//        String url = "https://fcm.googleapis.com/v1/projects/" + projectId + "/messages:send";
//
//        // Constructing headers
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Authorization", "Bearer " + accessToken);
//
//        // Making request to send message
//        okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder().url(url);
//        for (Map.Entry<String, String> entry : headers.entrySet()) {
//            requestBuilder.addHeader(entry.getKey(), entry.getValue());
//        }
//        requestBuilder.post(RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(body)));
//        Response sendMessageResponse = client.newCall(requestBuilder.build()).execute();
//
//        if (sendMessageResponse.isSuccessful()) {
//            System.out.println("Message sent: " + sendMessageResponse.body().string());
//        } else {
//            System.out.println("Error sending message: " + sendMessageResponse.body().string());
//        }
//    }
//
//    public static void main(String[] args) throws IOException, URISyntaxException {
//        FirebaseMessaging firebaseMessaging = new FirebaseMessaging();
//        firebaseMessaging.send("deviceToken", "callID", "name");
//    }

}

