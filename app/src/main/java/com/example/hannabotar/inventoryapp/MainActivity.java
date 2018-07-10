package com.example.hannabotar.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.hannabotar.inventoryapp.data.InventoryDbHelper;
import com.example.hannabotar.inventoryapp.data.ItemContract;

public class MainActivity extends AppCompatActivity {

    private InventoryDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        mDbHelper = new InventoryDbHelper(this);

        displayInventoryItems();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayInventoryItems();
    }

    private void displayInventoryItems() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] columns = {
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_PRODUCT_NAME,
                ItemContract.ItemEntry.COLUMN_PRICE,
                ItemContract.ItemEntry.COLUMN_QUANTITY,
                ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME,
                ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE
        };

        Cursor cursor = db.query(
                ItemContract.ItemEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        try {
            TextView booksView = (TextView) findViewById(R.id.text_view_items);
            int itemCount = cursor.getCount();
            booksView.setText(getString(R.string.summary, itemCount));

            if (itemCount > 0) {
                booksView.append("\n" +
                        ItemContract.ItemEntry._ID + " - " +
                        ItemContract.ItemEntry.COLUMN_PRODUCT_NAME + " - " +
                        ItemContract.ItemEntry.COLUMN_PRICE + " - " +
                        ItemContract.ItemEntry.COLUMN_QUANTITY + " - " +
                        ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME + " - " +
                        ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE + " - " +
                        "\n");

                int idIndex = cursor.getColumnIndex(ItemContract.ItemEntry._ID);
                int productNameIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME);
                int priceIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRICE);
                int quantityIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_QUANTITY);
                int supplierNameIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME);
                int supplierPhoneIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE);

                while (cursor.moveToNext()) {
                    booksView.append(
                            "\n" +
                                    cursor.getInt(idIndex) + " - " +
                                    cursor.getString(productNameIndex) + " - " +
                                    cursor.getInt(priceIndex) + " - " +
                                    cursor.getInt(quantityIndex) + " - " +
                                    cursor.getString(supplierNameIndex) + " - " +
                                    cursor.getString(supplierPhoneIndex)
                    );
                }
            }
        } finally {
            cursor.close();
        }
    }
}
