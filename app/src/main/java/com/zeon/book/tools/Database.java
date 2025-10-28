package com.zeon.book.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Books";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_BOOKS = "BOOKS";
    private static final String TABLE_QUOTES = "QUOTES";

    // Common column names
    private static final String COLUMN_ID = "ID";
    private static final String COLUMN_BOOK_NAME = "book_name";
    private static final String COLUMN_QUOTE = "quote";
    private static final String COLUMN_GENRE = "genre";
    private static final String COLUMN_SERIES = "series";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_NOTE = "note";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_FAVORITE = "favorite";
    private static final String COLUMN_FINISHED = "finished";

    // SQL statements
    private static final String CREATE_TABLE_QUOTES =
            "CREATE TABLE " + TABLE_QUOTES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_QUOTE + " TEXT, " +
                    COLUMN_AUTHOR + " TEXT)";

    private static final String CREATE_TABLE_BOOKS =
            "CREATE TABLE " + TABLE_BOOKS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_BOOK_NAME + " TEXT, " +
                    COLUMN_AUTHOR + " TEXT, " +
                    COLUMN_NOTE + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_GENRE + " TEXT, " +
                    COLUMN_SERIES + " TEXT, " +
                    COLUMN_DATE + " TEXT, " +
                    COLUMN_FAVORITE + " INTEGER, " +
                    COLUMN_YEAR + " INTEGER, " +
                    COLUMN_FINISHED + " INTEGER)";

    public Database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUOTES);
        db.execSQL(CREATE_TABLE_BOOKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        onCreate(db);
    }

    // Quote operations
    public boolean addQuote(String quote, String author) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUOTE, quote);
        values.put(COLUMN_AUTHOR, author);

        long result = db.insert(TABLE_QUOTES, null, values);
        return result != -1;
    }

    public Cursor readQuotes() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_QUOTES, null, null, null, null, null, COLUMN_ID);
    }

    public Cursor readQuote(String id) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {id};
        return db.query(TABLE_QUOTES, null, selection, selectionArgs, null, null, null);
    }

    public boolean updateQuote(String id, String author, String quote) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AUTHOR, author);
        values.put(COLUMN_QUOTE, quote);

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {id};

        int rowsAffected = db.update(TABLE_QUOTES, values, selection, selectionArgs);
        return rowsAffected > 0;
    }

    public boolean deleteQuote(String id) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {id};

        int rowsAffected = db.delete(TABLE_QUOTES, selection, selectionArgs);
        return rowsAffected > 0;
    }

    // Book operations
    public boolean addRecord(String bookName, String author, String note, String description,
                             String genre, String series, int favorite, int finished) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_BOOK_NAME, bookName);
        values.put(COLUMN_AUTHOR, author);
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_GENRE, genre);
        values.put(COLUMN_SERIES, series);
        values.put(COLUMN_FAVORITE, favorite);
        values.put(COLUMN_FINISHED, finished);

        long result = db.insert(TABLE_BOOKS, null, values);
        return result != -1;
    }

    public Cursor readAllBooks() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_BOOKS, null, null, null, null, null, COLUMN_BOOK_NAME + " ASC");
    }

    public Cursor readBy(String columnName, int value) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = columnName + " = ?";
        String[] selectionArgs = {String.valueOf(value)};
        return db.query(TABLE_BOOKS, null, selection, selectionArgs, null, null, COLUMN_BOOK_NAME + " ASC");
    }

    public Cursor readBy(String columnName, String value) {
        SQLiteDatabase db = getReadableDatabase();
        String selection = columnName + " = ?";
        String[] selectionArgs = {value};
        return db.query(TABLE_BOOKS, null, selection, selectionArgs, null, null, COLUMN_BOOK_NAME + " ASC");
    }

    public Cursor readScoreBy(String columnName, int value) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"COUNT(*)"};
        String selection = columnName + " = ?";
        String[] selectionArgs = {String.valueOf(value)};
        return db.query(TABLE_BOOKS, columns, selection, selectionArgs, null, null, null);
    }

    public Cursor getCountBooks() {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"COUNT(*)"};
        return db.query(TABLE_BOOKS, columns, null, null, null, null, null);
    }

    public Cursor readYears() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"DISTINCT " + COLUMN_YEAR};
        String selection = COLUMN_YEAR + " > 0 AND " + COLUMN_YEAR + " != ?";
        String[] selectionArgs = {String.valueOf(currentYear)};
        String orderBy = COLUMN_YEAR + " DESC";

        return db.query(TABLE_BOOKS, columns, selection, selectionArgs, null, null, orderBy);
    }

    public Cursor readCategory(String columnName) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"DISTINCT " + columnName};
        return db.query(TABLE_BOOKS, columns, null, null, null, null, columnName + " ASC");
    }

    public boolean update(String id, String bookName, String author, String note, String description,
                          String genre, String series, int favorite, int finished, String date, int year) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_BOOK_NAME, bookName);
        values.put(COLUMN_AUTHOR, author);
        values.put(COLUMN_NOTE, note);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_GENRE, genre);
        values.put(COLUMN_SERIES, series);
        values.put(COLUMN_FAVORITE, favorite);
        values.put(COLUMN_FINISHED, finished);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_YEAR, year);

        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {id};

        int rowsAffected = db.update(TABLE_BOOKS, values, selection, selectionArgs);
        return rowsAffected > 0;
    }

    public boolean delete(String id) {
        SQLiteDatabase db = getWritableDatabase();
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {id};

        int rowsAffected = db.delete(TABLE_BOOKS, selection, selectionArgs);
        return rowsAffected > 0;
    }
}