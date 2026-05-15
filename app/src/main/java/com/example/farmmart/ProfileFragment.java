package com.example.farmmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    TextView tvDisplayName;
    LinearLayout layoutHelp, layoutSupport;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvDisplayName = view.findViewById(R.id.tv_display_name);
        layoutHelp = view.findViewById(R.id.layout_help);
        layoutSupport = view.findViewById(R.id.layout_support);

        // --- Logic to show the User's Name ---
        // We can get this from the intent passed to the parent Activity
        if (getActivity() != null && getActivity().getIntent() != null) {
            String userName = getActivity().getIntent().getStringExtra("USER_NAME");
            if (userName != null) {
                tvDisplayName.setText(userName);
            } else {
                tvDisplayName.setText("FarmMart User"); // Fallback
            }
        }

        // --- Toast Messages for Features ---
        View.OnClickListener featureToast = v ->
                Toast.makeText(getContext(), "This feature would be added other time", Toast.LENGTH_SHORT).show();

        layoutHelp.setOnClickListener(featureToast);
        layoutSupport.setOnClickListener(featureToast);

        // You can add empty click listeners to the top buttons for now
        view.findViewById(R.id.btn_to_ship).setOnClickListener(v -> {});
        view.findViewById(R.id.btn_to_receive).setOnClickListener(v -> {});
        view.findViewById(R.id.btn_completed).setOnClickListener(v -> {});

        return view;
    }
}