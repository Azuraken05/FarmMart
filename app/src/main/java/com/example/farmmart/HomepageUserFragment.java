package com.example.farmmart;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.List;

public class HomepageUserFragment extends Fragment {

    private TextView tvUserName;
    private EditText searchBar;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage_user, container, false);

        tvUserName = view.findViewById(R.id.tv_user_name_home);
        searchBar = view.findViewById(R.id.et_search_home);
        TextView btnViewAll = view.findViewById(R.id.tv_view_all_categories);
        ImageView btnNotification = view.findViewById(R.id.btn_notification);

        db = AppDatabase.getInstance(getContext());

        if (getActivity() != null && getActivity().getIntent() != null) {
            String name = getActivity().getIntent().getStringExtra("USER_NAME");
            tvUserName.setText(name != null && !name.isEmpty() ? name : "Guest User");
        }

        View.OnClickListener inProgress = v ->
                Toast.makeText(getContext(), "This feature is still on progress", Toast.LENGTH_SHORT).show();

        view.findViewById(R.id.card_farmer_featured).setOnClickListener(inProgress);
        btnViewAll.setOnClickListener(inProgress);
        btnNotification.setOnClickListener(inProgress);

        setupCategoryClicks(view);
        populateProducts(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getView() != null) {
            populateProducts(getView());
        }
    }

    private void populateProducts(View view) {
        GridLayout productGrid = view.findViewById(R.id.grid_products);
        productGrid.removeAllViews();

        List<Product> products = db.productDao().getAllProducts();

        for (Product product : products) {
            View cardView = getLayoutInflater().inflate(R.layout.item_product_card, productGrid, false);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(10, 10, 10, 10);
            cardView.setLayoutParams(params);

            // Binding Data
            ((TextView) cardView.findViewById(R.id.tv_product_name)).setText(product.name);
            ((TextView) cardView.findViewById(R.id.tv_product_farm)).setText(product.farm);
            ((TextView) cardView.findViewById(R.id.tv_product_price)).setText(product.price);

            // ✅ Bind Description
            TextView tvDesc = cardView.findViewById(R.id.tv_product_description);
            tvDesc.setText(product.description != null ? product.description : "No description available.");

            ImageView productImg = cardView.findViewById(R.id.img_product);
            if (product.imagePath != null && !product.imagePath.isEmpty()) {
                try {
                    productImg.setImageURI(Uri.parse(product.imagePath));
                } catch (Exception e) {
                    productImg.setImageResource(R.drawable.logo);
                }
            } else {
                productImg.setImageResource(R.drawable.logo);
            }

            productGrid.addView(cardView);
        }
    }

    private void setupCategoryClicks(View v) {
        v.findViewById(R.id.cat_veg).setOnClickListener(view -> {});
        v.findViewById(R.id.cat_fruit).setOnClickListener(view -> {});
        v.findViewById(R.id.cat_rice).setOnClickListener(view -> {});
        v.findViewById(R.id.cat_egg).setOnClickListener(view -> {});
    }
}