package com.og.medicare.api;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class APIUtil {

    public static final String BASE_URL = "https://medicare-2be02be1cccf.herokuapp.com/api";

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    static OkHttpClient client = getOkHttpClient();

    static String TAG = APIUtil.class.toString();

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

    public static Response generateReport(JSONObject requestJson) {
        RequestBody body = RequestBody.create(String.valueOf(requestJson), JSON);
        return callAPI(BASE_URL + "/report/expiry_report", body);
    }

    public static boolean downloadFile(Response response, File destFile) {
        try {
            ResponseBody body = response.body();
            long contentLength = body.contentLength();
            BufferedSource source = body.source();

            BufferedSink sink = Okio.buffer(Okio.sink(destFile));
            Buffer sinkBuffer = sink.buffer();

            long totalBytesRead = 0;
            int bufferSize = 8 * 1024;
            for (long bytesRead; (bytesRead = source.read(sinkBuffer, bufferSize)) != -1; ) {
                sink.emit();
                totalBytesRead += bytesRead;
                int progress = (int) ((totalBytesRead * 100) / contentLength);
                // publishProgress(progress);
            }
            sink.flush();
            sink.close();
            source.close();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Exception while downloading file", e);
            return false;
        }
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

    private static Response callPutAPI(String url, RequestBody body) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
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

    public static Response getRoles() {
        return callGetAPI(BASE_URL + "/roles");
    }

    public static Response getRegisteredUser() {
        return callGetAPI(BASE_URL + "/register");
    }

    public static Response getOrders(int uid) {
        return callGetAPI(BASE_URL + "/order?uid=" + uid);
    }

    public static Response getOrders() {
        return callGetAPI(BASE_URL + "/order");
    }

    public static Response getConfig() {
        return callGetAPI(BASE_URL + "/config");
    }

    public static Response addUser(JSONObject obj) {
        RequestBody body = RequestBody.create(String.valueOf(obj), JSON);
        return callAPI(BASE_URL + "/register", body);
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
            Log.e(TAG, "Exception while calling get API", e);
            return null;
        }
    }

    public static Response updateOrder(int id, JSONObject inventoryJson) {
        RequestBody body = RequestBody.create(String.valueOf(inventoryJson), JSON);
        return callPutAPI(BASE_URL + "/order/" + id, body);
    }
}
