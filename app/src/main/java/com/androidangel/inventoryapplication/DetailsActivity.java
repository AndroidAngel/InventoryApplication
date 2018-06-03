package com.androidangel.inventoryapplication;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class DetailsActivity extends AppCompatActivity{
    private static final String LOG_TAG = DetailsActivity.class.getCanonicalName();
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private StockDbHelper stockDbHelper;
    EditText productNameEdit;
    EditText priceEdit;
    EditText productQuantityEdit;
    EditText supplierNameEdit;
    EditText supplierPhoneEdit;
    EditText supplierEmailEdit;
    ImageButton increaseBtn;
    ImageButton decreaseBtn;
    Button galleryBtn;
    Button cameraBtn;
    ImageView productImgV;

    Uri theUri;
    private static final int IMAGE_REQUEST = 0;
    boolean itemHasChanged = false;
    long currentItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        productNameEdit = findViewById(R.id.product_name_edit);
        priceEdit = findViewById(R.id.price_edit);
        productQuantityEdit = findViewById(R.id.quantity_edit);
        supplierNameEdit = findViewById(R.id.supplier_name_edit);
        supplierPhoneEdit = findViewById(R.id.supplier_phone_edit);
        supplierEmailEdit = findViewById(R.id.supplier_email_edit);
        increaseBtn = findViewById(R.id.plus_btn);
        decreaseBtn = findViewById(R.id.minus_btn);
        galleryBtn = findViewById(R.id.gallery_btn);
        cameraBtn = findViewById(R.id.camera_btn);
        productImgV = findViewById(R.id.image_view);

        stockDbHelper = new StockDbHelper(this);
        currentItemId = getIntent().getLongExtra("itemId", 0);

        if (currentItemId == 0){
            setTitle(getString(R.string.activity_title_new_item));
        }else {
            setTitle(getString(R.string.activity_title_edit_item));
            addItemToEditItem(currentItemId);
        }
        increaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plusOneItemInQuantity();
                itemHasChanged = true;
            }
        });
        decreaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                minusOneItemInQuantity();
                itemHasChanged = true;
            }
        });
        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryToOpenGalleryForImage();
                itemHasChanged = true;
            }
        });
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraForImage();
                itemHasChanged = true;
            }


        });
    }
    @Override
    public void onBackPressed(){
        if (!itemHasChanged){
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener backButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                finish();
            }
        };
        showUnsavedChangesDialog(backButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener backButtonClickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R .string.unsaved_changes);
        alertDialogBuilder.setPositiveButton(R.string.discard, backButtonClickListener);
        alertDialogBuilder.setNegativeButton(R.string.continue_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void plusOneItemInQuantity() {
        String prevValuesString = productQuantityEdit.getText().toString();
        int prevValue;
        if (prevValuesString.isEmpty()){
            prevValue = 0;
        }else {
            prevValue = Integer.parseInt(prevValuesString);
        }
        productQuantityEdit.setText(String.valueOf(prevValue + 1));

    }
    private void  minusOneItemInQuantity() {
        String prevValuesString = productQuantityEdit.getText().toString();
        int prevValue;
        if (prevValuesString.isEmpty()){
            return;
        }else if(prevValuesString.equals("0")){
            return;
        }else{
            prevValue = Integer.parseInt(prevValuesString);
            productQuantityEdit.setText(String.valueOf(prevValue - 1));
        }
    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_details, menu);
            return true;
        }

        @Override
        public boolean onPrepareOptionsMenu(Menu menu) {
            super.onPrepareOptionsMenu(menu);
            if (currentItemId == 0){
                MenuItem deleteOneItemInMenuItem = menu.findItem(R.id.delete);
                MenuItem deleteAllItemInMenuItem = menu.findItem(R.id.delete_all);
                MenuItem orderItemInMenuItem = menu.findItem(R.id.order);
                deleteOneItemInMenuItem.setVisible(false);
                deleteAllItemInMenuItem.setVisible(false);
                orderItemInMenuItem.setVisible(false);
            }
            return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.save:
                if (!addNewItemToDb()){
                    return true;
                }
                finish();
                return true;
            case android.R.id.home:
                if (!itemHasChanged){
                     NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                DialogInterface.OnClickListener backButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };
                showUnsavedChangesDialog(backButtonClickListener);
                return true;
            case R.id.order:
                showOrderConfirmation();
                return true;

            case R.id.delete:
                showDeleteConfirmation(currentItemId);
                return true;

            case R.id.delete_all:
                showDeleteConfirmation(0);
                return true;

        }
        return super.onOptionsItemSelected(menuItem);

    }

    private boolean addNewItemToDb() {
        boolean isOk = true;
        if (!checkIfItemSet(productNameEdit,"name")) {
            isOk = false;
        }
        if (!checkIfItemSet(priceEdit,"price")) {
            isOk = false;
        }
        if (!checkIfItemSet(productQuantityEdit,"quantity")) {
            isOk = false;
        }
        if (!checkIfItemSet(supplierNameEdit,"supplier name")) {
            isOk = false;
        }
        if (!checkIfItemSet(supplierPhoneEdit,"supplier phone")) {
            isOk = false;
        }
        if (!checkIfItemSet(supplierEmailEdit,"supplier email")) {
            isOk = false;
        }
        if (theUri == null && currentItemId == 0) {
            isOk = false;
            galleryBtn.setError("Missing image");
        }
        if (!isOk){
            return false;
        }
        if (currentItemId == 0) {
            Stock stock = new Stock(
                    productNameEdit.getText().toString().trim(),
                    priceEdit.getText().toString().trim(),
                    Integer.parseInt(productQuantityEdit.getText().toString().trim()),
                    supplierNameEdit.getText().toString().trim(),
                    supplierPhoneEdit.getText().toString().trim(),
                    supplierEmailEdit.getText().toString().trim(),
                    theUri.toString());
            stockDbHelper.insertItem(stock);
        }else{
            int quantity = Integer.parseInt(productQuantityEdit.getText().toString().trim());
            stockDbHelper.updateItem(currentItemId, quantity);
        }
        return true;
    }

    private boolean checkIfItemSet(EditText text, String description) {
        if (TextUtils.isEmpty(text.getText())){
            text.setError("Missing Product " + description);
            return false;
        }else {
            text.setError(null);
            return true;
        }
    }

    private void addItemToEditItem(long itemId) {
        Cursor cursor = stockDbHelper.readItem(itemId);
        cursor.moveToFirst();
        productNameEdit.setText(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_NAME)));
        priceEdit.setText(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_PRICE)));
        productQuantityEdit.setText(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_QUANTITY)));
        supplierNameEdit.setText(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_SUPPLIER_NAME)));
        supplierPhoneEdit.setText(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_SUPPLIER_PHONE)));
        supplierEmailEdit.setText(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_SUPPLIER_EMAIL)));
        productImgV.setImageURI(Uri.parse(cursor.getString(cursor.getColumnIndex(StockContract.StockEntry.COLUMN_IMAGE))));
        productNameEdit.setEnabled(false);
        priceEdit.setEnabled(false);
        galleryBtn.setEnabled(false);
        supplierNameEdit.setEnabled(false);
        supplierPhoneEdit.setEnabled(false);
        supplierEmailEdit.setEnabled(false);
    }
    private void showOrderConfirmation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.order_message);

        alertDialogBuilder.setPositiveButton(R.string.phone, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent phoneIntent = new Intent(android.content.Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:" + supplierPhoneEdit.getText().toString().trim()));
                startActivity(phoneIntent);
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.email, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setType("text/plain");
                emailIntent.setData(Uri.parse("mailto:" + supplierEmailEdit.getText().toString().trim()));
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Update new order");
                String bodyMessage = "Kindly send us as soon as possible of " + productNameEdit.getText().toString().trim()
                        + "!";
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
                startActivity(emailIntent);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private int deleteAllFromTable(){
        SQLiteDatabase sqLiteDatabase = stockDbHelper.getWritableDatabase();
        return sqLiteDatabase.delete(StockContract.StockEntry.TABLE_NAME, null,null);
    }
    private int  deleteOneItemFromTable(long itemId){
        SQLiteDatabase sqLiteDatabase = stockDbHelper.getWritableDatabase();
        String select = StockContract.StockEntry._ID + "=?";
        String[] selectArgs = {String.valueOf(itemId)};
        int rowDeleted = sqLiteDatabase.delete(StockContract.StockEntry.TABLE_NAME, select, selectArgs);
        return rowDeleted;
    }
    private void showDeleteConfirmation(final long itemId){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.delete_message);
        alertDialogBuilder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (itemId == 0){
                    deleteAllFromTable();
                }else {
                    deleteOneItemFromTable(itemId);
                }
                finish();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
    public void tryToOpenGalleryForImage(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        openGalleryForImage();
    }
    private void openGalleryForImage(){
        Intent galleryIntent;
        if (Build.VERSION.SDK_INT < 19){
            galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        }else{
            galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        galleryIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(galleryIntent,"Select Picture"), IMAGE_REQUEST);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode){
            case PERMISSION_REQUEST_READ_EXTERNAL_STORAGE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    openGalleryForImage();
                }
            }
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData){
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK){
            if (resultData != null){
                theUri = resultData.getData();
                productImgV.setImageURI(theUri);
                productImgV.invalidate();

            }
        }
    }
        private void openCameraForImage() {
}
}
