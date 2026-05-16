package com.example.farmmart;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Dashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Clean navigation bar color for your UI
        getWindow().setNavigationBarColor(android.graphics.Color.parseColor("#FCF9F4"));
        setContentView(R.layout.activity_dashboard);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView2);

        // 1. DEFAULT START
        if (savedInstanceState == null) {
            loadFragment(new HomepageUserFragment(), false);
            bottomNav.setSelectedItemId(R.id.home_user);
        }

        // 2. SWITCHING LOGIC
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.home_user) {
                selectedFragment = new HomepageUserFragment();
            } else if (itemId == R.id.cart_user) {
                // ✅ Now loads your new Cart Fragment
                selectedFragment = new CartFragmentUser();
            } else if (itemId == R.id.profile_user) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.shop_user) {
                // selectedFragment = new ShopFragment();
            } else if (itemId == R.id.chat_user) {
                // selectedFragment = new ChatFragment();
            }

            if (selectedFragment != null) {
                // We pass 'true' for the cart so the back button works
                boolean addToBackStack = (itemId == R.id.cart_user);
                loadFragment(selectedFragment, addToBackStack);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);

        // ✅ This allows the back button to return to the previous fragment
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }
}