package com.example.farmmart;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class fragment_shop extends Fragment {

    private RecyclerView recyclerView;
    private FarmerProductAdapter adapter;
    private AppDatabase db;
    private TextView tabLatest, tabTopSales, tabPrice, tabRelevance;
    private View tabIndicator;

    // ✅ Track the price toggle state
    private boolean isPriceLowToHigh = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        db = AppDatabase.getInstance(getContext());

        // 1. Initialize RecyclerView
        recyclerView = view.findViewById(R.id.rv_shop_products);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // 2. Initialize Views & Indicator
        tabRelevance = view.findViewById(R.id.tab_relevance);
        tabLatest = view.findViewById(R.id.tab_latest);
        tabTopSales = view.findViewById(R.id.tab_top_sales);
        tabPrice = view.findViewById(R.id.tab_price);
        tabIndicator = view.findViewById(R.id.tab_indicator);

        // 3. Initial Setup
        view.post(() -> {
            setActiveTab(tabRelevance);
            loadProducts("relevance");
            resetPriceTabText();
        });

        // 4. Tab Click Listeners
        tabRelevance.setOnClickListener(v -> {
            setActiveTab(tabRelevance);
            loadProducts("relevance");
            resetPriceTabText();
        });

        tabLatest.setOnClickListener(v -> {
            setActiveTab(tabLatest);
            loadProducts("latest");
            resetPriceTabText();
        });

        tabTopSales.setOnClickListener(v -> {
            Toast.makeText(getContext(), "This feature would be added soon", Toast.LENGTH_SHORT).show();
        });

        tabPrice.setOnClickListener(v -> {
            setActiveTab(tabPrice);
            if (isPriceLowToHigh) {
                loadProducts("price_low");
                tabPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_downward, 0);
            } else {
                loadProducts("price_high");
                tabPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_upward, 0);
            }
            isPriceLowToHigh = !isPriceLowToHigh;
        });

        return view;
    }

    private void setActiveTab(TextView selectedTab) {
        TextView[] tabs = {tabRelevance, tabLatest, tabTopSales, tabPrice};
        for (TextView tab : tabs) {
            tab.setTextColor(Color.parseColor("#828282"));
            tab.setTypeface(null, Typeface.NORMAL);
        }

        selectedTab.setTextColor(Color.parseColor("#435334"));
        selectedTab.setTypeface(null, Typeface.BOLD);

        float textWidth = selectedTab.getPaint().measureText(selectedTab.getText().toString());

        ViewGroup.LayoutParams params = tabIndicator.getLayoutParams();
        params.width = (int) textWidth;
        tabIndicator.setLayoutParams(params);

        float targetX = selectedTab.getX() + (selectedTab.getWidth() / 2f) - (textWidth / 2f);

        tabIndicator.animate()
                .x(targetX)
                .setDuration(250)
                .start();
    }

    private void resetPriceTabText() {
        isPriceLowToHigh = true;
        tabPrice.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.unfold_more, 0);
    }

    private void loadProducts(String criteria) {
        new Thread(() -> {
            List<Product> list = null;
            switch (criteria) {
                case "latest":
                    list = db.productDao().getAllProductsLatest();
                    break;
                case "price_low":
                    list = db.productDao().getAllProductsByPriceLow();
                    break;
                case "price_high":
                    list = db.productDao().getAllProductsByPriceHigh();
                    break;
                default:
                    list = db.productDao().getAllProducts();
                    break;
            }

            final List<Product> finalProductList = list;
            if (getActivity() != null && finalProductList != null) {
                getActivity().runOnUiThread(() -> {
                    // ✅ FIXED: Added 'false' to Constructor (Shop View uses item_product_card)
                    adapter = new FarmerProductAdapter(getContext(), finalProductList, false, new FarmerProductAdapter.OnProductActionListener() {
                        @Override
                        public void onEditClick(Product product) {
                            // In the Shop Fragment, clicking the item opens the detail page
                            Intent intent = new Intent(getContext(), activity_product_detail.class);
                            intent.putExtra("PRODUCT_ID", product.id);
                            startActivity(intent);
                        }

                        @Override
                        public void onDeleteClick(Product product) {
                            // Buyers don't delete products
                        }
                    });
                    recyclerView.setAdapter(adapter);
                });
            }
        }).start();
    }
}