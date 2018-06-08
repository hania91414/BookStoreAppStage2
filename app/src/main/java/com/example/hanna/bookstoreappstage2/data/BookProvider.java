package com.example.hanna.bookstoreappstage2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.hanna.bookstoreappstage2.R;
import com.example.hanna.bookstoreappstage2.data.BookContract.BookEntry;

/**
 * {@link ContentProvider} for Books app.
 */
public class BookProvider extends ContentProvider {


    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 100;

    /**
     * URI matcher code for the content URI for a single book in the books table
     */
    private static final int BOOK_ID = 101;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {
        //  This URI is used to provide access to MULTIPLE rows
        // of the books table.
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);

        // This URI is used to provide access to ONE single row
        // of the books table.
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }


    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // For the BOOKS code, query the books table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the books table.
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI.
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the books table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        // If the price is provided, check that it's greater than or equal to 0 $
        Integer price = values.getAsInteger(BookEntry.COLUMN_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Producy requires valid price");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new book with the given values
        long id = database.insert(BookEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more books).
     * Return the number of rows that were successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link BookEntry#COLUMN_PRODUCT_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(BookEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(BookEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Book name field is required");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(BookEntry.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Supplier name field is required");
            }
        }

        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            String supplierNumber = values.getAsString(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (supplierNumber == null) {
                throw new IllegalArgumentException("Supplier phone number field is required");
            }
        }

        // If the {@link BookEntry#COLUMN_PRICE} key is present,
        // check that the price value is valid.
        if (values.containsKey(BookEntry.COLUMN_PRICE)) {
            // Check that the price is greater than or equal to 0
            Integer price = values.getAsInteger(BookEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Book requires valid price");
            }
        }

        // If the {@link BookEntry#COLUMN_SUPPLIER_PHONE_NUMBER} key is present,
        // check that the phone number value is valid.
        if (values.containsKey(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER)) {
            // Check that the price is greater than or equal to 0
            Integer phoneNumber = values.getAsInteger(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (phoneNumber != null && phoneNumber < 0) {
                throw new IllegalArgumentException("Supplier field requires valid phone number");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        //ADD NOTIFICATION WHEN BOOKS TABLE UPDATED
        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:

                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;

            // Delete all rows that match the selection and selection args
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}