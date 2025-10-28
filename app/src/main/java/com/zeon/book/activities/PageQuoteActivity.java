package com.zeon.book.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.zeon.book.tools.Database;
import com.zeon.book.R;
import com.zeon.book.tools.constant.IntentName;

public class PageQuoteActivity extends AppCompatActivity {
    private TextView quoteTextView;
    private TextView authorTextView;
    private String quoteId;
    private Dialog editDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_quote);
        initialize();
    }

    private void initialize() {
        setTitle(getString(R.string.quote));
        initViews();
        loadQuoteData();
    }

    private void initViews() {
        authorTextView = findViewById(R.id.author);
        quoteTextView = findViewById(R.id.quote);
        editDialog = new Dialog(this);
    }

    private void loadQuoteData() {
        quoteId = String.valueOf(getIntent().getIntExtra(IntentName.ID.getName(), 0));
        updateQuoteDisplay();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.up_page_menu, menu);

        menu.findItem(R.id.navigation_finished).setVisible(false);
        menu.findItem(R.id.navigation_like).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.navigation_delete) {
            showDeleteConfirmation();
            return true;
        } else if (itemId == R.id.navigation_edit) {
            showEditDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showEditDialog() {
        editDialog.setContentView(R.layout.dialog_add_quote);
        setupEditDialogViews();
        editDialog.show();
    }

    private void setupEditDialogViews() {
        TextInputEditText quoteInput = editDialog.findViewById(R.id.quote);
        TextInputEditText authorInput = editDialog.findViewById(R.id.author);
        MaterialButton btnCancel = editDialog.findViewById(R.id.btnCancel);
        MaterialButton btnAdd = editDialog.findViewById(R.id.btnAdd);
        MaterialButton btnUpdate = editDialog.findViewById(R.id.btnUpdate);

        authorInput.setText(authorTextView.getText());
        quoteInput.setText(quoteTextView.getText());

        btnAdd.setVisibility(View.GONE);
        btnUpdate.setVisibility(View.VISIBLE);

        btnUpdate.setOnClickListener(view -> {
            updateQuote(
                    authorInput.getText().toString(),
                    quoteInput.getText().toString()
            );
            editDialog.dismiss();
        });

        btnCancel.setOnClickListener(view -> editDialog.dismiss());
    }

    private void updateQuote(String author, String quote) {
        try {
            new Database(this).updateQuote(quoteId, author, quote);
            updateQuoteDisplay();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_updating_record), Toast.LENGTH_SHORT).show();
            Log.e("PageQuoteActivity", "Error updating quote", e);
        }
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_quote_confirm))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> deleteQuote())
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void deleteQuote() {
        try {
            new Database(this).deleteQuote(quoteId);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_deleting), Toast.LENGTH_SHORT).show();
            Log.e("PageQuoteActivity", "Error deleting quote", e);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateQuoteDisplay() {
        try (Cursor cursor = new Database(this).readQuote(quoteId)) {
            if (cursor != null && cursor.moveToFirst()) {
                String author = cursor.getString(cursor.getColumnIndexOrThrow("author"));
                String quote = cursor.getString(cursor.getColumnIndexOrThrow("quote"));

                authorTextView.setText(author);
                quoteTextView.setText(quote);
            }
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.error_loading_data), Toast.LENGTH_SHORT).show();
            Log.e("PageQuoteActivity", "Error loading quote", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateQuoteDisplay();
    }
}