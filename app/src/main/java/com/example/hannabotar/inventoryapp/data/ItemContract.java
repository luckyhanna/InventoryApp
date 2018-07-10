package com.example.hannabotar.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Created by hanna on 7/10/2018.
 */

public final class ItemContract {

    public static abstract class ItemEntry implements BaseColumns {

        public static final String TABLE_NAME = "items";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "product_name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";

        public static final String SQL_CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                COLUMN_PRICE + " INTEGER NOT NULL, " +
                COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " +
                COLUMN_SUPPLIER_PHONE + " TEXT);";

    }

}
