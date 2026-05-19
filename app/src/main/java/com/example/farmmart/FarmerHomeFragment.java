package com.example.farmmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FarmerHomeFragment extends Fragment {

    private TextView tvFarmerName, tvProductCount, tvPendingCount, tvSalesCount;
    private TextView tvSeeAll;
    private Button btnViewProducts;
    private RecyclerView rvRecentOrders, rvMyProducts;

    private AppDatabase db;
    private int currentFarmerId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer_home, container, false);

        db = AppDatabase.getInstance(requireContext());

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        currentFarmerId = sharedPreferences.getInt("userId", -1);

        tvFarmerName = view.findViewById(R.id.tv_farmer_name);
        tvProductCount = view.findViewById(R.id.tv_count_products);
        tvPendingCount = view.findViewById(R.id.tv_count_pending);
        tvSalesCount = view.findViewById(R.id.tv_count_sales);
        tvSeeAll = view.findViewById(R.id.tv_see_all_orders);
        btnViewProducts = view.findViewById(R.id.btn_view_products);
        rvRecentOrders = view.findViewById(R.id.rv_recent_orders);
        rvMyProducts = view.findViewById(R.id.rv_my_products);

        if (rvRecentOrders != null) {
            rvRecentOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        }
        if (rvMyProducts != null) {
            rvMyProducts.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        }

        String farmerName = requireActivity().getIntent().getStringExtra("USER_NAME");
        if (farmerName != null && tvFarmerName != null) {
            tvFarmerName.setText(farmerName.toUpperCase());
        }

        if (tvSeeAll != null) {
            tvSeeAll.setOnClickListener(v -> Toast.makeText(requireContext(), "This feature is still in progress.", Toast.LENGTH_SHORT).show());
        }
        if (btnViewProducts != null) {
            btnViewProducts.setOnClickListener(v -> Toast.makeText(requireContext(), "This feature is still in progress.", Toast.LENGTH_SHORT).show());
        }

        loadDashboardAnalytics();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDashboardAnalytics();
    }

    private void loadDashboardAnalytics() {
        if (currentFarmerId == -1) {
            return;
        }

        new Thread(() -> {
            int totalProducts = db.productDao().getProductCountByFarmer(currentFarmerId);
            int pendingOrders = db.orderDao().getPendingCountByFarmer(currentFarmerId);
            double totalSales = db.orderDao().getTotalSalesByFarmer(currentFarmerId);

            List<OrderItem> recentOrders = db.orderDao().getRecentOrdersForFarmer(currentFarmerId);
            List<Product> farmerProducts = db.productDao().getProductsByFarmer(currentFarmerId);

            if (isAdded()) {
                requireActivity().runOnUiThread(() -> {
                    if (tvProductCount != null) tvProductCount.setText(String.valueOf(totalProducts));
                    if (tvPendingCount != null) tvPendingCount.setText(String.format("%02d", pendingOrders));

                    if (tvSalesCount != null) {
                        if (totalSales >= 1000) {
                            tvSalesCount.setText("₱" + String.format("%.1fk", totalSales / 1000.0));
                        } else {
                            tvSalesCount.setText("₱" + String.format("%.2f", totalSales));
                        }
                    }

                    if (rvMyProducts != null) {
                        // ✅ FIXED: Explicitly implementing your two interface actions to resolve the type mismatch
                        FarmerProductAdapter productsAdapter = new FarmerProductAdapter(
                                requireContext(),
                                farmerProducts,
                                true,
                                new FarmerProductAdapter.OnProductActionListener() {
                                    @Override
                                    public void onEditClick(Product product) {
                                        // When clicking your product on the dashboard, it opens the detail read-only page
                                        Intent intent = new Intent(requireActivity(), activity_product_detail.class);
                                        intent.putExtra("PRODUCT_ID", product.id);
                                        intent.putExtra("IS_FARMER_VIEW", true);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onDeleteClick(Product product) {
                                        // Optional handler placeholder if you select to delete directly from the horizontal scroll later
                                        Toast.makeText(requireContext(), "Delete clicked for " + product.name, Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                        rvMyProducts.setAdapter(productsAdapter);
                    }
                });
            }
        }).start();
    }
}