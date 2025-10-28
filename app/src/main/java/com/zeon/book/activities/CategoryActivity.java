package com.zeon.book.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Toast;

import com.zeon.book.tools.Database;
import com.zeon.book.R;
import com.zeon.book.adapters.SeriesAdapter;
import com.zeon.book.models.Series;
import com.zeon.book.tools.constant.IntentName;
import com.zeon.book.tools.constant.Key;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {
    private RecyclerView recyclerViewSeries;
    private SeriesAdapter adapter;
    private String categoryTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        init();
    }

    private void init() {
        initViews();
        setupIntentData();
        setupRecyclerView();
        loadCategoryData();
    }

    private void initViews() {
        recyclerViewSeries = findViewById(R.id.recyclerViewSeries);
    }

    private void setupIntentData() {
        categoryTitle = getIntent().getStringExtra(IntentName.TITLE_NAME.getName());
        setTitle(categoryTitle);
    }

    private void setupRecyclerView() {
        recyclerViewSeries.setLayoutManager(new LinearLayoutManager(this));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void loadCategoryData() {
        List<Series> dataHolder = loadDataFromDatabase();
        setupAdapter(dataHolder);
    }

    private List<Series> loadDataFromDatabase() {
        List<Series> dataList = new ArrayList<>();
        Database database = new Database(this);

        try (Cursor cursor = getCategoryCursor(database)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(0);
                    if (isValidName(name)) {
                        dataList.add(new Series(name));
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_loading_data), Toast.LENGTH_SHORT).show();
        }

        return dataList;
    }

    private Cursor getCategoryCursor(Database database) {
        String series = getResources().getString(R.string.series);
        String author = getResources().getString(R.string.author);

        if (series.equals(categoryTitle)) {
            return database.readCategory(Key.SERIES.getName());
        } else if (author.equals(categoryTitle)) {
            return database.readCategory(Key.AUTHOR.getName());
        } else {
            return database.readCategory(Key.GENRE.getName());
        }
    }

    private boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    private void setupAdapter(List<Series> data) {
        if (adapter == null) {
            adapter = new SeriesAdapter(data, this, categoryTitle);
            recyclerViewSeries.setAdapter(adapter);
        } else {
            adapter.updateData(data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCategoryData();
    }
}