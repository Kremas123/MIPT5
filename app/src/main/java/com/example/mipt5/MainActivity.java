package com.example.mipt5;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private final List<String> exchangeRatesList = new ArrayList<>(); // Original list of all currencies
    private final List<String> currentDisplayedList = new ArrayList<>(); // List for filtered results
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate called");

        // Initialize views
        ListView listViewRates = findViewById(R.id.listViewRates);
        EditText editTextFilter = findViewById(R.id.editTextFilter);

        // Setup adapter for the displayed list
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currentDisplayedList);
        listViewRates.setAdapter(adapter);

        // Fetch exchange rates
        fetchExchangeRates();

        // Add filter logic
        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRates(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchExchangeRates() {
        Log.d(TAG, "Fetching exchange rates");
        DataLoader dataLoader = new DataLoader(new DataLoader.DataLoaderCallback() {
            @Override
            public void onDataLoaded(List<String> data) {
                Log.d(TAG, "Data loaded: " + data);

                // Update the UI on the main thread
                runOnUiThread(() -> {
                    exchangeRatesList.clear();
                    exchangeRatesList.addAll(data);

                    // Initially, display all the currencies
                    currentDisplayedList.clear();
                    currentDisplayedList.addAll(exchangeRatesList);

                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "Error loading data", e);
            }
        });
        dataLoader.executeTask("https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml");
    }

    private void filterRates(String query) {
        query = query.toUpperCase();

        if (query.isEmpty()) {
            // If the query is empty, restore the full list
            Log.d(TAG, "Query is empty, restoring full list");
            currentDisplayedList.clear();
            currentDisplayedList.addAll(exchangeRatesList);
        } else {
            // Filter the list based on the query
            Log.d(TAG, "Filtering list for query: " + query);
            currentDisplayedList.clear();

            for (String rate : exchangeRatesList) {
                if (rate.toUpperCase().startsWith(query)) {
                    currentDisplayedList.add(rate);
                }
            }
        }

        // Notify the adapter of the changes
        adapter.notifyDataSetChanged();
    }
}
