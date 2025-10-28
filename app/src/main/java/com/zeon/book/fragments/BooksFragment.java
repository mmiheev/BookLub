package com.zeon.book.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.zeon.book.tools.Database;
import com.zeon.book.R;
import com.zeon.book.activities.BooksActivity;
import com.zeon.book.activities.CategoryActivity;
import com.zeon.book.tools.constant.IntentName;

import java.util.HashMap;
import java.util.Map;

public class BooksFragment extends Fragment {
    private TextView allScore, finishScore, favoriteScore;
    private Map<Integer, View> menuItems = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_books, container, false);
        initializeViews(view);
        setupClickListeners();
        updateScore();
        return view;
    }

    private void initializeViews(View view) {
        allScore = view.findViewById(R.id.allScore);
        finishScore = view.findViewById(R.id.finishScore);
        favoriteScore = view.findViewById(R.id.favoriteScore);

        menuItems.put(R.id.btnAll, view.findViewById(R.id.btnAll));
        menuItems.put(R.id.btnFinished, view.findViewById(R.id.btnFinished));
        menuItems.put(R.id.btnFavorite, view.findViewById(R.id.btnFavorite));
        menuItems.put(R.id.btnAuthor, view.findViewById(R.id.btnAuthor));
        menuItems.put(R.id.btnSeries, view.findViewById(R.id.btnSeries));
        menuItems.put(R.id.btnGenre, view.findViewById(R.id.btnGenre));
    }

    private void setupClickListeners() {
        menuItems.get(R.id.btnAll).setOnClickListener(v ->
                startBooksActivity(getString(R.string.category_all), 0, 0));

        menuItems.get(R.id.btnFinished).setOnClickListener(v ->
                startBooksActivity(getString(R.string.category_finished), 1, 0));

        menuItems.get(R.id.btnFavorite).setOnClickListener(v ->
                startBooksActivity(getString(R.string.category_favorite), 0, 1));

        menuItems.get(R.id.btnAuthor).setOnClickListener(v ->
                startCategoryActivity(getString(R.string.author)));

        menuItems.get(R.id.btnSeries).setOnClickListener(v ->
                startCategoryActivity(getString(R.string.series)));

        menuItems.get(R.id.btnGenre).setOnClickListener(v ->
                startCategoryActivity(getString(R.string.genre)));
    }

    private void startBooksActivity(String title, int finished, int favorite) {
        Intent intent = new Intent(getActivity(), BooksActivity.class)
                .putExtra(IntentName.TITLE_NAME.getName(), title);

        if (finished > 0) {
            intent.putExtra(IntentName.FINISHED.getName(), finished);
        }
        if (favorite > 0) {
            intent.putExtra(IntentName.FAVORITE.getName(), favorite);
        }

        startActivity(intent);
    }

    private void startCategoryActivity(String category) {
        startActivity(new Intent(getActivity(), CategoryActivity.class)
                .putExtra(IntentName.TITLE_NAME.getName(), category));
    }

    private void updateScore() {
        Database database = new Database(getActivity());

        try {
            updateScoreFromCursor(database.readScoreBy("favorite", 1), favoriteScore);
            updateScoreFromCursor(database.readScoreBy("finished", 1), finishScore);
            updateScoreFromCursor(database.getCountBooks(), allScore);
        } catch (Exception e) {
            Log.e("BooksFragment", "Error updating scores", e);
            setDefaultScores();
        }
    }

    private void updateScoreFromCursor(Cursor cursor, TextView textView) {
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    String score = cursor.getString(0);
                    textView.setText(score);
                } else {
                    textView.setText("0");
                }
            } finally {
                cursor.close();
            }
        } else {
            textView.setText("0");
        }
    }

    private void setDefaultScores() {
        allScore.setText("0");
        finishScore.setText("0");
        favoriteScore.setText("0");
    }

    @Override
    public void onResume() {
        super.onResume();
        updateScore();
    }
}