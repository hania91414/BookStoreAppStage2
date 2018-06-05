
package com.example.hanna.bookstoreappstage2;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.hanna.bookstoreappstage2.data.BookContract.BookEntry;

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

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
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);

        // Find the columns of book attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_QUANTITY);

        // Read the book attributes from the Cursor for the current pet
        String bookName = cursor.getString(nameColumnIndex);
        String bookQuantity = cursor.getString(quantityColumnIndex);

//        // If the pet breed is empty string or null, then use some default text
//        // that says "Unknown breed", so the TextView isn't blank. SHOW "UNKNOWN BREED"
//        if (TextUtils.isEmpty(petBreed)) {
//            petBreed = context.getString(R.string.unknown_breed);
//        }

        // Update the TextViews with the attributes for the current pet
        nameTextView.setText(bookName);
        quantityTextView.setText(bookQuantity);
    }
}