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
import com.zeon.book.activities.PageBookActivity;
import com.zeon.book.tools.constant.IntentName;
import com.zeon.book.models.Book;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private List<Book> books;
    private final Context context;

    public BookAdapter(List<Book> books, Context context) {
        this.books = books != null ? books : new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = books.get(position);
        holder.bind(book);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, PageBookActivity.class)
                    .putExtra(IntentName.ID.getName(), book.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void updateData(List<Book> newBooks) {
        this.books = newBooks != null ? newBooks : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addData(List<Book> additionalBooks) {
        if (additionalBooks != null && !additionalBooks.isEmpty()) {
            int startPosition = books.size();
            books.addAll(additionalBooks);
            notifyItemRangeInserted(startPosition, additionalBooks.size());
        }
    }

    public void clearData() {
        int itemCount = books.size();
        books.clear();
        notifyItemRangeRemoved(0, itemCount);
    }

    @Nullable
    public Book getItem(int position) {
        if (position >= 0 && position < books.size()) {
            return books.get(position);
        }
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView idTextView;
        private final TextView bookNameTextView;
        private final TextView authorTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.id);
            bookNameTextView = itemView.findViewById(R.id.bookName);
            authorTextView = itemView.findViewById(R.id.author);
        }

        public void bind(Book book) {
            idTextView.setText(String.valueOf(book.getId()));
            bookNameTextView.setText(book.getBookName());
            authorTextView.setText(book.getAuthor());
        }
    }
}
