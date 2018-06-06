package com.example.hanna.bookstoreappstage2;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.app.LoaderManager;
import android.content.CursorLoader;

import com.example.hanna.bookstoreappstage2.data.BookDbHelper;
import com.example.hanna.bookstoreappstage2.data.BookContract.BookEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int BOOK_LOADER = 0;

    BookCursorAdapter mCursorAdapter;

//    /** Database helper that will provide us access to the database */
//    private BookDbHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the ListView which will be populated with the book data
        ListView bookListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProdDetailsActivity.class);
                startActivity(intent);
            }
        });

        // Setup an Adapter to create a list item for each row of book data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);


        // Setup the item click listener
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(MainActivity.this, ProdDetailsActivity.class);

                // Form the content URI that represents the specific book that was clicked on,
                // by appending the "id"
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentBookUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

//        // To access our database, we instantiate our subclass of SQLiteOpenHelper
//        // and pass the context, which is the current activity.
//        mDbHelper = new BookDbHelper(this);

//        addDummyDataButton = findViewById(R.id.button);
//        addDummyDataButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                displayDatabaseInfo();
//            }
//        });
//    }

    /**
     * Helper method to insert hardcoded data into the database. For debugging purposes only.
     */
    private void insertBook() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, "Gone with the wind");
        values.put(BookEntry.COLUMN_PRICE, 10);
        values.put(BookEntry.COLUMN_QUANTITY, 2);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, "Best Supplier");
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, 76343535);

        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }


    /**
     * Helper method to delete all books in the database.
     */
    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + " rows deleted from book database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.insert_dummy_data:
                insertBook();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_books:
                // Delete all pets
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRICE,
                BookEntry.COLUMN_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                BookEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link PetCursorAdapter} with this new cursor containing updated pet data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

//    private void displayDatabaseInfo() {
//        // Create and/or open a database to read from it
//        SQLiteDatabase db = mDbHelper.getReadableDatabase();
//
//        // Define a projection that specifies which columns from the database
//        // you will actually use after this query.
//        String[] projection = {
//                BookEntry._ID,
//                BookEntry.COLUMN_PRODUCT_NAME,
//                BookEntry.COLUMN_PRICE,
//                BookEntry.COLUMN_QUANTITY,
//                BookEntry.COLUMN_SUPPLIER_NAME,
//                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};
//
//        // Perform a query on the books table
//        Cursor cursor = db.query(
//                BookEntry.TABLE_NAME,   // The table to query
//                projection,            // The columns to return
//                null,                  // The columns for the WHERE clause
//                null,                  // The values for the WHERE clause
//                null,
//                null,                      // Don't group the rows
//                null,                  // Don't filter by row groups
//                null);                   // The sort order
//
//
//        TextView displayView = findViewById(R.id.show_items_text_view);
//
//        try {
//            // Create a header in the Text View
//            // In the while loop below, iterate through the rows of the cursor and display
//            // the information from each column in this order.
//            displayView.setText("The book table contains " + cursor.getCount() + " books.\n\n");
//            displayView.append(BookEntry._ID + " - " +
//                    BookEntry.COLUMN_PRODUCT_NAME + " - " +
//                    BookEntry.COLUMN_PRICE + " - " +
//                    BookEntry.COLUMN_QUANTITY + " - " +
//                    BookEntry.COLUMN_SUPPLIER_NAME + " - " +
//                    BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + "\n");
//
//            // Figure out the index of each column
//            int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
//            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
//            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);
//            int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
//            int supplierColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
//            int phoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
//
//            // Iterate through all the returned rows in the cursor
//            while (cursor.moveToNext()) {
//                // Use that index to extract the String or Int value of the word
//                // at the current row the cursor is on.
//                int currentID = cursor.getInt(idColumnIndex);
//                String currentName = cursor.getString(nameColumnIndex);
//                int currentPrice = cursor.getInt(priceColumnIndex);
//                int currentQuantity = cursor.getInt(quantityColumnIndex);
//                String currentSupplier = cursor.getString(supplierColumnIndex);
//                int currentPhoneNumber = cursor.getInt(phoneColumnIndex);
//                // Display the values from each column of the current row in the cursor in the TextView
//                displayView.append(("\n" + currentID + " - " +
//                        currentName + " - " +
//                        currentPrice + " - " +
//                        currentQuantity + " - " +
//                        currentSupplier + " - " +
//                        currentPhoneNumber));
//            }
//        } finally {
//            // Always close the cursor when you're done reading from it. This releases all its
//            // resources and makes it invalid.
//            cursor.close();
//        }
//    }


}
