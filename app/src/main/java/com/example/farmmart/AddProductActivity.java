package com.example.farmmart;

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
    private ImageView imgPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        // Initialize Views
        EditText etName = findViewById(R.id.et_product_name);
        EditText etPrice = findViewById(R.id.et_product_price);
        EditText etStock = findViewById(R.id.et_product_stock);
        Spinner spinnerCat = findViewById(R.id.spinner_category);
        Button btnSave = findViewById(R.id.btn_save_product);
        imgPreview = findViewById(R.id.img_select_product);

        // ✅ FIXED: Using OpenDocument to allow persistable permissions
        ActivityResultLauncher<String[]> getContent = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {
                    if (uri != null) {
                        // ✅ TAKE PERMANENT PERMISSION
                        final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                        getContentResolver().takePersistableUriPermission(uri, takeFlags);

                        imgPreview.setImageURI(uri);
                        selectedImagePath = uri.toString();
                    }
                }
        );

        // ✅ Open Document picker for images
        imgPreview.setOnClickListener(v -> getContent.launch(new String[]{"image/*"}));

        btnSave.setOnClickListener(v -> {
            String nameInput = etName.getText().toString().trim();
            String priceInput = etPrice.getText().toString().trim();
            String stockInput = etStock.getText().toString().trim();
            String selectedCategory = spinnerCat.getSelectedItem().toString();

            if (nameInput.isEmpty() || priceInput.isEmpty() || stockInput.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                Product newProduct = new Product(
                        nameInput,
                        "My Farm",
                        "₱" + priceInput,
                        selectedCategory,
                        Integer.parseInt(stockInput),
                        selectedImagePath
                );

                AppDatabase.getInstance(getApplicationContext()).productDao().insertProduct(newProduct);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Product listed successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();
        });
    }
}