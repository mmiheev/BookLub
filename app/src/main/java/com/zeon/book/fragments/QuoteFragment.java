package com.zeon.book.fragments;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.zeon.book.tools.Database;
import com.zeon.book.R;
import com.zeon.book.models.Quote;
import com.zeon.book.adapters.QuoteAdapter;

import java.util.ArrayList;
import java.util.List;

public class QuoteFragment extends Fragment {
    private RecyclerView quoteRecyclerView;
    private FloatingActionButton addQuoteButton;
    private TextView emptyStateTextView;
    private QuoteAdapter quoteAdapter;
    private Dialog addQuoteDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quote, container, false);
        initViews(view);
        setupClickListeners();
        loadQuotes();
        return view;
    }

    private void initViews(View view) {
        quoteRecyclerView = view.findViewById(R.id.quoteList);
        emptyStateTextView = view.findViewById(R.id.tvSorry);
        addQuoteButton = view.findViewById(R.id.floatingActionAddButton);

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        quoteRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        quoteAdapter = new QuoteAdapter(new ArrayList<>(), getContext());
        quoteRecyclerView.setAdapter(quoteAdapter);
    }

    private void setupClickListeners() {
        addQuoteButton.setOnClickListener(v -> showAddQuoteDialog());
    }

    private void loadQuotes() {
        List<Quote> quotes = new ArrayList<>();
        int quoteNumber = 0;

        try (Cursor cursor = new Database(getContext()).readQuotes()) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    quoteNumber++;
                    Quote quote = new Quote(
                            cursor.getInt(0),
                            quoteNumber,
                            cursor.getString(1),
                            cursor.getString(2)
                    );
                    quotes.add(quote);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("QuoteFragment", "Error loading quotes", e);
        }

        updateUI(quotes);
    }

    private void updateUI(List<Quote> quotes) {
        quoteAdapter.updateData(quotes);
        updateVisibility(quotes.isEmpty());
    }

    private void updateVisibility(boolean isEmpty) {
        emptyStateTextView.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        quoteRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void showAddQuoteDialog() {
        if (addQuoteDialog == null) {
            createAddQuoteDialog();
        }
        addQuoteDialog.show();
    }

    private void createAddQuoteDialog() {
        addQuoteDialog = new Dialog(requireContext());
        addQuoteDialog.setContentView(R.layout.dialog_add_quote);
        setupDialogViews();
    }

    private void setupDialogViews() {
        TextInputEditText quoteInput = addQuoteDialog.findViewById(R.id.quote);
        TextInputEditText authorInput = addQuoteDialog.findViewById(R.id.author);
        MaterialButton cancelButton = addQuoteDialog.findViewById(R.id.btnCancel);
        MaterialButton addButton = addQuoteDialog.findViewById(R.id.btnAdd);

        addButton.setOnClickListener(view -> {
            String quoteText = quoteInput.getText() != null ? quoteInput.getText().toString().trim() : "";
            String authorText = authorInput.getText() != null ? authorInput.getText().toString().trim() : "";

            if (isValidQuote(quoteText, authorText)) {
                addQuote(quoteText, authorText);
                addQuoteDialog.dismiss();
                clearDialogFields(quoteInput, authorInput);
            } else {
                showValidationError();
            }
        });

        cancelButton.setOnClickListener(view -> {
            addQuoteDialog.dismiss();
            clearDialogFields(quoteInput, authorInput);
        });
    }

    private boolean isValidQuote(String quote, String author) {
        return !quote.isEmpty() && !author.isEmpty();
    }

    private void addQuote(String quote, String author) {
        try {
            new Database(getContext()).addQuote(quote, author);
            loadQuotes();
            Toast.makeText(getContext(), getString(R.string.quote_added), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("QuoteFragment", "Error adding quote", e);
            Toast.makeText(getContext(), getString(R.string.error_adding_record), Toast.LENGTH_SHORT).show();
        }
    }

    private void clearDialogFields(TextInputEditText quoteInput, TextInputEditText authorInput) {
        quoteInput.setText("");
        authorInput.setText("");
    }

    private void showValidationError() {
        Toast.makeText(getContext(), getString(R.string.fill_quote_and_author), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadQuotes();
    }

    @Override
    public void onDestroyView() {
        if (addQuoteDialog != null && addQuoteDialog.isShowing()) {
            addQuoteDialog.dismiss();
        }
        addQuoteDialog = null;
        super.onDestroyView();
    }
}