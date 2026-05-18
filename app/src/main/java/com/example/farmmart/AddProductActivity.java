package com.example.farmmart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class AddProductActivity extends AppCompatActivity {

    private String selectedImagePath = "";
    private int productId = -1; // -1 means New Product
    private ImageView imgPreview;
    private EditText etName, etPrice, etStock, etDescription;
    private Spinner spinnerCat;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        etName = findViewById(R.id.et_product_name);
        etPrice = findViewById(R.id.et_product_price);
        etStock = findViewById(R.id.et_product_stock);
        etDescription = findViewById(R.id.et_product_description);
        spinnerCat = findViewById(R.id.spinner_category);
        imgPreview = findViewById(R.id.img_select_product);
        Button btnSave = findViewById(R.id.btn_save_product);

        // ✅ Check if we are editing an existing product
        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId != -1) {
            btnSave.setText("UPDATE PRODUCT");
            loadProductForEdit();
        }

        // Image Picker Logic
        ActivityResultLauncher<String[]> getContent = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        imgPreview.setImageURI(uri);
                        selectedImagePath = uri.toString();
                    }
                }
        );

        imgPreview.setOnClickListener(v -> getContent.launch(new String[]{"image/*"}));
        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void loadProductForEdit() {
        new Thread(() -> {
            Product p = AppDatabase.getInstance(this).productDao().getProductById(productId);
            if (p != null) {
                runOnUiThread(() -> {
                    etName.setText(p.name);
                    etPrice.setText(p.price.replace("₱", ""));
                    etStock.setText(String.valueOf(p.stock));
                    etDescription.setText(p.description);
                    selectedImagePath = p.imagePath;
                    if (p.imagePath != null && !p.imagePath.isEmpty()) {
                        try {
                            imgPreview.setImageURI(Uri.parse(p.imagePath));
                        } catch (Exception e) {
                            imgPreview.setImageResource(R.drawable.logo);
                        }
                    }
                });
            }
        }).start();
    }

    private void saveProduct() {
        String name = etName.getText().toString().trim();
        String priceText = etPrice.getText().toString().trim();
        String stockText = etStock.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();

        // ✅ Retrieve the farmer ID saved during Login in MainActivity
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int currentFarmerId = sharedPreferences.getInt("userId", -1);

        // Validation
        if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            Toast.makeText(this, "Please fill in Name, Price, and Stock", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ If currentFarmerId is -1, it means the login session wasn't saved correctly
        if (currentFarmerId == -1) {
            Toast.makeText(this, "Session error. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                int stockValue = Integer.parseInt(stockText);

                // Create product object with the farmer's ID as the 8th parameter
                Product p = new Product(
                        name,
                        "My Farm",
                        "₱" + priceText,
                        spinnerCat.getSelectedItem().toString(),
                        stockValue,
                        selectedImagePath,
                        desc,
                        currentFarmerId
                );

                if (productId != -1) {
                    p.id = productId; // Keep the same ID if updating
                    AppDatabase.getInstance(this).productDao().updateProduct(p);
                } else {
                    AppDatabase.getInstance(this).productDao().insertProduct(p);
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Market Updated!", Toast.LENGTH_SHORT).show();
                    finish(); // Return to the dashboard
                });
            } catch (NumberFormatException e) {
                runOnUiThread(() -> Toast.makeText(this, "Invalid format in Price or Stock", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}