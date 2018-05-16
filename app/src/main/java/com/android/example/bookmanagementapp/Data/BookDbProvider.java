package com.android.example.bookmanagementapp.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.example.bookmanagementapp.R;

public class BookDbProvider extends ContentProvider{
    public static final String LOG_TAG = BookDbProvider.class.getSimpleName();

    private BookDbHelper mDbHelper;

    private static final int BOOK = 100;
    private static final int BOOK_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(BookDbContract.CONTENT_AUTHORITY, BookDbContract.PATH_BOOKS, BOOK);
        sUriMatcher.addURI(BookDbContract.CONTENT_AUTHORITY, BookDbContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch(match) {
            case BOOK:
                cursor = database.query(BookDbContract.BookDbEntry.TABLE_NAME, strings, s, strings1, null, null, s1);
                break;
            case BOOK_ID:
                s = BookDbContract.BookDbEntry._ID + "=?";
                strings1 = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookDbContract.BookDbEntry.TABLE_NAME, strings, s, strings1, null, null, s1);
                break;
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.cannot_query) + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case BOOK:
                return BookDbContract.BookDbEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookDbContract.BookDbEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.gettype_error1) + uri + getContext().getResources().getString(R.string.gettype_error2) + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case BOOK:
                return insertEntry(uri, contentValues);
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.insertion_not_supported) + uri);
        }
    }

    private Uri insertEntry(Uri uri, ContentValues values) {

        String title = values.getAsString(BookDbContract.BookDbEntry.COLUMN_BOOKDB_TITLE);
        if(title == null) {
            throw new IllegalArgumentException(getContext().getResources().getString(R.string.title_required));
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(BookDbContract.BookDbEntry.TABLE_NAME, null, values);
        if(id == -1) {
            Log.e(LOG_TAG,  getContext().getResources().getString(R.string.failed_to_insert) + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch(match) {
            case BOOK:
                rowsDeleted = database.delete(BookDbContract.BookDbEntry.TABLE_NAME, s, strings);
                break;
            case BOOK_ID:
                s = BookDbContract.BookDbEntry._ID + "=?";
                strings = new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookDbContract.BookDbEntry.TABLE_NAME, s, strings);
                break;
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.deletion_not_supported) + uri);
        }
        if(rowsDeleted !=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOK:
                return updateEntry(uri, contentValues, s, strings);
            case BOOK_ID:
                s = BookDbContract.BookDbEntry._ID + "=?";
                strings = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateEntry(uri, contentValues, s, strings);
            default:
                throw new IllegalArgumentException(getContext().getResources().getString(R.string.update_not_supported) + uri);
        }
    }

    private int updateEntry(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        String title = values.getAsString(BookDbContract.BookDbEntry.COLUMN_BOOKDB_TITLE);
        if(title == null) {
            throw new IllegalArgumentException(getContext().getResources().getString(R.string.title_required));
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int id = database.update(BookDbContract.BookDbEntry.TABLE_NAME, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return id;
    }
}
