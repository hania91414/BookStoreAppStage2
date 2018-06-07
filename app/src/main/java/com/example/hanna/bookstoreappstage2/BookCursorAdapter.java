
package com.example.hanna.bookstoreappstage2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hanna.bookstoreappstage2.data.BookContract.BookEntry;
import com.example.hanna.bookstoreappstage2.data.BookDbHelper;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    int numberQuantity;

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);

        // Find the columns of book attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRICE);

        int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
        int id = cursor.getInt(idColumnIndex);
        final Uri contentUri = Uri.withAppendedPath(BookEntry.CONTENT_URI, Integer.toString(id));

        Button sale = (Button) view.findViewById(R.id.sale);
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantityInt = Integer.valueOf(quantityTextView.getText().toString());

                if (quantityInt == 0) {
                    Toast.makeText(context, R.string.quantity_lower_than_zero, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    quantityInt--;
                }
                ContentValues values = new ContentValues();
                values.put(BookEntry.COLUMN_QUANTITY, quantityInt);
                context.getContentResolver().update(contentUri, values, null, null);
            }
        });

        // Read the book attributes from the Cursor for the current pet
        String bookName = cursor.getString(nameColumnIndex);
        String bookQuantity = cursor.getString(quantityColumnIndex);
        String priceQuantity = cursor.getString(priceColumnIndex);

//        // If the pet breed is empty string or null, then use some default text
//        // that says "Unknown breed", so the TextView isn't blank. SHOW "UNKNOWN BREED"
//        if (TextUtils.isEmpty(petBreed)) {
//            petBreed = context.getString(R.string.unknown_breed);
//        }

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(bookName);
        quantityTextView.setText(bookQuantity);
        priceTextView.setText(priceQuantity);
    }
}