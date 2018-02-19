package com.example.android.ward.activities;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.ward.R;
import com.example.android.ward.data.WardContract.ProductEntry;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int VIEW_PRODUCT_LOADER = 2;
    private Uri currentProductUri;

    private TextView nameTextView;
    private TextView quantityTextView;
    private Button decreaseQuantityButton;
    private Button increaseQuantityButton;
    private TextView totalTextView;
    private TextView supplierNameTextView;
    private ImageButton emailSupplierImageButton;
    private TextView supplierEmailTextView;
    private ImageButton callSupplierImageButton;
    private TextView supplierPhoneNumberTextView;

    private String productName;
    private int quantity;
    private int pricePerProduct;
    private int total;
    private String supplierName;
    private String supplierEmail;
    private String supplierPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        currentProductUri = getIntent().getData();
        //Kick off the loader.
        getLoaderManager().initLoader(VIEW_PRODUCT_LOADER, null, this);

        nameTextView = findViewById(R.id.textview_details_name);
        quantityTextView = findViewById(R.id.textview_details_quantity);
        decreaseQuantityButton = findViewById(R.id.button_details_decreasequantity);
        increaseQuantityButton = findViewById(R.id.button_details_increasequantity);
        totalTextView = findViewById(R.id.textview_details_total);
        supplierNameTextView = findViewById(R.id.textview_details_suppliername);
        emailSupplierImageButton = findViewById(R.id.imagebutton_details_emailsupplier);
        supplierEmailTextView = findViewById(R.id.textview_details_supplieremail);
        callSupplierImageButton = findViewById(R.id.imagebutton_details_callsupplier);
        supplierPhoneNumberTextView = findViewById(R.id.textview_details_supplierphonenumber);

        decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    int newQuantity = quantity - 1;

                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_AVAILABLE_QUANTITY, newQuantity);

                    int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);

                    // Show a toast message depending on whether or not the update was successful.
                    if (rowsAffected == 0) {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(DetailsActivity.this, "Error with updating quantity", Toast.LENGTH_LONG).show();
                    } else {
                        // Otherwise, the update was successful and we can display a toast (for MORE than ONE product).
                        Toast.makeText(DetailsActivity.this, "Quantity updated", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    int newQuantity = quantity + 1;

                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_AVAILABLE_QUANTITY, newQuantity);

                    int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);

                    // Show a toast message depending on whether or not the update was successful.
                    if (rowsAffected == 0) {
                        // If no rows were affected, then there was an error with the update.
                        Toast.makeText(DetailsActivity.this, "Error with updating quantity", Toast.LENGTH_LONG).show();
                    } else {
                        // Otherwise, the update was successful and we can display a toast (for MORE than ONE product).
                        Toast.makeText(DetailsActivity.this, "Quantity updated", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        emailSupplierImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailToSupplier();
            }
        });

        callSupplierImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierPhoneNumber));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    private void sendEmailToSupplier() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + supplierEmail)); // only email apps should handle this.
        intent.putExtra(Intent.EXTRA_SUBJECT, "Ward's customer: " + productName);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_AVAILABLE_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(
                this,
                currentProductUri,      // Query the content URI for the *current* product.
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            productName = data.getString(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
            pricePerProduct = data.getInt(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
            quantity = data.getInt(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_AVAILABLE_QUANTITY));
            supplierName = data.getString(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME));
            supplierEmail = data.getString(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL));
            supplierPhoneNumber = data.getString(data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER));

            nameTextView.setText(productName);
            quantityTextView.setText(String.valueOf(quantity));
            totalTextView.setText(String.valueOf(calculateTotalPrice()));
            supplierNameTextView.setText(supplierName);
            supplierEmailTextView.setText(supplierEmail);
            supplierPhoneNumberTextView.setText(supplierPhoneNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameTextView.setText("");
        quantityTextView.setText("");
        totalTextView.setText(String.valueOf(0));
        supplierNameTextView.setText("");
        supplierEmailTextView.setText("");
        supplierPhoneNumberTextView.setText("");
    }
    private int calculateTotalPrice() {
        return quantity * pricePerProduct;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_add.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu.
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option.
            case R.id.action_details_edit:
                // Start the EditActivity to edit the product.
                Log.i("CatalogActivity", "pressedItemUri: " + currentProductUri);
                Intent intent = new Intent(DetailsActivity.this, EditActivity.class);
                intent.setData(currentProductUri);
                startActivity(intent);
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_details_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this product?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deletePet();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deletePet() {

        // Call the ContentResolver to delete the product at the given content URI.
        // Pass in null for the selection and selection args because the currentProductUri
        // content URI already identifies the product that we want.
        int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);

        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, "Error with deleting product", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
        }
        // Close the activity
        finish();
    }
}
