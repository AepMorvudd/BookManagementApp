package com.android.example.bookmanagementapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.example.bookmanagementapp.Data.BookDbContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    EditText mBookAuthor, mBookTitle;
    RadioButton mBookOwned, mBookNotOwned;
    private Uri mCurrentBookUri;
    private static final int EXISTING_ENTRY_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        if(mCurrentBookUri == null) {
            setTitle(R.string.insertion_activity_label);
        } else {
            setTitle(R.string.editor_activity_label);
            getLoaderManager().initLoader(EXISTING_ENTRY_LOADER, null, this);
        }
        mBookAuthor = (EditText) findViewById(R.id.editr_author);
        mBookTitle = (EditText) findViewById(R.id.editor_title);
        mBookOwned = (RadioButton) findViewById(R.id.radio__i_own_this_book);
        mBookNotOwned = (RadioButton) findViewById(R.id.radio_i_do_not_own_this_book);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.editor_menu_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.editor_menu_save:
                String autorString = mBookAuthor.getText().toString().trim();
                String titleString = mBookTitle.getText().toString().trim();
                int ownership;
                if(mBookOwned.isChecked()) {
                    ownership = 1;
                } else if(mBookNotOwned.isChecked()) {
                    ownership = 2;
                } else {ownership = 0;};

                if(TextUtils.isEmpty(titleString) || TextUtils.isEmpty(autorString) || ownership == 0) {
                    Toast.makeText(this, R.string.empty_values_toast, Toast.LENGTH_LONG).show();
                } else {
                    Log.i("EditorActivity", "Checked id is: " + ownership);
                    makeEntry(titleString, autorString, ownership);
                    finish();
                }
                return true;
            case R.id.editor_menu_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String projection[] = {
                BookDbContract.BookDbEntry._ID,
                BookDbContract.BookDbEntry.COLUMN_BOOKDB_TITLE,
                BookDbContract.BookDbEntry.COLUMN_BOOKDV_AUTHOR,
                BookDbContract.BookDbEntry.COLUMN_BOOKDB_OWNERSHIP
        };
        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor == null || cursor.getCount() <1) {
            return;
        }
        if(cursor.moveToFirst()) {
            int authorColumnIndex = cursor.getColumnIndex(BookDbContract.BookDbEntry.COLUMN_BOOKDV_AUTHOR);
            int titleColumnIndex = cursor.getColumnIndex(BookDbContract.BookDbEntry.COLUMN_BOOKDB_TITLE);
            int ownershipColumnIndex = cursor.getColumnIndex(BookDbContract.BookDbEntry.COLUMN_BOOKDB_OWNERSHIP);

            String author = cursor.getString(authorColumnIndex);
            String title = cursor.getString(titleColumnIndex);
            int ownership = cursor.getInt(ownershipColumnIndex);

            mBookAuthor.setText(author);
            mBookTitle.setText(title);
            if(ownership == 1) {
                mBookOwned.setChecked(true);
            } else if(ownership == 2) {
                mBookNotOwned.setChecked(true);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mBookAuthor.setText("");
        mBookTitle.setText("");
        mBookOwned.setChecked(false);
        mBookNotOwned.setChecked(false);
    }

    private void makeEntry(String title, String author, int ownership) {
        ContentValues values = new ContentValues();
        values.put(BookDbContract.BookDbEntry.COLUMN_BOOKDB_TITLE, title);
        values.put(BookDbContract.BookDbEntry.COLUMN_BOOKDV_AUTHOR, author);
        values.put(BookDbContract.BookDbEntry.COLUMN_BOOKDB_OWNERSHIP, ownership);

        if(mCurrentBookUri == null) {
            Uri newUri = getContentResolver().insert(BookDbContract.BookDbEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.saving_error), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.entry_saved), Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.updating_error), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.entry_updated), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteEntry() {
        if (mCurrentBookUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
            if(rowsDeleted == 0) {
                Toast.makeText(this, R.string.entr_deleted_error, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.entry_deleted, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_message);

        builder.setPositiveButton(R.string.delete_dialog_confirmation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteEntry();
                finish();
            }
        });
        builder.setNegativeButton(R.string.delete_dialog_deny, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface!=null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}