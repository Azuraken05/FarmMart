package com.example.farmmart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderSubListFragment extends Fragment implements FarmerOrdersAdapter.OnOrderActionListener {

    private RecyclerView rvOrders;
    private FarmerOrdersAdapter adapter;
    private AppDatabase db;
    private int currentFarmerId;
    private int tabPosition;

    public OrderSubListFragment() {
        // Required empty public constructor
    }

    public OrderSubListFragment(int position) {
        this.tabPosition = position;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ✅ FIXED: Changed layout reference so it inflates the dedicated list view container!
        View view = inflater.inflate(R.layout.fragment_order_sub_list, container, false);

        rvOrders = view.findViewById(R.id.rv_farmer_orders);
        if (rvOrders != null) {
            rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        db = AppDatabase.getInstance(getContext());

        // Fix the NullPointerException warning here too
        Context context = getContext();
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            currentFarmerId = sharedPreferences.getInt("userId", -1);
        }

        loadOrdersForCurrentTab();

        return view;
    }

    private void loadOrdersForCurrentTab() {
        new Thread(() -> {
            String targetStatus = "To Ship";
            if (tabPosition == 1) targetStatus = "To Receive"; // Shipping mapping
            if (tabPosition == 2) targetStatus = "Completed";

            List<OrderItem> list = db.orderDao().getOrdersByFarmerAndStatus(currentFarmerId, targetStatus);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (rvOrders != null) {
                        adapter = new FarmerOrdersAdapter(getContext(), list, this);
                        rvOrders.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onShipOrderClick(OrderItem orderItem) {
        new Thread(() -> {
            // Update pipeline status dynamically when button triggers
            if ("To Ship".equals(orderItem.status)) {
                orderItem.status = "To Receive";
                db.orderDao().updateOrder(orderItem);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Order Accepted! Transferred to Customer Tracking.", Toast.LENGTH_SHORT).show();
                        loadOrdersForCurrentTab();
                    });
                }
            }
        }).start();
    }
}