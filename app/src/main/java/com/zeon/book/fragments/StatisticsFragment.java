package com.zeon.book.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeon.book.tools.Database;
import com.zeon.book.R;
import com.zeon.book.activities.BooksActivity;
import com.zeon.book.adapters.YearAdapter;
import com.zeon.book.tools.constant.IntentName;
import com.zeon.book.models.Year;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticsFragment extends Fragment {
    private TextView textViewScore, textViewTitle;
    private CardView currentBooksCard;
    private RecyclerView yearsRecyclerView;
    private YearAdapter yearAdapter;
    private int currentYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        initViews(view);
        setupClickListeners();
        loadData();
        return view;
    }

    private void initViews(View view) {
        textViewScore = view.findViewById(R.id.textViewScore);
        textViewTitle = view.findViewById(R.id.textViewTitle);
        currentBooksCard = view.findViewById(R.id.currentBooks);
        yearsRecyclerView = view.findViewById(R.id.rvYears);

        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        textViewTitle.setText(getString(R.string.statistics_for_year, currentYear));

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        yearsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        yearAdapter = new YearAdapter(new ArrayList<>(), getActivity());
        yearsRecyclerView.setAdapter(yearAdapter);
    }

    private void setupClickListeners() {
        currentBooksCard.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BooksActivity.class)
                    .putExtra(IntentName.YEAR.getName(), currentYear)
                    .putExtra(IntentName.IS_YEAR.getName(), true)
                    .putExtra(IntentName.TITLE_NAME.getName(), String.valueOf(currentYear));
            startActivity(intent);
        });
    }

    private void loadData() {
        updateScore();
        loadYears();
    }

    private void updateScore() {
        try (Cursor cursor = new Database(getActivity()).readScoreBy("year", currentYear)) {
            if (cursor != null && cursor.moveToFirst()) {
                String score = cursor.getString(0);
                textViewScore.setText(score);
            } else {
                textViewScore.setText("0"); // Значение по умолчанию
            }
        } catch (Exception e) {
            Log.e("AimFragment", "Error loading score", e);
            textViewScore.setText("0");
        }
    }

    private void loadYears() {
        List<Year> years = new ArrayList<>();

        try (Cursor cursor = new Database(getActivity()).readYears()) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int year = cursor.getInt(0);
                    years.add(new Year(year));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("AimFragment", "Error loading years", e);
        }

        yearAdapter.updateData(years);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateScore();
    }
}