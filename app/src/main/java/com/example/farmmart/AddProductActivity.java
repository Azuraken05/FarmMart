package com.example.farmmart;

import android.annotation.SuppressLint;
import android.content.Intent;
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

        // ✅ Check if we are editing
        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId != -1) {
            btnSave.setText("UPDATE PRODUCT");
            loadProductForEdit();
        }

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
        // ✅ Get strings and trim spaces
        String name = etName.getText().toString().trim();
        String priceText = etPrice.getText().toString().trim();
        String stockText = etStock.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();

        // ✅ VALIDATION: Prevents NumberFormatException crash if fields are empty
        if (name.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            Toast.makeText(this, "Please fill in Name, Price, and Stock", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                // ✅ Safe to parse now because we checked if they were empty
                int stockValue = Integer.parseInt(stockText);

                Product p = new Product(
                        name,
                        "My Farm",
                        "₱" + priceText,
                        spinnerCat.getSelectedItem().toString(),
                        stockValue,
                        selectedImagePath,
                        desc
                );

                if (productId != -1) {
                    p.id = productId;
                    AppDatabase.getInstance(this).productDao().updateProduct(p);
                } else {
                    AppDatabase.getInstance(this).productDao().insertProduct(p);
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Market Updated!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (NumberFormatException e) {
                runOnUiThread(() -> Toast.makeText(this, "Invalid number format in Price or Stock", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}