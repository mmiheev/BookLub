package com.zeon.book.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.zeon.book.R;
import com.zeon.book.tools.Database;
import com.zeon.book.tools.constant.IntentName;

import java.util.Calendar;
import java.util.Locale;


public class PageBookActivity extends AppCompatActivity {
    private TextView bookName, author, note, description, textViewGenre, textViewSeries;

    private int id = 1;
    private int favoriteValue = 0;
    private int finishedValue = 0;
    private int year = -1;
    private String date = "";

    private Dialog dateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_book);
        init();
    }

    private void init() {
        initViews();
        loadBookData();
    }

    private void initViews() {
        bookName = findViewById(R.id.bookName);
        author = findViewById(R.id.author);
        note = findViewById(R.id.note);
        description = findViewById(R.id.description);
        textViewGenre = findViewById(R.id.textViewGenre);
        textViewSeries = findViewById(R.id.textViewSeries);

        dateDialog = new Dialog(this);
    }

    private void loadBookData() {
        id = getIntent().getIntExtra(IntentName.ID.getName(), 1);
        setTitle(getString(R.string.book));

        try (Cursor cursor = new Database(this).readBy(IntentName.ID.getName(), id)) {
            if (cursor != null && cursor.moveToFirst()) {
                populateBookData(cursor);
            }
        } catch (Exception e) {
            Log.e("PageBookActivity", "Error loading book data", e);
            Toast.makeText(this, getString(R.string.error_loading_data), Toast.LENGTH_SHORT).show();
        }
    }

    private void populateBookData(Cursor cursor) {
        String strBookName = cursor.getString(cursor.getColumnIndexOrThrow("book_name"));
        String strAuthor = cursor.getString(cursor.getColumnIndexOrThrow("author"));
        String strNote = cursor.getString(cursor.getColumnIndexOrThrow("note"));
        String strDescription = cursor.getString(cursor.getColumnIndexOrThrow("description"));
        String strGenre = cursor.getString(cursor.getColumnIndexOrThrow("genre"));
        String strSeries = cursor.getString(cursor.getColumnIndexOrThrow("series"));

        favoriteValue = cursor.getInt(cursor.getColumnIndexOrThrow("favorite"));
        finishedValue = cursor.getInt(cursor.getColumnIndexOrThrow("finished"));
        date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
        year = cursor.getInt(cursor.getColumnIndexOrThrow("year"));

        bookName.setText(strBookName);
        author.setText(strAuthor);
        note.setText(strNote);
        description.setText(strDescription);
        textViewSeries.setText(strSeries);
        textViewGenre.setText(strGenre);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.up_page_menu, menu);
        updateMenuIcons(menu);
        return true;
    }

    private void updateMenuIcons(Menu menu) {
        MenuItem finishedItem = menu.getItem(0);
        MenuItem favoriteItem = menu.getItem(1);

        int finishedIcon = finishedValue == 0 ?
                R.drawable.ic_finished : R.drawable.ic_not_finished;
        int favoriteIcon = favoriteValue == 0 ?
                R.drawable.ic_not_like : R.drawable.ic_like;

        finishedItem.setIcon(getDrawable(finishedIcon));
        favoriteItem.setIcon(getDrawable(favoriteIcon));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.navigation_delete) {
            showDeleteConfirmation();
            return true;
        } else if (itemId == R.id.navigation_edit) {
            openEditActivity();
            return true;
        } else if (itemId == R.id.navigation_like) {
            toggleFavorite(item);
            return true;
        } else if (itemId == R.id.navigation_finished) {
            toggleFinished(item);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Удалить книгу?")
                .setPositiveButton("Да", (dialog, which) -> deleteBook())
                .setNegativeButton("Нет", null)
                .show();
    }

    private void deleteBook() {
        new Database(this).delete(String.valueOf(id));
        finish();
    }

    private void openEditActivity() {
        Intent editIntent = new Intent(this, AddActivity.class)
                .putExtra(IntentName.ID.getName(), id)
                .putExtra(IntentName.FAVORITE.getName(), favoriteValue)
                .putExtra(IntentName.FINISHED.getName(), finishedValue)
                .putExtra(IntentName.BOOK_NAME.getName(), bookName.getText().toString())
                .putExtra(IntentName.AUTHOR.getName(), author.getText().toString())
                .putExtra(IntentName.YEAR.getName(), year)
                .putExtra(IntentName.NOTE.getName(), note.getText().toString())
                .putExtra(IntentName.GENRE.getName(), textViewGenre.getText().toString())
                .putExtra(IntentName.SERIES.getName(), textViewSeries.getText().toString())
                .putExtra(IntentName.DESCRIPTION.getName(), description.getText().toString())
                .putExtra(IntentName.UPDATE.getName(), true);

        startActivity(editIntent);
    }

    private void toggleFavorite(MenuItem item) {
        favoriteValue = favoriteValue == 1 ? 0 : 1;

        String message = favoriteValue == 1 ?
                getString(R.string.book_added_to_favorites) : getString(R.string.book_removed_from_favorites);
        int icon = favoriteValue == 1 ?
                R.drawable.ic_like : R.drawable.ic_not_like;

        item.setIcon(getDrawable(icon));
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        updateBookStatus();
    }

    private void toggleFinished(MenuItem item) {
        if (finishedValue == 1) {
            markAsUnfinished(item);
        } else {
            showDatePickerDialog(item);
        }
    }

    private void markAsUnfinished(MenuItem item) {
        finishedValue = 0;
        year = -1;
        date = "";

        item.setIcon(getDrawable(R.drawable.ic_finished));
        Toast.makeText(this, getString(R.string.book_not_finished), Toast.LENGTH_SHORT).show();

        updateBookStatus();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showDatePickerDialog(MenuItem item) {
        dateDialog.setContentView(R.layout.dialog_choose_date);
        dateDialog.setTitle(getString(R.string.set_reading_date));

        TextView tvDate = dateDialog.findViewById(R.id.tvDate);
        MaterialButton btnSave = dateDialog.findViewById(R.id.btnSave);
        MaterialButton btnCancel = dateDialog.findViewById(R.id.btnCancel);

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, selectedYear, month, day) -> {
            String selectedDate = String.format(Locale.getDefault(), "%d/%d/%d", day, month + 1, selectedYear);
            tvDate.setText(selectedDate);
            year = selectedYear;
            date = selectedDate;
        };

        tvDate.setOnClickListener(view -> {
            new DatePickerDialog(
                    this,
                    android.R.style.Theme_Holo_Dialog_MinWidth,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        btnSave.setOnClickListener(view -> {
            if (year <= 0) {
                year = Calendar.getInstance().get(Calendar.YEAR);
            }

            finishedValue = 1;
            updateBookStatus();

            item.setIcon(getDrawable(R.drawable.ic_not_finished));
            Toast.makeText(this, getString(R.string.book_finished), Toast.LENGTH_SHORT).show();

            dateDialog.dismiss();
            loadBookData();
        });

        btnCancel.setOnClickListener(view -> dateDialog.dismiss());
        dateDialog.show();
    }

    private void updateBookStatus() {
        new Database(this).update(
                String.valueOf(id),
                getTextSafe(bookName),
                getTextSafe(author),
                getTextSafe(note),
                getTextSafe(description),
                getTextSafe(textViewGenre),
                getTextSafe(textViewSeries),
                favoriteValue,
                finishedValue,
                date,
                year
        );
    }

    private String getTextSafe(TextView textView) {
        return textView.getText() != null ? textView.getText().toString() : "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookData();
        invalidateOptionsMenu();
    }
}