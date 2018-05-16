package com.android.example.bookmanagementapp.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "bookmanagement.db";
    private static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_ENTRIES = "CREATE TABLE " + BookDbContract.BookDbEntry.TABLE_NAME + " ("
                + BookDbContract.BookDbEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookDbContract.BookDbEntry.COLUMN_BOOKDB_TITLE + " TEXT NOT NULL, "
                + BookDbContract.BookDbEntry.COLUMN_BOOKDV_AUTHOR + " TEXT NOT NULL, "
                + BookDbContract.BookDbEntry.COLUMN_BOOKDB_OWNERSHIP + " INTEGER NOT NULL);";

        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }
}
