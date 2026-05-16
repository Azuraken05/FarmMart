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
    private String currentImagePath = ""; // ✅ Stores the path to pass to the Cart

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
        ImageView btnBack = findViewById(R.id.btn_back_detail);

        db = AppDatabase.getInstance(this);

        // 2. Get Product ID from Intent
        int productId = getIntent().getIntExtra("PRODUCT_ID", -1);

        if (productId != -1) {
            loadProductDetails(productId, imgMain, tvName, tvPrice, tvDesc);
        } else {
            Toast.makeText(this, "Error: Product not found", Toast.LENGTH_SHORT).show();
        }

        // 3. Back Button Logic
        btnBack.setOnClickListener(v -> finish());

        // 4. Quantity Increment
        btnPlus.setOnClickListener(v -> {
            quantity++;
            tvQty.setText(String.valueOf(quantity));
        });

        // 5. Quantity Decrement
        btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQty.setText(String.valueOf(quantity));
            }
        });

        // 6. ✅ Add to Cart Logic (Saving to Room Database)
        btnAddToCart.setOnClickListener(v -> {
            String pName = tvName.getText().toString();
            String pPrice = tvPrice.getText().toString();

            new Thread(() -> {
                // Create a new CartItem object with current details
                CartItem cartItem = new CartItem(
                        productId,
                        pName,
                        pPrice,
                        currentImagePath, // Passed from the loaded product
                        quantity,
                        true // Items are selected for checkout by default
                );

                // Perform the Database Insert
                db.cartDao().addToCart(cartItem);

                // Show success message on the UI Thread
                runOnUiThread(() -> {
                    Toast.makeText(this, "Added " + quantity + " " + pName + " to cart", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });
    }

    private void loadProductDetails(int id, ImageView img, TextView name, TextView price, TextView desc) {
        new Thread(() -> {
            Product p = db.productDao().getProductById(id);

            if (p != null) {
                // ✅ Capture the image path so it can be saved to the Cart table later
                currentImagePath = p.imagePath;

                runOnUiThread(() -> {
                    name.setText(p.name);
                    price.setText(p.price);
                    desc.setText(p.description != null ? p.description : "No description available.");

                    // Handle Image Loading
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