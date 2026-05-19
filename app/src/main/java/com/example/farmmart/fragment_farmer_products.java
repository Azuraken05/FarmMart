package com.example.farmmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView; // ✅ ADDED: For binding layout count elements
import android.widget.Toast;
import java.util.List;

public class fragment_farmer_products extends Fragment {

    private RecyclerView rvFarmerProducts;
    private TextView tvTotalProductsCount, tvActiveProductsCount; // ✅ ADDED: View handles for dashboard metrics
    private AppDatabase db;

    public fragment_farmer_products() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer_products, container, false);

        rvFarmerProducts = view.findViewById(R.id.rv_farmer_products);
        Button btnAddNew = view.findViewById(R.id.btn_add_new_product);

        // ✅ ADDED: Mapping to capture the summary header metrics values
        tvTotalProductsCount = view.findViewById(R.id.tv_total_products_count);
        tvActiveProductsCount = view.findViewById(R.id.tv_active_products_count);

        db = AppDatabase.getInstance(getContext());

        rvFarmerProducts.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAddNew.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddProductActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFarmerProducts();
    }

    /**
     * Loads products specifically for the logged-in farmer.
     */
    private void loadFarmerProducts() {
        // ✅ 1. Get the current farmer's ID from SharedPreferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int currentFarmerId = sharedPreferences.getInt("userId", -1);

        new Thread(() -> {
            // ✅ 2. Use your filtered database calculation indicators queries
            int totalCount = db.productDao().getTotalProductCount(currentFarmerId);
            int activeCount = db.productDao().getActiveProductCount(currentFarmerId);
            List<Product> products = db.productDao().getProductsByFarmer(currentFarmerId);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // ✅ 3. Safely update text strings indicators on UI main context thread
                    if (tvTotalProductsCount != null) {
                        tvTotalProductsCount.setText(String.valueOf(totalCount));
                    }
                    if (tvActiveProductsCount != null) {
                        tvActiveProductsCount.setText(String.valueOf(activeCount));
                    }

                    // 4. Construct list display metrics layout maps bounds
                    FarmerProductAdapter adapter = new FarmerProductAdapter(
                            getContext(),
                            products,
                            true,
                            new FarmerProductAdapter.OnProductActionListener() {

                                @Override
                                public void onEditClick(Product product) {
                                    Intent intent = new Intent(getActivity(), AddProductActivity.class);
                                    intent.putExtra("PRODUCT_ID", product.id);
                                    startActivity(intent);
                                }

                                @Override
                                public void onDeleteClick(Product product) {
                                    deleteProductFromDb(product);
                                }
                            });
                    rvFarmerProducts.setAdapter(adapter);
                });
            }
        }).start();
    }

    private void deleteProductFromDb(Product product) {
        new Thread(() -> {
            db.productDao().deleteProduct(product);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), product.name + " deleted", Toast.LENGTH_SHORT).show();
                    loadFarmerProducts(); // Automatically updates totals and item list feeds together
                });
            }
        }).start();
    }
}