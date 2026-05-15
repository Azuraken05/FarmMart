package com.example.farmmart;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class activity_product_detail extends AppCompatActivity {

    private int quantity = 1;
    private TextView tvQty;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // 1. Initialize Views
        ImageView imgMain = findViewById(R.id.img_detail_main);
        TextView tvName = findViewById(R.id.tv_detail_name);
        TextView tvPrice = findViewById(R.id.tv_detail_price);
        TextView tvDesc = findViewById(R.id.tv_detail_desc);
        tvQty = findViewById(R.id.tv_detail_qty);

        Button btnPlus = findViewById(R.id.btn_plus);
        Button btnMinus = findViewById(R.id.btn_minus);
        Button btnAddToCart = findViewById(R.id.btn_add_to_cart_large);

        // ✅ Initialize Back Button
        ImageView btnBack = findViewById(R.id.btn_back_detail);

        db = AppDatabase.getInstance(this);

        // 2. Get Product ID passed from HomepageUserFragment
        int productId = getIntent().getIntExtra("PRODUCT_ID", -1);

        if (productId != -1) {
            loadProductDetails(productId, imgMain, tvName, tvPrice, tvDesc);
        } else {
            Toast.makeText(this, "Error: Product not found", Toast.LENGTH_SHORT).show();
        }

        // ✅ 3. Back Button Logic
        btnBack.setOnClickListener(v -> {
            finish(); // Closes this screen and returns to the previous one
        });

        // 4. Increment Quantity Logic
        btnPlus.setOnClickListener(v -> {
            quantity++;
            tvQty.setText(String.valueOf(quantity));
        });

        // 5. Decrement Quantity Logic (Minimum is 1)
        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQty.setText(String.valueOf(quantity));
            }
        });

        // 6. Add to Cart Logic
        btnAddToCart.setOnClickListener(v -> {
            String message = "Added " + quantity + " " + tvName.getText().toString() + " to cart";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    private void loadProductDetails(int id, ImageView img, TextView name, TextView price, TextView desc) {
        new Thread(() -> {
            Product p = db.productDao().getProductById(id);

            if (p != null) {
                runOnUiThread(() -> {
                    name.setText(p.name);
                    price.setText(p.price);
                    desc.setText(p.description != null ? p.description : "No description available.");

                    if (p.imagePath != null && !p.imagePath.isEmpty()) {
                        try {
                            img.setImageURI(Uri.parse(p.imagePath));
                        } catch (Exception e) {
                            img.setImageResource(R.drawable.logo);
                        }
                    } else {
                        img.setImageResource(R.drawable.logo);
                    }
                });
            }
        }).start();
    }
}