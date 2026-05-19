package com.example.farmmart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private TextView tvDisplayName, tvAccountType, tvMemberSince;
    private LinearLayout layoutHelp, layoutSupport, layoutSettings;
    private LinearLayout btnToShip, btnToReceive, btnCompleted; // ✅ Added click triggers back
    private CardView btnLogout;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflates the fragment_profile layout sheet asset
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Layout Element Mappings
        tvDisplayName = view.findViewById(R.id.tv_display_name);
        tvAccountType = view.findViewById(R.id.tv_profile_account_type);
        tvMemberSince = view.findViewById(R.id.tv_profile_member_since);

        layoutHelp = view.findViewById(R.id.layout_help);
        layoutSupport = view.findViewById(R.id.layout_support);
        layoutSettings = view.findViewById(R.id.layout_settings);
        btnLogout = view.findViewById(R.id.btn_logout);

        // ✅ Initialize My Purchases Element Mappings
        btnToShip = view.findViewById(R.id.btn_to_ship);
        btnToReceive = view.findViewById(R.id.btn_to_receive);
        btnCompleted = view.findViewById(R.id.btn_completed);

        db = AppDatabase.getInstance(getContext());

        // 1. Fetch data from database and track active profile dynamically
        loadUserData(view);

        // 2. Direct "In Progress" listener notification assignment for non-functional cells
        View.OnClickListener featureInProgressToast = v ->
                Toast.makeText(getContext(), "This feature is still on progress", Toast.LENGTH_SHORT).show();

        if (layoutHelp != null) layoutHelp.setOnClickListener(featureInProgressToast);
        if (layoutSupport != null) layoutSupport.setOnClickListener(featureInProgressToast);
        if (layoutSettings != null) layoutSettings.setOnClickListener(featureInProgressToast);

        // 3. ✅ RESTORED: Click logic for Customer Purchase Management Windows
        if (btnToShip != null) {
            btnToShip.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), MyPurchasesActivity.class);
                intent.putExtra("TAB_INDEX", 0); // Open "To Ship" tab first
                startActivity(intent);
            });
        }

        if (btnToReceive != null) {
            btnToReceive.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), MyPurchasesActivity.class);
                intent.putExtra("TAB_INDEX", 1); // Open "To Receive" tab first
                startActivity(intent);
            });
        }

        if (btnCompleted != null) {
            btnCompleted.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), MyPurchasesActivity.class);
                intent.putExtra("TAB_INDEX", 2); // Open "Completed" tab first
                startActivity(intent);
            });
        }

        // 4. Operational Active Logout Management System
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            });
        }

        return view;
    }

    private void loadUserData(View rootView) {
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        int userId = preferences.getInt("userId", -1);

        if (userId != -1) {
            new Thread(() -> {
                User user = db.userDao().getUserById(userId);

                if (user != null && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (tvDisplayName != null) tvDisplayName.setText(user.name);
                        if (tvAccountType != null) tvAccountType.setText(user.role);
                        if (tvMemberSince != null) {
                            if (user.createdAt != null && !user.createdAt.isEmpty()) {
                                tvMemberSince.setText(user.createdAt);
                            } else {
                                tvMemberSince.setText("January 2026");
                            }
                        }

                        // Hide the purchase layout card dynamically if a Farmer is using the device
                        if ("Farmer".equalsIgnoreCase(user.role)) {
                            View purchaseContainer = rootView.findViewById(R.id.card_my_purchases);
                            if (purchaseContainer != null) {
                                purchaseContainer.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }).start();
        }
    }
}