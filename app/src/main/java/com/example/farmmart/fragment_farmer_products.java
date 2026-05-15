package com.example.farmmart;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import java.util.List;

public class fragment_farmer_products extends Fragment {

    private RecyclerView rvFarmerProducts;
    private AppDatabase db;

    public fragment_farmer_products() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_farmer_products, container, false);

        // 1. Initialize Views
        rvFarmerProducts = view.findViewById(R.id.rv_farmer_products);
        Button btnAddNew = view.findViewById(R.id.btn_add_new_product);

        // Setup Database
        db = AppDatabase.getInstance(getContext());

        // 2. Setup RecyclerView
        rvFarmerProducts.setLayoutManager(new LinearLayoutManager(getContext()));

        // 3. Handle "+ Add New" Button Click
        btnAddNew.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddProductActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the list whenever the farmer returns to this fragment
        loadFarmerProducts();
    }

    private void loadFarmerProducts() {
        // Fetch all products from Room Database
        List<Product> products = db.productDao().getAllProducts();

        // Initialize the Adapter with the product list and click listeners
        FarmerProductAdapter adapter = new FarmerProductAdapter(products, new FarmerProductAdapter.OnProductListener() {
            @Override
            public void onDelete(Product product) {
                // Delete product from DB and refresh
                deleteProductFromDb(product);
            }

            @Override
            public void onEdit(Product product) {
                // For now, a simple toast. Later we can send the ID to AddProductActivity to edit.
                Toast.makeText(getContext(), "Edit feature coming soon for " + product.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        rvFarmerProducts.setAdapter(adapter);
    }

    private void deleteProductFromDb(Product product) {
        // Run database operation on a background thread
        new Thread(() -> {
            db.productDao().deleteProduct(product);

            // Update UI on the Main Thread
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), product.getName() + " removed from market", Toast.LENGTH_SHORT).show();
                    loadFarmerProducts(); // Refresh the list
                });
            }
        }).start();
    }
}