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
import com.zeon.book.activities.BooksActivity;
import com.zeon.book.models.Series;

import java.util.ArrayList;
import java.util.List;

public class SeriesAdapter extends RecyclerView.Adapter<SeriesAdapter.ViewHolder> {
    private List<Series> seriesList;
    private final Context context;
    private final String categoryKey;
    private final String intentName;

    public SeriesAdapter(List<Series> seriesList, Context context, String categoryKey) {
        this.seriesList = seriesList != null ? seriesList : new ArrayList<>();
        this.context = context;
        this.categoryKey = categoryKey;
        this.intentName = resolveIntentName(categoryKey, context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Series series = seriesList.get(position);
        holder.bind(series);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, BooksActivity.class)
                    .putExtra(intentName, series.getName())
                    .putExtra(IntentName.TITLE_NAME.getName(), categoryKey);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return seriesList.size();
    }

    private String resolveIntentName(String key, Context context) {
        String seriesKey = context.getResources().getString(R.string.series);
        String authorKey = context.getResources().getString(R.string.author);

        if (seriesKey.equals(key)) {
            return IntentName.SERIES.getName();
        } else if (authorKey.equals(key)) {
            return IntentName.AUTHOR.getName();
        } else {
            return IntentName.GENRE.getName();
        }
    }

    public void updateData(List<Series> newData) {
        this.seriesList = newData != null ? newData : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addData(List<Series> additionalData) {
        if (additionalData != null && !additionalData.isEmpty()) {
            int startPosition = seriesList.size();
            seriesList.addAll(additionalData);
            notifyItemRangeInserted(startPosition, additionalData.size());
        }
    }

    public void clearData() {
        int itemCount = seriesList.size();
        seriesList.clear();
        notifyItemRangeRemoved(0, itemCount);
    }

    @Nullable
    public Series getItem(int position) {
        if (position >= 0 && position < seriesList.size()) {
            return seriesList.get(position);
        }
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tvName);
        }

        public void bind(Series series) {
            nameTextView.setText(series.getName());
        }
    }
}