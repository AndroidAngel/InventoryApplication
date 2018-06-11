package com.androidangel.inventoryapplication;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    private final static String LOG_TAG = MainActivity.class.getCanonicalName();
    StockDbHelper dbHelper;
    StockAdapter adapter;
    int lastVisibleItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new StockDbHelper(this);

        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

        final ListView listView = findViewById(R.id.listView_v);
        View emptyView = findViewById(R.id.empty_v);
        listView.setEmptyView(emptyView);

        Cursor cursor = dbHelper.readStock();

        adapter = new StockAdapter(this, cursor);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == 0) return;
                final int currentFirstVisibleItem = view.getFirstVisiblePosition();
                if (currentFirstVisibleItem > lastVisibleItem) {
                    fab.show();
                } else if (currentFirstVisibleItem < lastVisibleItem) {
                    fab.hide();
                }
                lastVisibleItem = currentFirstVisibleItem;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.swapCursor(dbHelper.readStock());
    }

    public void clickOnViewItem(long id) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("itemId", id);
        startActivity(intent);
    }

    public void clickOnSale(long id, int quantity) {
        dbHelper.sellOneItem(id, quantity);
        adapter.swapCursor(dbHelper.readStock());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dummmy_data:
                addDummyData();
                adapter.swapCursor(dbHelper.readStock());
        }
        return super.onOptionsItemSelected(item);
    }
    private void addDummyData() {

        Stock adoboDummy =  new Stock(
                "Chicken Adobo",
                "₱ 50",
                25,
                "Nena\'s Filipino Delicacy",
                "+02 526 1234",
                "nenafoodhouse@gmail.com",
                "android.resource://com.androidangel.inventoryapplication/drawable/adobo_dummy");
        dbHelper.insertItem(adoboDummy);

        Stock arrozCaldoDummy = new Stock(
                "Arroz Caldo",
                "₱ 20",
                16,
                "Lugawan sa Kanto",
                "+02 123 4567",
                "lugawansakanto@gmail.com",
                "android.resource://com.androidangel.inventoryapplication/drawable/arroz_caldo_dummy");
        dbHelper.insertItem(arrozCaldoDummy);

        Stock balotDummy = new Stock(
                "Balot",
                "₱ 15",
                50,
                "Balot House",
                "+02 012 5678",
                "balothouse@gmail.com",
                "android.resource://com.androidangel.inventoryapplication/drawable/balot_dummy");
        dbHelper.insertItem(balotDummy);


        Stock bulaloDummy = new Stock(
                "Bulalo",
                "₱ 120",
                15,
                "Jerry\'s Bulalohan",
                "+639 123 4567 89",
                "jerrysbulalohan@gmail.com",
                "android.resource://com.androidangel.inventoryapplication/drawable/bulalo_dummy");
        dbHelper.insertItem(bulaloDummy);

        Stock pinakbetDummy = new Stock(
                "Pinakbet",
                "₱ 30",
                18,
                "Nena\'s Filipino Delicacy",
                "+02 526 1234",
                "nenafoodhouse@gmail.com",
                "android.resource://com.androidangel.inventoryapplication/drawable/pinakbet_dummy");
        dbHelper.insertItem(pinakbetDummy);

        Stock sinigangDummy = new Stock(
                "Sinigang na Hipon",
                "₱ 50",
                20,
                "Nena\'s Filipino Delicacy",
                "+02 526 1234",
                "nenafoodhouse@gmail.com",
                "android.resource://com.androidangel.inventoryapplication/drawable/sinigang_dummy");
        dbHelper.insertItem(sinigangDummy);
    }
}
