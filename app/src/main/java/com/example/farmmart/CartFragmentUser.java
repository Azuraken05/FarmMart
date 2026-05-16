package com.example.farmmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartFragmentUser extends Fragment implements CartAdapter.OnCartChangeListener {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<CartItem> cartList;
    private TextView tvTotalPrice, tvItemCount, tvSelectedLabel;
    private CheckBox cbSelectAll;
    private Button btnCheckout;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ✅ Matches your layout filename: fragment_cart_user.xml
        View view = inflater.inflate(R.layout.fragment_cart_user, container, false);

        // 1. Initialize Database and Views
        db = AppDatabase.getInstance(getContext());
        recyclerView = view.findViewById(R.id.rv_cart_items);
        tvTotalPrice = view.findViewById(R.id.tv_cart_total_price);
        tvItemCount = view.findViewById(R.id.tv_cart_item_count);
        tvSelectedLabel = view.findViewById(R.id.tv_selected_label);
        cbSelectAll = view.findViewById(R.id.cb_select_all);
        btnCheckout = view.findViewById(R.id.btn_checkout);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 2. Load the Initial Data
        loadCartItems();


        // ✅ "Select All" Checkbox Logic: Toggles every item in the DB
        cbSelectAll.setOnClickListener(v -> {
            boolean isChecked = cbSelectAll.isChecked();
            new Thread(() -> {
                db.cartDao().setAllSelected(isChecked);
                // Reload items to sync the UI with the DB state
                loadCartItems();
            }).start();
        });

        // ✅ Checkout Button Logic
        btnCheckout.setOnClickListener(v -> {
            if (cartList != null && !cartList.isEmpty()) {
                // Here you would navigate to your Order Confirmation screen
                Toast.makeText(getContext(), "Proceeding to Checkout...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadCartItems() {
        new Thread(() -> {
            // Fetch all items currently in the cart_items table
            cartList = db.cartDao().getAllCartItems();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    // Re-attach adapter with the new list
                    adapter = new CartAdapter(getContext(), cartList, this);
                    recyclerView.setAdapter(adapter);
                    calculateTotal();
                });
            }
        }).start();
    }

    // ✅ Triggered by CartAdapter when quantity or individual checkbox changes
    @Override
    public void onDataChanged() {
        calculateTotal();
    }

    private void calculateTotal() {
        double total = 0;
        int selectedCount = 0;

        if (cartList != null) {
            for (CartItem item : cartList) {
                if (item.isSelected) {
                    try {
                        // Sanitizes string price like "₱20.00" or "₱1,200"
                        String cleanPrice = item.productPrice.replace("₱", "").replace(",", "").trim();
                        double price = Double.parseDouble(cleanPrice);
                        total += (price * item.quantity);
                        selectedCount++;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        final double finalTotal = total;
        final int finalCount = selectedCount;

        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                // Updates UI elements to match your reference design
                tvTotalPrice.setText("₱" + (int) finalTotal);
                tvItemCount.setText(cartList.size() + " ITEMS");
                tvSelectedLabel.setText("Selected (" + finalCount + " items)");
                btnCheckout.setText("Checkout (" + finalCount + ")");

                // Handle the Select All text/state automatically
                cbSelectAll.setText("Select All (" + cartList.size() + ")");

                // Logic to sync "Select All" checkbox state
                if (finalCount == 0) {
                    cbSelectAll.setChecked(false);
                } else if (finalCount == cartList.size()) {
                    cbSelectAll.setChecked(true);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh items whenever the user navigates back to the Cart
        loadCartItems();
    }
}