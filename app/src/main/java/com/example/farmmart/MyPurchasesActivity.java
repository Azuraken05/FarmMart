package com.example.farmmart;

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
    // ✅ Added TextView declarations for highlighting
    private TextView tvToShip, tvToReceive, tvCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_purchases);

        db = AppDatabase.getInstance(this);
        rvOrders = findViewById(R.id.rv_my_purchases);
        btnBack = findViewById(R.id.btn_purchases_back);

        tabToShip = findViewById(R.id.tab_to_ship);
        tabToReceive = findViewById(R.id.tab_to_receive);
        tabCompleted = findViewById(R.id.tab_completed);

        // ✅ Find the TextViews inside the layouts (Check your XML IDs match these)
        tvToShip = findViewById(R.id.tv_to_ship);
        tvToReceive = findViewById(R.id.tv_to_receive);
        tvCompleted = findViewById(R.id.tv_completed);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        btnBack.setOnClickListener(v -> finish());

        String status = getIntent().getStringExtra("ORDER_STATUS");
        if (status == null) status = "To Ship";

        // ✅ Set up Click Listeners with highlighting
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

        // Initial UI state
        updateTabUI(status);
        loadOrders(status);
    }

    // ✅ New method to highlight the active tab
    private void updateTabUI(String status) {
        int activeColor = Color.parseColor("#435334"); // Your theme green
        int inactiveColor = Color.parseColor("#808080"); // Gray

        // Reset all
        tvToShip.setTextColor(inactiveColor);
        tvToReceive.setTextColor(inactiveColor);
        tvCompleted.setTextColor(inactiveColor);

        // Highlight selected
        if (status.equals("To Ship")) tvToShip.setTextColor(activeColor);
        else if (status.equals("To Receive")) tvToReceive.setTextColor(activeColor);
        else if (status.equals("Completed")) tvCompleted.setTextColor(activeColor);
    }

    private void loadOrders(String status) {
        new Thread(() -> {
            List<OrderItem> orders = db.orderDao().getOrdersByStatus(status);
            runOnUiThread(() -> {
                adapter = new OrderAdapter(this, orders);
                rvOrders.setAdapter(adapter);
            });
        }).start();
    }
}