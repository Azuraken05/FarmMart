package com.example.farmmart;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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

    // ✅ New: Sub-navigation layout and tab controls for the Completed section
    private LinearLayout layoutCompletedSubTabs;
    private TextView tvToRateSubTab, tvMyReviewsSubTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_purchases);

        // 1. Initialize Database and Views
        db = AppDatabase.getInstance(this);
        rvOrders = findViewById(R.id.rv_my_purchases);
        btnBack = findViewById(R.id.btn_purchases_back);

        // 2. Initialize Main Navigation Tabs
        tabToShip = findViewById(R.id.tab_to_ship);
        tabToReceive = findViewById(R.id.tab_to_receive);
        tabCompleted = findViewById(R.id.tab_completed);

        // 3. Initialize Main Tab TextViews for highlighting
        tvToShip = findViewById(R.id.tv_to_ship);
        tvToReceive = findViewById(R.id.tv_to_receive);
        tvCompleted = findViewById(R.id.tv_completed);

        // ✅ 4. Initialize the sub-navigation elements
        // Make sure these IDs match your activity_my_purchases.xml layout
        layoutCompletedSubTabs = findViewById(R.id.layout_completed_sub_tabs);
        tvToRateSubTab = findViewById(R.id.tv_to_rate_sub_tab);
        tvMyReviewsSubTab = findViewById(R.id.tv_my_reviews_sub_tab);

        // 5. Set up RecyclerView
        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        // 6. Back Button - Returns to ProfileFragment
        btnBack.setOnClickListener(v -> finish());

        // 7. Get the initial tab status from ProfileFragment
        String status = getIntent().getStringExtra("ORDER_STATUS");
        if (status == null) {
            status = "To Ship"; // Default to "To Ship"
        }

        // 8. Main Tab Click Listeners
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

        // ✅ 9. Sub-tab Click Listeners inside the Completed view
        tvToRateSubTab.setOnClickListener(v -> {
            updateSubTabHighlight(true);
            loadOrders("Completed"); // Loads the items available to rate
        });

        tvMyReviewsSubTab.setOnClickListener(v -> {
            updateSubTabHighlight(false);
            // Show the work-in-progress message requested
            Toast.makeText(this, "This feature is still in progress.", Toast.LENGTH_SHORT).show();
        });

        // Initial UI State and Data Load
        updateTabUI(status);
        loadOrders(status);
    }

    /**
     * Highlights the selected main tab text and handles sub-tab container visibility.
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
                layoutCompletedSubTabs.setVisibility(View.GONE); // Hide sub-tabs
                break;
            case "To Receive":
                tvToReceive.setTextColor(activeColor);
                layoutCompletedSubTabs.setVisibility(View.GONE); // Hide sub-tabs
                break;
            case "Completed":
                tvCompleted.setTextColor(activeColor);
                layoutCompletedSubTabs.setVisibility(View.VISIBLE); // ✅ Reveal sub-navigation layout
                updateSubTabHighlight(true); // Default sub-tab highlight to "To Rate"
                break;
        }
    }

    /**
     * ✅ Helper method to handle sub-navigation text colors (To Rate vs My Reviews)
     */
    private void updateSubTabHighlight(boolean isToRateSelected) {
        int activeColor = Color.parseColor("#435334");
        int inactiveColor = Color.parseColor("#808080");

        if (isToRateSelected) {
            tvToRateSubTab.setTextColor(activeColor);
            tvMyReviewsSubTab.setTextColor(inactiveColor);
        } else {
            tvToRateSubTab.setTextColor(inactiveColor);
            tvMyReviewsSubTab.setTextColor(activeColor);
        }
    }

    /**
     * Loads orders from the database on a background thread.
     */
    private void loadOrders(String status) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int currentUserId = sharedPreferences.getInt("userId", -1);

        new Thread(() -> {
            List<OrderItem> orders = db.orderDao().getOrdersByStatus(status, currentUserId);

            runOnUiThread(() -> {
                adapter = new OrderAdapter(this, orders);
                rvOrders.setAdapter(adapter);
            });
        }).start();
    }
}