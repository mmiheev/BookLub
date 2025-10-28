package com.zeon.book.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.zeon.book.R;
import com.zeon.book.activities.BooksActivity;
import com.zeon.book.tools.constant.IntentName;
import com.zeon.book.models.Year;

import java.util.ArrayList;
import java.util.List;

public class YearAdapter extends RecyclerView.Adapter<YearAdapter.ViewHolder> {
    private List<Year> years;
    private final Context context;

    public YearAdapter(List<Year> years, Context context) {
        this.years = years != null ? years : new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Year year = years.get(position);
        holder.bind(year);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, BooksActivity.class)
                    .putExtra(IntentName.YEAR.getName(), year.getYear())
                    .putExtra(IntentName.IS_YEAR.getName(), true)
                    .putExtra(IntentName.TITLE_NAME.getName(), String.valueOf(year.getYear()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return years.size();
    }

    public void updateData(List<Year> newData) {
        this.years = newData != null ? newData : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addData(List<Year> additionalData) {
        if (additionalData != null && !additionalData.isEmpty()) {
            int startPosition = years.size();
            years.addAll(additionalData);
            notifyItemRangeInserted(startPosition, additionalData.size());
        }
    }

    public void clearData() {
        int itemCount = years.size();
        years.clear();
        notifyItemRangeRemoved(0, itemCount);
    }

    @Nullable
    public Year getItem(int position) {
        if (position >= 0 && position < years.size()) {
            return years.get(position);
        }
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView yearTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            yearTextView = itemView.findViewById(R.id.tvName);
        }

        public void bind(Year year) {
            yearTextView.setText(String.valueOf(year.getYear()));
        }
    }
}
