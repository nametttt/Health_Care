package com.tanya.health_care.code;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.*;

public class FirebaseMessaging {

    public static void send(final String deviceToken, final String name, final String title, final String notificationBody) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();

                try {
                    Map<String, Object> json = new HashMap<>();
                    json.put("type", "service_account");
                    json.put("project_id", "health-care-16fe6");
                    json.put("private_key_id", "231ec32237c0d66c645c29a541185600367f5d3d");
                    json.put("private_key", "-----BEGIN PRIVATE KEY-----\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCverzEa2b45Y0d\nbqFlyB0/8s8IDjGz/Thq8UETC8bS0/Oe0nWjrTcL68U1py/HstVfznPHCtz1wEbm\nQ1KlGsA/xHRd7nuWT7cyuTxrFSAwGYomG8ixOqVRhdnTHho+jwSQXFN/J1So2Qnp\no/8DPIVQE4qt5spMejgmE0yMcgsGMF3ZZmVT05OeyY81LDtqW0dxofbh0C4ReLEV\noNm9QKwb/jcy6pIhKfQsOfutXPJMa4MNjCMwPepzxTPBOnGIXs43ztwnK2MRBubo\nur0r2rLDctCMS4dci0C4+DfzXha3UAp8EVaYr2bOgGLRxoxN06pYnPSdmGNC7346\nZnM0KnJfAgMBAAECggEAHNP2l/QnBgO8I5YtqKlv+LX8gg/G5ZFXh+YbcTQRlUgh\nsTlU/y8xKGGy08YkCNQdzq/9gDkOJMdSPnjX5mzXIYK2NUo8/mwZJhmdTeA6GfzQ\nH8OXvFnsl9B8AP1khVt8ffTU+MIu29BQGxSIi/EQ259iPIB6Qocfm023WSlRG1Jo\nJ43WnNVR7I4DU4HJPmiHQif1Zs/8GkdmNJ4erpNyZRR7QmpBFa2blY95jGn4aDjs\n75LCCEgiR2TMP+iM41+cuHhWT9943osUGjFCswFbPEQXiI3Gbd9d5p5+RFQQ5ez/\n0Yq2aRum9u6sTrQmIXHXWtc2wOKt2A3lqDHb2RwCSQKBgQDZJM5/oP7BbFrIRkkb\n1acLVio85PYXAB81bYYcuJ9CnmPskS0wlNXpJ3TdKwlVPTpn5q1065nQyei1gSIc\n2Szk8bW0YK+8lwx0RRoxwyETTx2Qycu8hIsQCo0NKN695WgazaaPl47f0YasWKLI\nu0PJlCIjqdHJuAjFtlZGYzcX1wKBgQDO4VGkjl7FkvuK3u4LC17w6yibC/oh20F6\n5Td3XoevgLJbrsT9Ziw7vBGrCVixurDSqpGOWJAEzFMsyK1xrspt8zh2rQK2XxA/\njwoDFisebFL4oEOiw2oQju2Kqnwt3bzbinP04yRmu2fkTqN2+KCfQZtjm/ecjoiR\nozYWUtCIuQKBgACNpnUOzPdjNkwCWddXUQinXI4xKytd1baGI+xY0BcUXj7RE+Nm\nzbC9Z//URz7PXCG9pb2BiC/PA+fKf3LBXurqbsMZ5/i8Dc0SDAIKdEAfWHzw9rLx\nR4Vv8XISI5cqSXyf/4eoJDYPy58OzsNiLscJuJiMqzsvW95dG5UsVYybAoGBAMpX\nQIYFJZZyWwqJXiN5PKPALNfwz/X7P8EWnbhnwaga5NedSVhabVsDZ/h0TMaQRU/W\nukSSRFIdEiDUKiftgFonsAsKzoNW2UqA+ZbpQMUhx8ka3uw72upVEOo2MvydSpEj\niqRy4PCFzWhUrba4GEmLXpL/QiBinBptmoIc/48pAoGBAMind030AbK69YieBDvH\n9UW0Zh2BbDSzeUfzEgCaVsHhrwf4PE34AonOOvDDil+ZuVKAJPO7dHe7CgdYC+TI\nrYydud38KMwA360txGbxRW2ihK7iQOy0hKFXDqmln/xmtaV9883oxZmJ/ZzurciO\nsIs4wB9sWgvBo6MTpjXiQdYZ\n-----END PRIVATE KEY-----\n");
                    json.put("client_email", "firebase-adminsdk-cr2l6@health-care-16fe6.iam.gserviceaccount.com");
                    json.put("client_id", "102825907373396888813");
                    json.put("auth_uri", "https://accounts.google.com/o/oauth2/auth");
                    json.put("token_uri", "https://oauth2.googleapis.com/token");
                    json.put("auth_provider_x509_cert_url", "https://www.googleapis.com/oauth2/v1/certs");
                    json.put("client_x509_cert_url", "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-cr2l6%40health-care-16fe6.iam.gserviceaccount.com");
                    json.put("universe_domain", "googleapis.com");

            String scope = "https://www.googleapis.com/auth/firebase.messaging";

            RequestBody requestBody = RequestBody.create(MediaType.get("application/json"), json.toString());

            // Request to get credentials
            Request request = new Request.Builder()
                    .url("https://oauth2.googleapis.com/token")
                    .post(requestBody)
                    .build();

            Response response = client.newCall(request).execute();

            String accessToken = response.body().string().replace("\n", "");


            // Constructing message body
            Map<String, Object> body = new HashMap<>();
            Map<String, Object> message = new HashMap<>();
            Map<String, Object> notification = new HashMap<>();
            Map<String, Object> data = new HashMap<>();

            notification.put("title", title);
            notification.put("body", notificationBody);

            data.put("callerName", name);

            message.put("token", deviceToken);
            message.put("notification", notification);
            message.put("data", data);

            body.put("message", message);

            // Constructing request URL
            String projectId = "health-care-16fe6";
            String url = "https://fcm.googleapis.com/v1/projects/" + projectId + "/messages:send";

            // Constructing headers
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", "Bearer " + accessToken);

            // Making request to send message
            okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder().url(url);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
            requestBuilder.post(RequestBody.create(MediaType.parse("application/json"), new Gson().toJson(body)));
            Response sendMessageResponse = client.newCall(requestBuilder.build()).execute();

            if (sendMessageResponse.isSuccessful()) {
                System.out.println("Message sent: " + sendMessageResponse.body().string());
            } else {
                System.out.println("Error sending message: " + sendMessageResponse.body().string());
            }


        } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }
}
