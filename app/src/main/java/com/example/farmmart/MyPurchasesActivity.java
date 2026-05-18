package com.example.farmmart;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyPurchasesActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private AppDatabase db;
    private ImageView btnBack;

    private LinearLayout tabToShip, tabToReceive, tabCompleted;
    private TextView tvToShip, tvToReceive, tvCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_purchases);

        // 1. Initialize Database and Views
        db = AppDatabase.getInstance(this);
        rvOrders = findViewById(R.id.rv_my_purchases);
        btnBack = findViewById(R.id.btn_purchases_back);

        // 2. Initialize Navigation Tabs
        tabToShip = findViewById(R.id.tab_to_ship);
        tabToReceive = findViewById(R.id.tab_to_receive);
        tabCompleted = findViewById(R.id.tab_completed);

        // 3. Initialize Tab TextViews for highlighting
        tvToShip = findViewById(R.id.tv_to_ship);
        tvToReceive = findViewById(R.id.tv_to_receive);
        tvCompleted = findViewById(R.id.tv_completed);

        // 4. Set up RecyclerView
        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        // 5. Back Button - Returns to ProfileFragment
        btnBack.setOnClickListener(v -> finish());

        // 6. Get the initial tab status from ProfileFragment
        String status = getIntent().getStringExtra("ORDER_STATUS");
        if (status == null) {
            status = "To Ship"; // Default to "To Ship"
        }

        // 7. Tab Click Listeners
        tabToShip.setOnClickListener(v -> {
            updateTabUI("To Ship");
            loadOrders("To Ship");
        });

        tabToReceive.setOnClickListener(v -> {
            updateTabUI("To Receive");
            loadOrders("To Receive");
        });

        tabCompleted.setOnClickListener(v -> {
            updateTabUI("Completed");
            loadOrders("Completed");
        });

        // Initial UI State and Data Load
        updateTabUI(status);
        loadOrders(status);
    }

    /**
     * Highlights the selected tab text and resets others to gray.
     */
    private void updateTabUI(String status) {
        int activeColor = Color.parseColor("#435334"); // FarmMart Green
        int inactiveColor = Color.parseColor("#808080"); // Gray

        tvToShip.setTextColor(inactiveColor);
        tvToReceive.setTextColor(inactiveColor);
        tvCompleted.setTextColor(inactiveColor);

        switch (status) {
            case "To Ship":
                tvToShip.setTextColor(activeColor);
                break;
            case "To Receive":
                tvToReceive.setTextColor(activeColor);
                break;
            case "Completed":
                tvCompleted.setTextColor(activeColor);
                break;
        }
    }

    /**
     * Loads orders from the database on a background thread.
     * ✅ UPDATED: Now filters by the current logged-in User ID.
     */
    private void loadOrders(String status) {
        // Retrieve current logged-in user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int currentUserId = sharedPreferences.getInt("userId", -1);

        new Thread(() -> {
            // Fetch filtered orders from the Room Database for this specific user
            List<OrderItem> orders = db.orderDao().getOrdersByStatus(status, currentUserId);

            runOnUiThread(() -> {
                adapter = new OrderAdapter(this, orders);
                rvOrders.setAdapter(adapter);
            });
        }).start();
    }
}