package com.androidangel.inventoryapplication;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StockAdapter extends CursorAdapter{

    private final MainActivity activity;
    public StockAdapter(MainActivity context, Cursor c) {
        super(context, c, 0);
        this.activity = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView productNameTV = view.findViewById(R.id.product_name);
        TextView productQuantityTV = view.findViewById(R.id.quantity);
        TextView productPriceTV =  view.findViewById(R.id.price);
        ImageView buyImg =  view.findViewById(R.id.buy);
        ImageView productImg = view.findViewById(R.id.image_view);

        String name = cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_NAME));
        final int quantity = cursor.getInt(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_QUANTITY));
        String price = cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_PRICE));

        productImg.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_IMAGE))));

        productNameTV.setText(name);
        productQuantityTV.setText(String.valueOf(quantity));
        productPriceTV.setText(price);

        final long id = cursor.getLong(cursor.getColumnIndex(StockContract.StockEntry._ID));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.clickOnViewItem(id);
            }
        });

        buyImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.clickOnSale(id,quantity);

            }
        });
    }
}
