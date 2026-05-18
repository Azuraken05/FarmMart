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
    private CardView btnLogout;
    private AppDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Views
        tvDisplayName = view.findViewById(R.id.tv_display_name);
        tvAccountType = view.findViewById(R.id.tv_profile_account_type);
        tvMemberSince = view.findViewById(R.id.tv_profile_member_since);

        layoutHelp = view.findViewById(R.id.layout_help);
        layoutSupport = view.findViewById(R.id.layout_support);
        layoutSettings = view.findViewById(R.id.layout_settings);
        btnLogout = view.findViewById(R.id.btn_logout);

        db = AppDatabase.getInstance(getContext());

        // 1. Load User Data from Database
        loadUserData();

        // 2. Feature Toasts
        View.OnClickListener featureToast = v ->
                Toast.makeText(getContext(), "This feature would be added soon", Toast.LENGTH_SHORT).show();

        layoutHelp.setOnClickListener(featureToast);
        layoutSupport.setOnClickListener(featureToast);
        layoutSettings.setOnClickListener(featureToast);

        // 3. Logout Functionality
        btnLogout.setOnClickListener(v -> {
            // Clear SharedPreferences session
            SharedPreferences preferences = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            // Redirect to MainActivity (Login Page)
            // FLAG_ACTIVITY_CLEAR_TASK ensures the user cannot go back to the profile
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();
        });

        // Placeholder clicks for top buttons
        view.findViewById(R.id.btn_to_ship).setOnClickListener(v -> {});
        view.findViewById(R.id.btn_to_receive).setOnClickListener(v -> {});
        view.findViewById(R.id.btn_completed).setOnClickListener(v -> {});

        return view;
    }

    private void loadUserData() {
        // Assuming you store the logged-in user's ID in SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE);
        int userId = preferences.getInt("user_id", -1);

        if (userId != -1) {
            new Thread(() -> {
                User user = db.userDao().getUserById(userId);
                if (user != null) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            tvDisplayName.setText(user.name);
                            tvAccountType.setText(user.role); // Sets "Customer" or "Farmer"

                            // Sets registration date if available
                            if (user.createdAt != null && !user.createdAt.isEmpty()) {
                                tvMemberSince.setText(user.createdAt);
                            } else {
                                tvMemberSince.setText("January 2026"); // Fallback
                            }
                        });
                    }
                }
            }).start();
        } else {
            // Fallback for intent-based data if shared prefs aren't ready
            if (getActivity() != null && getActivity().getIntent() != null) {
                String userName = getActivity().getIntent().getStringExtra("USER_NAME");
                if (userName != null) tvDisplayName.setText(userName);
            }
        }
    }
}