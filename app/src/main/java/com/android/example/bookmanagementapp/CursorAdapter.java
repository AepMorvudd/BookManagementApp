package com.android.example.bookmanagementapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.example.bookmanagementapp.Data.BookDbContract;

public class CursorAdapter extends android.widget.CursorAdapter {

    public CursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.item_detail, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView displayTitle = (TextView) view.findViewById(R.id.book_title);
        TextView displayAuthor = (TextView) view.findViewById(R.id.book_author);

        String title = cursor.getString(cursor.getColumnIndex(BookDbContract.BookDbEntry.COLUMN_BOOKDB_TITLE));
        String author = cursor.getString(cursor.getColumnIndex(BookDbContract.BookDbEntry.COLUMN_BOOKDV_AUTHOR));

        displayTitle.setText(title);
        displayAuthor.setText(author);
    }
}
