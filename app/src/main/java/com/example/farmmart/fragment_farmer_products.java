package com.example.farmmart;

import android.content.Intent;
import android.os.Bundle;
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

    public fragment_farmer_products() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer_products, container, false);
        rvFarmerProducts = view.findViewById(R.id.rv_farmer_products);
        Button btnAddNew = view.findViewById(R.id.btn_add_new_product);
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

    private void loadFarmerProducts() {
        new Thread(() -> {
            List<Product> products = db.productDao().getAllProducts();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // ✅ FIXED: Added getContext() as the first argument
                    // ✅ FIXED: Updated listener implementation to match the new Adapter interface
                    FarmerProductAdapter adapter = new FarmerProductAdapter(getContext(), products, new FarmerProductAdapter.OnProductListener() {

                        @Override
                        public void onProductClick(Product product) {
                            // This replaces the old onEdit if you want simple click-to-edit logic
                            Intent intent = new Intent(getActivity(), AddProductActivity.class);
                            intent.putExtra("PRODUCT_ID", product.id);
                            startActivity(intent);
                        }

                        // Note: If you want onDelete/onEdit buttons specifically,
                        // you must add those methods back into the FarmerProductAdapter.OnProductListener interface first.
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
                    loadFarmerProducts();
                });
            }
        }).start();
    }
}