package com.example.mipt5;

import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataLoader {

    private static final String TAG = "DataLoader";
    private final DataLoaderCallback callback;
    private final ExecutorService executorService;

    public interface DataLoaderCallback {
        void onDataLoaded(List<String> data);
        void onError(Exception e);
    }

    public DataLoader(DataLoaderCallback callback) {
        this.callback = callback;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public void executeTask(String url) {
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Fetching data from URL: " + url);
                URL urlObj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                connection.setRequestMethod("GET");
                InputStream inputStream = connection.getInputStream();
                List<String> data = Parser.parseXML(inputStream);

                Log.d(TAG, "Fetched Data: " + data);
                if (callback != null) {
                    callback.onDataLoaded(data);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error fetching data", e);
                if (callback != null) {
                    callback.onError(e);
                }
            }
        });
    }
}
