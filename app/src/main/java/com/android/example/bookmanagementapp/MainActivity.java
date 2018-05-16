package com.android.example.bookmanagementapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.example.bookmanagementapp.Data.BookDbContract;
import com.android.example.bookmanagementapp.Data.BookDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    CursorAdapter mCurstorAdapter;
    private static final int BOOK_LOADER = 0;
    BookDbHelper mDbHelper = new BookDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        ListView itemListYouOwn = (ListView) findViewById(R.id.listViewId);
        mCurstorAdapter = new CursorAdapter(this, null);
        mCurstorAdapter.getFilterQueryProvider();
        itemListYouOwn.setAdapter(mCurstorAdapter);
        getLoaderManager().initLoader(BOOK_LOADER, null, this);

        itemListYouOwn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity. this, EditorActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookDbContract.BookDbEntry.CONTENT_URI, l);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookDbContract.BookDbEntry._ID,
                BookDbContract.BookDbEntry.COLUMN_BOOKDB_TITLE,
                BookDbContract.BookDbEntry.COLUMN_BOOKDV_AUTHOR,
                BookDbContract.BookDbEntry.COLUMN_BOOKDB_OWNERSHIP
        };

        CursorLoader cursor = new CursorLoader(this,
                BookDbContract.BookDbEntry.CONTENT_URI,
                projection,
                null, null, null);
        return cursor;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCurstorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCurstorAdapter.swapCursor(null);
    }
}

// TODO 1. Dodanie funkcjonalności do sortowania książaek na podstawie informacji, czy daną publikację uzytkownik posiada, czy nie
// TODO 1.2. Ddanie funkocjnalności, gdzie uzytkownik będzie mógł zaznaczyć, które akurat pozycje czyta (będą one potem wyświetlały się na górze widoku)
// TODO 2. Dodanie funkcjonalności, gdzie użytkonik może zaznaczyć, czy daną książkę przeczytał, czy też nie

// TODO Dodanie funkcjonalności, która tworzy modal, przy wyświetlaniu informacji o publikacji, która już istnieje w DB, a dopiero po klieknięciu w przycisk "edytuj" przenosi uzytkownika do EdfitorActivity
// TODO Dodanie menu, gdzie użytkownik może wybrać język aplikacj (Polski i Angielski)