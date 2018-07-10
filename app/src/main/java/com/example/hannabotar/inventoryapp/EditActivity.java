package com.example.hannabotar.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hannabotar.inventoryapp.data.InventoryDbHelper;
import com.example.hannabotar.inventoryapp.data.ItemContract;

public class EditActivity extends AppCompatActivity {

    private EditText mProductName;
    private EditText mPrice;
    private EditText mQuantity;
    private EditText mSupplierName;
    private EditText mSupplierPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        mProductName = (EditText) findViewById(R.id.edit_product_name);
        mPrice = (EditText) findViewById(R.id.edit_price);
        mQuantity = (EditText) findViewById(R.id.edit_quantity);
        mSupplierName = (EditText) findViewById(R.id.edit_supplier_name);
        mSupplierPhone = (EditText) findViewById(R.id.edit_supplier_phone);
    }

    private boolean insertItem() {
        String productName = mProductName.getText().toString().trim();
        if (productName.isEmpty()) {
            Toast.makeText(this, getString(R.string.errorProductName), Toast.LENGTH_SHORT).show();
            return false;
        }
        int price = 0;
        try {
            price = Integer.parseInt(mPrice.getText().toString());
        } catch (NumberFormatException ex) {
            Toast.makeText(this, getString(R.string.errorPrice), Toast.LENGTH_SHORT).show();
            return false;
        }
        int quantity = 0;
        try {
            quantity = Integer.parseInt(mQuantity.getText().toString());
        } catch (NumberFormatException ex) {
            Toast.makeText(this, getString(R.string.errorQuantity), Toast.LENGTH_SHORT).show();
            return false;
        }
        String supplierName = mSupplierName.getText().toString().trim();
        if (supplierName.isEmpty()) {
            Toast.makeText(this, getString(R.string.errorSupplierName), Toast.LENGTH_SHORT).show();
            return false;
        }
        String supplierPhone = mSupplierPhone.getText().toString().trim();

        ContentValues newItem = new ContentValues();
        newItem.put(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME, productName);
        newItem.put(ItemContract.ItemEntry.COLUMN_PRICE, price);
        newItem.put(ItemContract.ItemEntry.COLUMN_QUANTITY, quantity);
        newItem.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME, supplierName);
        newItem.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);

        InventoryDbHelper mDbHelper = new InventoryDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long newItemId = db.insert(ItemContract.ItemEntry.TABLE_NAME, null, newItem);
        if (newItemId == -1) {
            Toast.makeText(this, getString(R.string.errorSave), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(this, getString(R.string.successSave), Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (insertItem()) {
                    finish();
                    return true;
                }
                return false;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
