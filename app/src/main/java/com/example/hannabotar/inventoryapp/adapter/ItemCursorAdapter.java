package com.example.hannabotar.inventoryapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.hannabotar.inventoryapp.R;
import com.example.hannabotar.inventoryapp.data.ItemContract;

public class ItemCursorAdapter extends CursorAdapter {

    private static final String CURRENCY = " eur";

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.inventory_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvName = (TextView) view.findViewById(R.id.item_name);
        TextView tvPrice = (TextView) view.findViewById(R.id.item_price);
        TextView tvQuantity = (TextView) view.findViewById(R.id.item_quantity);
        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME));
        Integer price = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRICE));
        Integer quantity = cursor.getInt(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_QUANTITY));
        // Populate fields with extracted properties
        tvName.setText(name);
        tvPrice.setText(String.valueOf(price) + CURRENCY);
        tvQuantity.setText(String.valueOf(quantity));
    }
}
