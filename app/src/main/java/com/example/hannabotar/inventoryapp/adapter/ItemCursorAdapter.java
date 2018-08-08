package com.example.hannabotar.inventoryapp.adapter;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.hannabotar.inventoryapp.EditActivity;
import com.example.hannabotar.inventoryapp.MainActivity;
import com.example.hannabotar.inventoryapp.R;
import com.example.hannabotar.inventoryapp.data.ItemContract;

public class ItemCursorAdapter extends CursorAdapter implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String CURRENCY = " eur";
    private static final int DB_ITEM_LOADER_ID = 1;

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.inventory_item, parent, false);
    }

    @Override
    public void bindView(final View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvName = (TextView) view.findViewById(R.id.item_name);
        TextView tvPrice = (TextView) view.findViewById(R.id.item_price);
        TextView tvQuantity = (TextView) view.findViewById(R.id.item_quantity);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);

        final long id = cursor.getLong(cursor.getColumnIndex(ItemContract.ItemEntry._ID));

        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME));
        Integer price = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRICE));
        final Integer quantity = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_QUANTITY));

        final ContentResolver contentResolver = context.getContentResolver();

        if (quantity > 0) {
            saleButton.setEnabled(true);
            saleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ContentValues item = new ContentValues();
                    item.put(ItemContract.ItemEntry.COLUMN_QUANTITY, quantity - 1);
                    contentResolver.update(ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, id), item, null, null);
                }
            });
        } else {
            saleButton.setEnabled(false);
        }

        // Populate fields with extracted properties
        tvName.setText(name);
        tvPrice.setText(String.valueOf(price) + CURRENCY);
        tvQuantity.setText(String.valueOf(quantity));

        // Setup item click listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(view.getContext(), EditActivity.class);
                edit.setData(ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, id));
                view.getContext().startActivity(edit);
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
