package com.example.farmmart;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout; // ✅ Added this
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomepageUserFragment extends Fragment {

    private TextView tvUserName;
    private EditText searchBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage_user, container, false);

        // Initialize Views
        tvUserName = view.findViewById(R.id.tv_user_name_home);
        searchBar = view.findViewById(R.id.et_search_home);
        TextView btnViewAll = view.findViewById(R.id.tv_view_all_categories);
        ImageView btnNotification = view.findViewById(R.id.btn_notification);

        // 1. DYNAMIC NAME LOGIC
        if (getActivity() != null && getActivity().getIntent() != null) {
            String name = getActivity().getIntent().getStringExtra("USER_NAME");
            if (name != null && !name.isEmpty()) {
                tvUserName.setText(name);
            } else {
                tvUserName.setText("Guest User");
            }
        }

        // 2. SEARCH FILTER LOGIC
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 3. TOAST MESSAGES
        View.OnClickListener inProgress = v ->
                Toast.makeText(getContext(), "This feature is still on progress", Toast.LENGTH_SHORT).show();

        view.findViewById(R.id.card_farmer_featured).setOnClickListener(inProgress);
        btnViewAll.setOnClickListener(inProgress);
        btnNotification.setOnClickListener(inProgress);

        // 4. CATEGORY CLICK LISTENERS
        setupCategoryClicks(view);

        // 5. ✅ POPULATE PRODUCTS
        populateProducts(view);

        return view;
    }

    private void populateProducts(View view) {
        GridLayout productGrid = view.findViewById(R.id.grid_products);

        // Sample data array
        Product[] products = {
                new Product("Sweet Mangosteen", "Saman's Finest", "₱280.00", R.drawable.logo),
                new Product("Native Kamatis", "Organic Native", "₱85.00", R.drawable.logo),
                new Product("Fresh Rambutan", "Laguna Harvest", "₱120.00", R.drawable.logo),
                new Product("Fresh Calamansi", "Pantry Staple", "₱50.00", R.drawable.logo)
        };

        // Loop to create cards
        for (Product product : products) {
            // Inflate the card
            View cardView = getLayoutInflater().inflate(R.layout.item_product_card, productGrid, false);

            // ✅ ADD THIS LOGIC: This forces the cards to be 2 columns and share space
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0; // Required for weight to work
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f); // 1f = equal weight
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED);
            params.setMargins(10, 10, 10, 10);
            cardView.setLayoutParams(params);

            // Set the data
            ((TextView) cardView.findViewById(R.id.tv_product_name)).setText(product.name);
            ((TextView) cardView.findViewById(R.id.tv_product_farm)).setText(product.farm);
            ((TextView) cardView.findViewById(R.id.tv_product_price)).setText(product.price);
            ((ImageView) cardView.findViewById(R.id.img_product)).setImageResource(product.imageRes);

            // Add the card to the GridLayout
            productGrid.addView(cardView);
        }
    }

    private void setupCategoryClicks(View v) {
        v.findViewById(R.id.cat_veg).setOnClickListener(view -> { /* Future Nav */ });
        v.findViewById(R.id.cat_fruit).setOnClickListener(view -> { /* Future Nav */ });
        v.findViewById(R.id.cat_rice).setOnClickListener(view -> { /* Future Nav */ });
        v.findViewById(R.id.cat_egg).setOnClickListener(view -> { /* Future Nav */ });
    }
}