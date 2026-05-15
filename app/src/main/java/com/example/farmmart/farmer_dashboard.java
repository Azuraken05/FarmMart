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

        // 1. Handle System Bar Padding (Fixes overlapping status/nav bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // Keep bottom 0 if nav bar is custom
            return insets;
        });

        // 2. Initialize Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottomNavigationView2);

// 3. Set Default Fragment (Loads "PRODUCTS" screen first)
        if (savedInstanceState == null) {
            replaceFragment(new fragment_farmer_products());
            bottomNavigationView.setSelectedItemId(R.id.shop_farmer); // ✅ Matches your XML ID
        }

// 4. Handle Navigation Item Clicks
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.shop_farmer) { // ✅ Matches your XML ID
                replaceFragment(new fragment_farmer_products());
                return true;
            } else if (id == R.id.home_farmer) { // ✅ Matches your XML ID
                // replaceFragment(new FarmerHomeFragment());
                return true;
            } else if (id == R.id.orders_farmer) { // ✅ Matches your XML ID
                // replaceFragment(new FarmerOrdersFragment());
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