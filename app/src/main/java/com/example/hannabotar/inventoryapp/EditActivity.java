package com.example.hannabotar.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.hannabotar.inventoryapp.data.ItemContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.edit_product_name)
    EditText mProductName;
    @BindView(R.id.edit_price)
    EditText mPrice;
    @BindView(R.id.edit_quantity)
    EditText mQuantity;
    @BindView(R.id.edit_supplier_name)
    EditText mSupplierName;
    @BindView(R.id.edit_supplier_phone)
    EditText mSupplierPhone;

    private static final int DB_ITEM_LOADER_ID = 1;

    private Uri mCurrentItemUri;

    private boolean mItemHasChanged = false;

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
    // the view, and we change the mItemHasChanged boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    private static final String[] PROJECTION = {
            ItemContract.ItemEntry._ID,
            ItemContract.ItemEntry.COLUMN_PRODUCT_NAME,
            ItemContract.ItemEntry.COLUMN_PRICE,
            ItemContract.ItemEntry.COLUMN_QUANTITY,
            ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME,
            ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();
        if (mCurrentItemUri != null) {
            setTitle(R.string.title_edit_inventory_item);
            getLoaderManager().initLoader(DB_ITEM_LOADER_ID, null, this);
        } else {
            setTitle(R.string.title_new_inventory_item);

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        }

        mProductName.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mQuantity.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierPhone.setOnTouchListener(mTouchListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the item hasn't changed, continue with handling back button press
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_item_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save item to DB
                if (saveItem()) {
                    // close the editor activity and go back to the initial one
                    finish();
                    return true;
                }
                return false;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Show delete confirmation
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the item hasn't changed, continue with navigating up to parent activity
                // which is the {@link InventoryActivity}.
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean saveItem() {
        String productName = mProductName.getText().toString().trim();
        if (TextUtils.isEmpty(productName)) {
            Toast.makeText(this, getString(R.string.errorProductName), Toast.LENGTH_SHORT).show();
            return false;
        }
        int price = 0;
        String priceString = mPrice.getText().toString();
        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, getString(R.string.errorPrice), Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            price = Integer.parseInt(priceString);
        } catch (NumberFormatException ex) {
            Toast.makeText(this, getString(R.string.errorPrice), Toast.LENGTH_SHORT).show();
            return false;
        }
        int quantity = 0;
        String quantityString = mQuantity.getText().toString();
        if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, getString(R.string.errorQuantity), Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            quantity = Integer.parseInt(quantityString);
        } catch (NumberFormatException ex) {
            Toast.makeText(this, getString(R.string.errorQuantity), Toast.LENGTH_SHORT).show();
            return false;
        }
        String supplierName = mSupplierName.getText().toString().trim();
        if (TextUtils.isEmpty(supplierName)) {
            Toast.makeText(this, getString(R.string.errorSupplierName), Toast.LENGTH_SHORT).show();
            return false;
        }
        String supplierPhone = mSupplierPhone.getText().toString().trim();

        ContentValues item = new ContentValues();
        item.put(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME, productName);
        item.put(ItemContract.ItemEntry.COLUMN_PRICE, price);
        item.put(ItemContract.ItemEntry.COLUMN_QUANTITY, quantity);
        item.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME, supplierName);
        item.put(ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);

        if (mCurrentItemUri == null) {
            // insert item
            Uri uri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, item);
            if (uri == null) {
                Toast.makeText(this, getString(R.string.errorSave), Toast.LENGTH_SHORT).show();
                return false;
            } else {
                long savedId = ContentUris.parseId(uri);
                Toast.makeText(this, getString(R.string.successSave, savedId), Toast.LENGTH_SHORT).show();
                return true;
            }
        } else {
            // update item
            long updatedRows = getContentResolver().update(mCurrentItemUri, item, null, null);
            if (updatedRows == 0) {
                Toast.makeText(this, getString(R.string.errorUpdate), Toast.LENGTH_SHORT).show();
                return false;
            } else {
                long updatedId = ContentUris.parseId(mCurrentItemUri);
                Toast.makeText(this, getString(R.string.successUpdate), Toast.LENGTH_SHORT).show();
                return true;
            }

        }

    }

    private void deleteItem() {
        if (mCurrentItemUri == null) {
            return;
        } else {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.errorDelete), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.successDelete), Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mCurrentItemUri != null) {
            return new CursorLoader(this, mCurrentItemUri, PROJECTION, null, null, null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {
            String name = data.getString(data.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRODUCT_NAME));
            mProductName.setText(name);

            Integer price = data.getInt(data.getColumnIndex(ItemContract.ItemEntry.COLUMN_PRICE));
            mPrice.setText(String.valueOf(price));

            Integer quantity = data.getInt(data.getColumnIndex(ItemContract.ItemEntry.COLUMN_QUANTITY));
            mQuantity.setText(String.valueOf(quantity));

            String supplierName = data.getString(data.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_SUPPLIER_NAME));
            mSupplierName.setText(supplierName);

            String supplierPhone = data.getString(data.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_SUPPLIER_PHONE));
            mSupplierPhone.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductName.setText("");
        mPrice.setText("");
        mQuantity.setText("");
        mSupplierName.setText("");
        mSupplierPhone.setText("");
    }
}
