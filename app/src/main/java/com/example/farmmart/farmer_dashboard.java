package com.example.farmmart;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class farmer_dashboard extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_farmer_dashboard);

        // 1. Handle System Bar Padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // 2. Initialize Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottomNavigationView2);

        // 3. Set Default Fragment to FarmerHomeFragment on initial Login load
        if (savedInstanceState == null) {
            replaceFragment(new FarmerHomeFragment());
            bottomNavigationView.setSelectedItemId(R.id.home_farmer);
        }

        // 4. Handle Navigation Item Clicks
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.home_farmer) {
                replaceFragment(new FarmerHomeFragment());
                return true;
            } else if (id == R.id.shop_farmer) {
                replaceFragment(new fragment_farmer_products());
                return true;
            } else if (id == R.id.orders_farmer) {
                // ✅ NOW ACTIVE: Loads your Orders fragment with the To Ship, Shipping, and Completed tabs!
                replaceFragment(new FarmerOrdersFragment());
                return true;

            } else if (id == R.id.chat_farmer) {
                // ✅ FIXED & CONNECTED: Added the missing chat ID handler to link up your new universal chat fragment
                replaceFragment(new fragment_chat());
                return true;

            } else if (id == R.id.profile_farmer) {
                // Loads your brand-new, standalone ProfileFragment file!
                replaceFragment(new ProfileFragment());
                return true;
            }
            return false;
        });
    }

    // Helper method to swap fragments in the FrameLayout
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}