package com.example.farmmart;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout; // ✅ ADDED: Required for the notification bell container layout
import android.widget.Toast;          // ✅ ADDED: Required for displaying the unavailable message alert
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FarmerHomeFragment extends Fragment {

    private TextView tvFarmerName, tvProductsCount, tvPendingCount, tvSalesCount, tvSeeAllOrders;
    private RelativeLayout layoutNotificationBell; // ✅ ADDED: Notification container handle
    private RecyclerView rvRecentOrders, rvMyProducts;
    private AppDatabase db;
    private int currentFarmerId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer_home, container, false);

        // Initialize display properties
        tvFarmerName = view.findViewById(R.id.tv_dashboard_farmer_name);
        tvProductsCount = view.findViewById(R.id.tv_dash_products_count);
        tvPendingCount = view.findViewById(R.id.tv_dash_pending_count);
        tvSalesCount = view.findViewById(R.id.tv_dash_sales_count);
        tvSeeAllOrders = view.findViewById(R.id.tv_dash_see_all_orders);
        layoutNotificationBell = view.findViewById(R.id.layout_notification_bell); // ✅ FIXED: Bound the notification bell container

        rvRecentOrders = view.findViewById(R.id.rv_dashboard_recent_orders);
        rvMyProducts = view.findViewById(R.id.rv_dashboard_my_products);

        if (rvRecentOrders != null) {
            rvRecentOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        if (rvMyProducts != null) {
            rvMyProducts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        }

        db = AppDatabase.getInstance(getContext());

        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        currentFarmerId = prefs.getInt("userId", -1);

        // ✅ TOAST LISTENER: Sends the prompt message when the farmer clicks the notification icon bell
        if (layoutNotificationBell != null) {
            layoutNotificationBell.setOnClickListener(v -> {
                Toast.makeText(getContext(), "This feature isn't available yet", Toast.LENGTH_SHORT).show();
            });
        }

        // ✅ LISTEN ROUTE: Clicking "See All" navigates directly to the main Orders tab
        if (tvSeeAllOrders != null) {
            tvSeeAllOrders.setOnClickListener(v -> {
                if (getActivity() instanceof farmer_dashboard) {
                    com.google.android.material.bottomnavigation.BottomNavigationView bottomNav =
                            getActivity().findViewById(R.id.bottomNavigationView2);
                    if (bottomNav != null) {
                        bottomNav.setSelectedItemId(R.id.orders_farmer);
                    }
                }
            });
        }

        loadDashboardData();

        return view;
    }

    private void loadDashboardData() {
        new Thread(() -> {
            // 1. Core user lookup data
            User farmer = db.userDao().getUserById(currentFarmerId);

            // 2. Fetch live data metrics counting tasks
            int totalProducts = db.productDao().getProductCountByFarmer(currentFarmerId);
            int pendingCount = db.orderDao().getPendingCountByFarmer(currentFarmerId);
            double totalSales = db.orderDao().getTotalSalesByFarmer(currentFarmerId);

            // 3. Fetch data list streams
            List<OrderItem> allRecentOrders = db.orderDao().getRecentOrdersForFarmer(currentFarmerId);
            List<Product> farmerProductsList = db.productDao().getProductsByFarmer(currentFarmerId);

            // ✅ FORCE LIMIT TO MAX 3 ITEMS FOR THE DASHBOARD FEED
            final List<OrderItem> limitedRecentOrders;
            if (allRecentOrders != null && allRecentOrders.size() > 3) {
                limitedRecentOrders = allRecentOrders.subList(0, 3);
            } else {
                limitedRecentOrders = allRecentOrders;
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (farmer != null && tvFarmerName != null) {
                        tvFarmerName.setText(farmer.name);
                    }

                    if (tvProductsCount != null) {
                        tvProductsCount.setText(String.valueOf(totalProducts));
                    }
                    if (tvPendingCount != null) {
                        tvPendingCount.setText(String.format("%02d", pendingCount));
                    }
                    if (tvSalesCount != null) {
                        tvSalesCount.setText(String.format("₱%.1fk", totalSales / 1000.0));
                    }

                    // ✅ ATTACH RECENT ORDERS ADAPTER (Limited to 3 items, with custom avatar initials)
                    RecentOrdersDashboardAdapter ordersAdapter = new RecentOrdersDashboardAdapter(getContext(), limitedRecentOrders);
                    if (rvRecentOrders != null) {
                        rvRecentOrders.setAdapter(ordersAdapter);
                    }

                    // Attach the horizontal products adapter
                    DashboardProductsAdapter productsAdapter = new DashboardProductsAdapter(getContext(), farmerProductsList);
                    if (rvMyProducts != null) {
                        rvMyProducts.setAdapter(productsAdapter);
                    }
                });
            }
        }).start();
    }

    // Horizontal Recycler Inline Products Adapter Block
    private static class DashboardProductsAdapter extends RecyclerView.Adapter<DashboardProductsAdapter.ViewHolder> {
        private Context ctx;
        private List<Product> list;

        public DashboardProductsAdapter(Context ctx, List<Product> list) {
            this.ctx = ctx;
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(ctx).inflate(R.layout.item_dashboard_product, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Product prod = list.get(position);
            holder.title.setText(prod.name);
            holder.price.setText(prod.price != null && prod.price.contains("₱") ? prod.price : "₱" + prod.price + "/kg");
            holder.stock.setText("STOCK " + prod.stock);

            if (prod.stock <= 0) {
                holder.badge.setText("OUT OF STOCK");
                holder.badge.setTextColor(android.graphics.Color.RED);
                holder.badge.setBackgroundColor(android.graphics.Color.parseColor("#FFEBEE"));
            } else {
                holder.badge.setText("IN STOCK");
                holder.badge.setTextColor(android.graphics.Color.parseColor("#435334"));
                holder.badge.setBackgroundColor(android.graphics.Color.parseColor("#E2F0D9"));
            }

            if (prod.imagePath != null && !prod.imagePath.isEmpty()) {
                try {
                    holder.img.setImageURI(Uri.parse(prod.imagePath));
                } catch (Exception e) {
                    holder.img.setImageResource(R.drawable.logo);
                }
            } else {
                holder.img.setImageResource(R.drawable.logo);
            }
        }

        @Override
        public int getItemCount() { return list != null ? list.size() : 0; }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView img;
            TextView title, price, stock, badge;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                img = itemView.findViewById(R.id.img_dash_prod_thumb);
                title = itemView.findViewById(R.id.tv_dash_prod_title);
                price = itemView.findViewById(R.id.tv_dash_prod_price);
                stock = itemView.findViewById(R.id.tv_dash_prod_stock);
                badge = itemView.findViewById(R.id.tv_dash_prod_badge);
            }
        }
    }
}