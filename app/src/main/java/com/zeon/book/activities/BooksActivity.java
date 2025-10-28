package com.zeon.book.activities;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zeon.book.tools.Database;
import com.zeon.book.R;
import com.zeon.book.tools.constant.IntentName;
import com.zeon.book.adapters.BookAdapter;
import com.zeon.book.models.Book;

import java.util.ArrayList;

public class BooksActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView recyclerViewBooks;
    private FloatingActionButton floatingActionAddButton;
    private TextView tvSorry;

    private ArrayList<Book> allBooks = new ArrayList<>();
    private ArrayList<Book> filteredBooks = new ArrayList<>();
    private BookAdapter bookAdapter;

    private int favoriteValue = 0;
    private int finishedValue = 0;
    private boolean isYear = false;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        initViews();
        setupIntentData();
        setupRecyclerView();
        setupClickListeners();
        setupSearchView();
        loadData();
    }

    private void initViews() {
        floatingActionAddButton = findViewById(R.id.floatingActionAddButton);
        recyclerViewBooks = findViewById(R.id.recyclerViewBooks);
        searchView = findViewById(R.id.searchView);
        tvSorry = findViewById(R.id.tvSorry);
    }

    private void setupIntentData() {
        Intent intent = getIntent();
        finishedValue = intent.getIntExtra(IntentName.FINISHED.getName(), 0);
        favoriteValue = intent.getIntExtra(IntentName.FAVORITE.getName(), 0);
        title = intent.getStringExtra(IntentName.TITLE_NAME.getName());
        isYear = intent.getBooleanExtra(IntentName.IS_YEAR.getName(), false);

        setTitle(title);

        if (finishedValue > 0 || favoriteValue > 0 || isYear ||
                isSpecialCategory(title)) {
            floatingActionAddButton.setVisibility(View.GONE);
        }
    }

    private boolean isSpecialCategory(@NonNull String category) {
        String authorStr = getResources().getString(R.string.author);
        String seriesStr = getResources().getString(R.string.series);
        String genreStr = getResources().getString(R.string.genre);
        return category.equals(authorStr) || category.equals(seriesStr) || category.equals(genreStr);
    }

    private void setupRecyclerView() {
        recyclerViewBooks.setLayoutManager(new LinearLayoutManager(this));
        bookAdapter = new BookAdapter(filteredBooks, this);
        recyclerViewBooks.setAdapter(bookAdapter);
    }

    private void setupClickListeners() {
        floatingActionAddButton.setOnClickListener(v ->
                startActivity(new Intent(this, AddActivity.class)
                        .putExtra(IntentName.FAVORITE.getName(), 0)
                        .putExtra(IntentName.FINISHED.getName(), 0)
                        .putExtra(IntentName.UPDATE.getName(), false))
        );
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String input) {
                filterBooks(input);
                return true;
            }
        });
    }

    private void loadData() {
        try (Cursor cursor = getDataCursor()) {
            allBooks.clear();

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Book book = new Book(
                            cursor.getInt(0),
                            cursor.getString(1),
                            cursor.getString(2)
                    );
                    allBooks.add(book);
                } while (cursor.moveToNext());
            }

            updateUI();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_loading_data), Toast.LENGTH_SHORT).show();
        }
    }

    private Cursor getDataCursor() {
        Database database = new Database(this);

        if (isYear) {
            int year = getIntent().getIntExtra(IntentName.YEAR.getName(), -1);
            return database.readBy("year", year);
        }

        if (favoriteValue == 1) {
            return database.readBy("favorite", favoriteValue);
        } else if (finishedValue == 1) {
            return database.readBy("finished", finishedValue);
        } else if (title.equals(getResources().getString(R.string.author))) {
            return database.readBy("author", getIntent().getStringExtra(IntentName.AUTHOR.getName()));
        } else if (title.equals(getResources().getString(R.string.series))) {
            return database.readBy("series", getIntent().getStringExtra(IntentName.SERIES.getName()));
        } else if (title.equals(getResources().getString(R.string.genre))) {
            return database.readBy("genre", getIntent().getStringExtra(IntentName.GENRE.getName()));
        } else {
            return database.readAllBooks();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterBooks(String query) {
        filteredBooks.clear();

        if (TextUtils.isEmpty(query)) {
            filteredBooks.addAll(allBooks);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Book book : allBooks) {
                if (book.getAuthor().toLowerCase().contains(lowerCaseQuery) ||
                        book.getBookName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredBooks.add(book);
                }
            }
        }

        bookAdapter.notifyDataSetChanged();
        updateVisibility();
    }

    private void updateUI() {
        String currentQuery = searchView.getQuery().toString();
        filterBooks(currentQuery);
    }

    private void updateVisibility() {
        boolean hasData = !filteredBooks.isEmpty();
        tvSorry.setVisibility(hasData ? View.GONE : View.VISIBLE);
        recyclerViewBooks.setVisibility(hasData ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}