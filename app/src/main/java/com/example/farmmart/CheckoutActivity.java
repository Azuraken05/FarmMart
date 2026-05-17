package com.example.farmmart;

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

    // ✅ Added for showing the item list
    private RecyclerView rvItems;
    private CheckoutItemAdapter adapter;

    private double subtotal = 0;
    private final int DELIVERY_FEE = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        db = AppDatabase.getInstance(this);

        // Initialize Views
        tvSubtotal = findViewById(R.id.tv_checkout_subtotal);
        tvDeliveryFee = findViewById(R.id.tv_checkout_delivery);
        tvTotal = findViewById(R.id.tv_checkout_total);
        btnPlaceOrder = findViewById(R.id.btn_place_order);
        btnBack = findViewById(R.id.btn_checkout_back);

        // ✅ Initialize RecyclerView
        rvItems = findViewById(R.id.rv_checkout_items);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        btnBack.setOnClickListener(v -> finish());

        loadOrderSummary();

        // Logic for Place Order
        btnPlaceOrder.setOnClickListener(v -> {
            placeOrderLogic();
        });
    }

    private void loadOrderSummary() {
        new Thread(() -> {
            // Get only selected items
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
                // ✅ Update the list of items
                adapter = new CheckoutItemAdapter(selectedItems);
                rvItems.setAdapter(adapter);

                // Update text summaries
                tvSubtotal.setText("₱" + (int) subtotal);
                tvDeliveryFee.setText("₱" + DELIVERY_FEE);
                tvTotal.setText("₱" + (int) finalTotal);
                btnPlaceOrder.setText("Place Order");
            });
        }).start();
    }

    private void placeOrderLogic() {
        new Thread(() -> {
            // 1. Delete only the items that were checked/selected
            db.cartDao().deleteSelectedItems();

            runOnUiThread(() -> {
                Toast.makeText(this, "Order Placed Successfully!", Toast.LENGTH_LONG).show();
                // 2. Return to Dashboard (Cart Fragment will refresh via onResume)
                finish();
            });
        }).start();
    }
}