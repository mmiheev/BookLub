package com.zeon.book.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.zeon.book.tools.Database;
import com.zeon.book.R;
import com.zeon.book.tools.constant.IntentName;


public class AddActivity extends AppCompatActivity {
    private static final int DEFAULT_YEAR = -1;
    private static final int DEFAULT_ID = 1;

    private TextInputEditText bookName, author, note, textInputGenre, textInputSeries, shortDescription;
    private MaterialButton btnCancel, btnAdd, btnUpdate;

    private int favoriteValue = 0;
    private int finishedValue = 0;
    private int id = DEFAULT_ID;
    private int year = DEFAULT_YEAR;
    private boolean isUpdateMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initViews();
        setupIntentData();
        setupButtonVisibility();
        setupClickListeners();
    }

    private void initViews() {
        bookName = findViewById(R.id.bookName);
        author = findViewById(R.id.author);
        note = findViewById(R.id.note);
        shortDescription = findViewById(R.id.shortDescription);
        textInputGenre = findViewById(R.id.textInputGenre);
        textInputSeries = findViewById(R.id.textInputSeries);

        btnCancel = findViewById(R.id.btnCancel);
        btnAdd = findViewById(R.id.btnAdd);
        btnUpdate = findViewById(R.id.btnUpdate);
    }

    private void setupIntentData() {
        Intent intent = getIntent();
        favoriteValue = intent.getIntExtra(IntentName.FAVORITE.getName(), 0);
        finishedValue = intent.getIntExtra(IntentName.FINISHED.getName(), 0);
        id = intent.getIntExtra(IntentName.ID.getName(), DEFAULT_ID);
        year = intent.getIntExtra(IntentName.YEAR.getName(), DEFAULT_YEAR);
        isUpdateMode = intent.getBooleanExtra(IntentName.UPDATE.getName(), false);

        setFieldFromIntent(intent, IntentName.BOOK_NAME, bookName);
        setFieldFromIntent(intent, IntentName.AUTHOR, author);
        setFieldFromIntent(intent, IntentName.NOTE, note);
        setFieldFromIntent(intent, IntentName.DESCRIPTION, shortDescription);
        setFieldFromIntent(intent, IntentName.GENRE, textInputGenre);
        setFieldFromIntent(intent, IntentName.SERIES, textInputSeries);
    }

    private void setFieldFromIntent(Intent intent, IntentName fieldName, TextInputEditText field) {
        String value = intent.getStringExtra(fieldName.getName());
        if (value != null) {
            field.setText(value);
        }
    }

    private void setupButtonVisibility() {
        if (isUpdateMode) {
            btnUpdate.setVisibility(View.VISIBLE);
            btnAdd.setVisibility(View.GONE);
        } else {
            btnUpdate.setVisibility(View.GONE);
            btnAdd.setVisibility(View.VISIBLE);
        }
    }

    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> finish());

        btnAdd.setOnClickListener(v -> {
            if (validateFields()) {
                addRecord();
                finish();
            }
        });

        btnUpdate.setOnClickListener(v -> {
            if (validateFields()) {
                updateRecord();
                finish();
            }
        });
    }

    private boolean validateFields() {
        return !bookName.getText().toString().trim().isEmpty()
                && !author.getText().toString().trim().isEmpty();
    }

    private void addRecord() {
        try {
            new Database(this).addRecord(
                    getSafeText(bookName),
                    getSafeText(author),
                    getSafeText(note),
                    getSafeText(shortDescription),
                    getSafeText(textInputGenre),
                    getSafeText(textInputSeries),
                    favoriteValue,
                    finishedValue
            );
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_adding_record), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRecord() {
        try {
            new Database(this).update(
                    String.valueOf(id),
                    getSafeText(bookName),
                    getSafeText(author),
                    getSafeText(note),
                    getSafeText(shortDescription),
                    getSafeText(textInputGenre),
                    getSafeText(textInputSeries),
                    favoriteValue,
                    finishedValue,
                    "",
                    year
            );
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_updating_record), Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    private String getSafeText(TextInputEditText field) {
        return field.getText() != null ? field.getText().toString().trim() : "";
    }
}