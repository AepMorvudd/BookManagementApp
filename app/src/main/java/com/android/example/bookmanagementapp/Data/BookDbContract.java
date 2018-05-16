package com.android.example.bookmanagementapp.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class BookDbContract {

    private BookDbContract() { }

    public static final String CONTENT_AUTHORITY = "com.android.example.bookmanagementapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "book";

    public static class BookDbEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String TABLE_NAME = "bookdb";
        public static final String _ID = BaseColumns._ID;

        //Column names
        public static final String COLUMN_BOOKDB_TITLE = "title";
        public static final String COLUMN_BOOKDV_AUTHOR = "author";
        public static final String COLUMN_BOOKDB_OWNERSHIP = "ownership";
    }
}
