package com.example.farmmart;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This forces the system window to be transparent so your rounded corners look clean
        getWindow().setNavigationBarColor(android.graphics.Color.parseColor("#FCF9F4"));
        setContentView(R.layout.activity_dashboard);

        // ✅ Matches the ID in your activity_dashboard.xml
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView2);

        // 1. DEFAULT START: Load Homepage when the app opens
        if (savedInstanceState == null) {
            loadFragment(new HomepageUserFragment());
            bottomNav.setSelectedItemId(R.id.home_user);
        }

        // 2. SWITCHING LOGIC: Listen for clicks on the Bottom Nav
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.home_user) {
                selectedFragment = new HomepageUserFragment();
            } else if (itemId == R.id.profile_user) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.shop_user) {
                // selectedFragment = new ShopFragment(); // Uncomment when ready
            } else if (itemId == R.id.chat_user) {
                // selectedFragment = new ChatFragment(); // Uncomment when ready
            } else if (itemId == R.id.cart_user) {
                // selectedFragment = new CartFragment(); // Uncomment when ready
            }

            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });
    }

    // Helper method to keep the code clean
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}