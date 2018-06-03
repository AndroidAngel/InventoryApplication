package com.androidangel.inventoryapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StockDbHelper extends SQLiteOpenHelper {


    public final static String LOG_TAG = StockDbHelper.class.getCanonicalName();

    public StockDbHelper(Context context) {
        super(context, StockContract.DB_NAME, null,StockContract.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(StockContract.StockEntry.CREATE_TABLE_STOCK);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db,int oldVersion, int newVersion){

    }
    public void insertItem(Stock item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(StockContract.StockEntry.COLUMN_NAME, item.getProductName());
        contentValues.put(StockContract.StockEntry.COLUMN_PRICE, item.getPrice());
        contentValues.put(StockContract.StockEntry.COLUMN_QUANTITY, item.getQuantity());
        contentValues.put(StockContract.StockEntry.COLUMN_SUPPLIER_NAME, item.getSupplierName());
        contentValues.put(StockContract.StockEntry.COLUMN_SUPPLIER_PHONE, item.getSupplierPhone());
        contentValues.put(StockContract.StockEntry.COLUMN_SUPPLIER_EMAIL, item.getSupplierEmail());
        contentValues.put(StockContract.StockEntry.COLUMN_IMAGE, item.getImage());
        long id = db.insert(StockContract.StockEntry.TABLE_NAME,null, contentValues);

    }
    public Cursor readStock(){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = new String[]{
                StockContract.StockEntry._ID,
                StockContract.StockEntry.COLUMN_NAME,
                StockContract.StockEntry.COLUMN_PRICE,
                StockContract.StockEntry.COLUMN_QUANTITY,
                StockContract.StockEntry.COLUMN_SUPPLIER_NAME,
                StockContract.StockEntry.COLUMN_SUPPLIER_PHONE,
                StockContract.StockEntry.COLUMN_SUPPLIER_EMAIL,
                StockContract.StockEntry.COLUMN_IMAGE,
        };
        Cursor cursor = db.query(
                StockContract.StockEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        return cursor;
    }
    public Cursor readItem(long itemId){
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                StockContract.StockEntry._ID,
                StockContract.StockEntry.COLUMN_NAME,
                StockContract.StockEntry.COLUMN_PRICE,
                StockContract.StockEntry.COLUMN_QUANTITY,
                StockContract.StockEntry.COLUMN_SUPPLIER_NAME,
                StockContract.StockEntry.COLUMN_SUPPLIER_PHONE,
                StockContract.StockEntry.COLUMN_SUPPLIER_EMAIL,
                StockContract.StockEntry.COLUMN_IMAGE,

        };
        String select = StockContract.StockEntry._ID + "=?";
        String[] selectArgs = new String[] { String.valueOf(itemId)};

        Cursor cursor = db.query(
                StockContract.StockEntry.TABLE_NAME,
                projection,
                select,
                selectArgs,
                null,
                null,
                null
        );
        return cursor;
    }
    public void updateItem(long currentItemId, int quantity){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(StockContract.StockEntry.COLUMN_QUANTITY, quantity);
        String select = StockContract.StockEntry._ID + "=?";
        String[] selectArgs = new String[]{String.valueOf(currentItemId)};
        db.update(StockContract.StockEntry.TABLE_NAME, contentValues, select, selectArgs);
    }
    public void sellItem(long itemId, int quantity){
        SQLiteDatabase db = getWritableDatabase();
        int newItemQuantity = 0;
        if (quantity > 0 ){
            newItemQuantity = quantity -1;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(StockContract.StockEntry.COLUMN_QUANTITY, newItemQuantity);
        String select = StockContract.StockEntry._ID + "=?";
        String[] selectArgs = new String[]{ String.valueOf(itemId)};
        db.update(StockContract.StockEntry.TABLE_NAME,
        contentValues, select, selectArgs);
    }
}
