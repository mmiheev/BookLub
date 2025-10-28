package com.zeon.book.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.zeon.book.R;
import com.zeon.book.tools.constant.IntentName;
import com.zeon.book.models.Quote;
import com.zeon.book.activities.PageQuoteActivity;

import java.util.ArrayList;
import java.util.List;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.ViewHolder> {
    private List<Quote> quotes;
    private final Context context;

    public QuoteAdapter(List<Quote> quotes, Context context) {
        this.quotes = quotes != null ? quotes : new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quote, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Quote quote = quotes.get(position);
        holder.bind(quote);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, PageQuoteActivity.class)
                    .putExtra(IntentName.ID.getName(), quote.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return quotes.size();
    }

    public void updateData(List<Quote> newQuotes) {
        this.quotes = newQuotes != null ? newQuotes : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addData(List<Quote> additionalQuotes) {
        if (additionalQuotes != null && !additionalQuotes.isEmpty()) {
            int startPosition = quotes.size();
            quotes.addAll(additionalQuotes);
            notifyItemRangeInserted(startPosition, additionalQuotes.size());
        }
    }

    public void clearData() {
        int itemCount = quotes.size();
        quotes.clear();
        notifyItemRangeRemoved(0, itemCount);
    }

    @Nullable
    public Quote getItem(int position) {
        if (position >= 0 && position < quotes.size()) {
            return quotes.get(position);
        }
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView numQuoteTextView;
        private final TextView idTextView;
        private final TextView quoteTextView;
        private final TextView authorTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            numQuoteTextView = itemView.findViewById(R.id.numQuote);
            idTextView = itemView.findViewById(R.id.id);
            quoteTextView = itemView.findViewById(R.id.quote);
            authorTextView = itemView.findViewById(R.id.author);
        }

        public void bind(Quote quote) {
            numQuoteTextView.setText("#" + quote.getNum());
            idTextView.setText(String.valueOf(quote.getId()));
            quoteTextView.setText(quote.getQuote());
            authorTextView.setText(quote.getAuthor());
        }
    }
}
