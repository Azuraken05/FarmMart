package com.example.farmmart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FarmerOrdersFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TextView tvTotalOrdersBadge;
    private AppDatabase db;
    private int currentFarmerId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_farmer_orders, container, false); // Make sure layout matches your file name

        // Initialize Tab Element Variables
        tabLayout = view.findViewById(R.id.farmer_orders_tab_layout);
        viewPager = view.findViewById(R.id.farmer_orders_view_pager);
        tvTotalOrdersBadge = view.findViewById(R.id.tv_total_orders_badge);

        db = AppDatabase.getInstance(getContext());

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        currentFarmerId = sharedPreferences.getInt("userId", -1);

        // ✅ 1. Set up ViewPager Adapter to hold 3 sub-views smoothly
        OrdersPagerAdapter pagerAdapter = new OrdersPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // ✅ 2. Initialize Navigation Titles Loop
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("To Ship"); break;
                case 1: tab.setText("Shipping"); break; // Matches your design "Shipping" tab view
                case 2: tab.setText("Completed"); break;
            }
        }).attach();

        // 3. Update the item counter label dynamically
        updateGlobalItemCountBadge();

        return view;
    }

    private void updateGlobalItemCountBadge() {
        new Thread(() -> {
            // Count total rows currently assigned to this active farmer across active statuses
            int totalShipCount = db.orderDao().getPendingCountByFarmer(currentFarmerId); // Returns To Ship items count

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (tvTotalOrdersBadge != null) {
                        tvTotalOrdersBadge.setText(totalShipCount + " ITEMS");
                    }
                });
            }
        }).start();
    }

    // ✅ Fragment Slider Collection Adapter Nested Sub-Class Matrix
    private static class OrdersPagerAdapter extends FragmentStateAdapter {
        public OrdersPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Returns specific sub-lists based on tab choice (You can create variations or pass values via bundle parameters)
            return new OrderSubListFragment(position);
        }

        @Override
        public int getItemCount() {
            return 3; // Outlines our 3 pipeline paths
        }
    }
}