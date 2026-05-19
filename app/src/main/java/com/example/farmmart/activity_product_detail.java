package com.example.farmmart;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class activity_product_detail extends AppCompatActivity {

    private int quantity = 1;
    private TextView tvQty;
    private AppDatabase db;
    private String currentImagePath = ""; // Stores the path to pass to the Cart

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

        // 2. Get Product ID and Farmer View check flag from Intent
        int productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        boolean isFarmerView = getIntent().getBooleanExtra("IS_FARMER_VIEW", false);

        // ✅ HIDE PURCHASE CONTROLS SAFELY IF ACCESSED FROM FARMER DASHBOARD
        if (isFarmerView) {
            if (btnAddToCart != null) btnAddToCart.setVisibility(View.GONE);
            if (btnPlus != null) btnPlus.setVisibility(View.GONE);
            if (btnMinus != null) btnMinus.setVisibility(View.GONE);
            if (tvQty != null) tvQty.setVisibility(View.GONE);
        }

        if (productId != -1) {
            loadProductDetails(productId, imgMain, tvName, tvPrice, tvDesc);
        } else {
            Toast.makeText(this, "Error: Product not found", Toast.LENGTH_SHORT).show();
        }

        // 3. Back Button Logic
        btnBack.setOnClickListener(v -> finish());

        // 4. Quantity Increment
        if (btnPlus != null) {
            btnPlus.setOnClickListener(v -> {
                quantity++;
                tvQty.setText(String.valueOf(quantity));
            });
        }

        // 5. Quantity Decrement
        if (btnMinus != null) {
            btnMinus.setOnClickListener(v -> {
                if (quantity > 1) {
                    quantity--;
                    tvQty.setText(String.valueOf(quantity));
                }
            });
        }

        // 6. Add to Cart Logic (Saving to Room Database)
        if (btnAddToCart != null) {
            btnAddToCart.setOnClickListener(v -> {
                String pName = tvName.getText().toString();
                String pPrice = tvPrice.getText().toString();

                new Thread(() -> {
                    // ✅ 1. Fetch the product from Room to find out who owns it
                    Product currentProduct = db.productDao().getProductById(productId);
                    int trackedFarmerId = -1;

                    if (currentProduct != null) {
                        trackedFarmerId = currentProduct.farmerId;
                    }

                    // ✅ 2. Pass the retrieved farmerId as the 7th argument to match your new constructor structure
                    CartItem cartItem = new CartItem(
                            productId,
                            pName,
                            pPrice,
                            currentImagePath, // Passed from the loaded product
                            quantity,
                            true,             // Items are selected for checkout by default
                            trackedFarmerId   // Links the item back to the owner
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
    }

    private void loadProductDetails(int id, ImageView img, TextView name, TextView price, TextView desc) {
        new Thread(() -> {
            Product p = db.productDao().getProductById(id);

            if (p != null) {
                // Capture the image path so it can be saved to the Cart table later
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