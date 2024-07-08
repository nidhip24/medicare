package com.og.medicare.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class APIUtil {

    public static final String BASE_URL = "https://medicare-2be02be1cccf.herokuapp.com/api";

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    static OkHttpClient client = getOkHttpClient();

    // okhttp client
    public static OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public static Response addOrder(JSONObject inventoryJson) {
        RequestBody body = RequestBody.create(String.valueOf(inventoryJson), JSON);
        return callAPI(BASE_URL + "/order", body);
    }

    public static Response addInventory(JSONObject inventoryJson) {
        RequestBody body = RequestBody.create(String.valueOf(inventoryJson), JSON);
        return callAPI(BASE_URL + "/inventory", body);
    }

    public static Response addDistribution(JSONObject obj) {
        RequestBody body = RequestBody.create(String.valueOf(obj), JSON);
        return callAPI(BASE_URL + "/distribution", body);
    }

    private static Response callAPI(String url, RequestBody body) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Response getInventory() {
        return callGetAPI(BASE_URL + "/inventory");
    }

    public static Response getDistribution() {
        return callGetAPI(BASE_URL + "/distribution");
    }

    private static Response callGetAPI(String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
