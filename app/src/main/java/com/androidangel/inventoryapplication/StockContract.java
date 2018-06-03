package com.androidangel.inventoryapplication;

import android.provider.BaseColumns;

public class StockContract {
    public final static int DB_VERSION = 1;
    public final static String DB_NAME = "inventory.db";

    public StockContract() {
    }

    public static final class StockEntry implements BaseColumns {

        public static final String TABLE_NAME = "stock";

        public static final String _ID = BaseColumns._ID;

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_SUPPLIER_PHONE = "supplier_phone";
        public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";
        public static final String COLUMN_IMAGE = "image";


        public static final String CREATE_TABLE_STOCK = "CREATE TABLE " +
                StockEntry.TABLE_NAME + "(" +
                StockEntry._ID + " INTEGER PRIMARY KEY," +
                StockEntry.COLUMN_NAME + " TEXT NOT NULL," +
                StockEntry.COLUMN_PRICE + " TEXT NOT NULL," +
                StockEntry.COLUMN_QUANTITY + " TEXT NOT NULL," +
                StockEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL," +
                StockEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL," +
                StockEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL," +
                StockEntry.COLUMN_IMAGE + " TEXT NOT NULL" + ");";

    }
}
