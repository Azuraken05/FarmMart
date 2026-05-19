package com.example.farmmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private AppDatabase db;
    private TextView tvSubtotal, tvTotal, tvDeliveryFee;
    private Button btnPlaceOrder;
    private ImageView btnBack;

    private RecyclerView rvItems;
    private CheckoutItemAdapter adapter;

    private double subtotal = 0;
    private final int DELIVERY_FEE = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        db = AppDatabase.getInstance(this);

        tvSubtotal = findViewById(R.id.tv_checkout_subtotal);
        tvDeliveryFee = findViewById(R.id.tv_checkout_delivery);
        tvTotal = findViewById(R.id.tv_checkout_total);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        btnBack = findViewById(R.id.btn_checkout_back);

        rvItems = findViewById(R.id.rv_checkout_items);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());

        loadOrderSummary();

        btnPlaceOrder.setOnClickListener(v -> {
            placeOrderLogic();
        });
    }

    private void loadOrderSummary() {
        new Thread(() -> {
            List<CartItem> selectedItems = db.cartDao().getSelectedItems();
            subtotal = 0;

            for (CartItem item : selectedItems) {
                try {
                    String cleanPrice = item.productPrice.replace("₱", "").replace(",", "").trim();
                    subtotal += Double.parseDouble(cleanPrice) * item.quantity;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            double finalTotal = subtotal + DELIVERY_FEE;

            runOnUiThread(() -> {
                adapter = new CheckoutItemAdapter(selectedItems);
                rvItems.setAdapter(adapter);

                tvSubtotal.setText("₱" + (int) subtotal);
                tvDeliveryFee.setText("₱" + DELIVERY_FEE);
                tvTotal.setText("₱" + (int) finalTotal);
                btnPlaceOrder.setText("Place Order");
            });
        }).start();
    }

    private void placeOrderLogic() {
        // 1. Get current user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int currentUserId = sharedPreferences.getInt("userId", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            List<CartItem> selectedItems = db.cartDao().getSelectedItems();

            if (selectedItems.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(this, "No items selected", Toast.LENGTH_SHORT).show());
                return;
            }

            // 2. Transfer each CartItem to the OrderItem table
            for (CartItem item : selectedItems) {
                OrderItem order = new OrderItem();
                order.productName = item.productName;
                order.productPrice = item.productPrice;
                order.quantity = item.quantity;
                order.imagePath = item.imagePath;

                // ✅ FIXED: Changed from "Completed" back to "To Ship" so it reappears inside your ProfileFragment user tabs!
                order.status = "To Ship";

                // 3. Assign the order to the logged-in customer user
                order.userId = currentUserId;

                // ✅ Remembers the farmerId link so your dashboard analytics can trace it later
                order.farmerId = item.farmerId;

                db.orderDao().insertOrder(order);
            }

            // 4. Clear the selected items from the cart
            db.cartDao().deleteSelectedItems();

            runOnUiThread(() -> {
                // ✅ UPDATED: Fixed text indicator to guide users to the proper section
                Toast.makeText(this, "Order Placed! Check your 'To Ship' tab.", Toast.LENGTH_LONG).show();
                finish();
            });
        }).start();
    }
}