package com.example.farmmart;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // Add this import
import android.widget.TextView;
import android.widget.Toast; // Add this import

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class register extends AppCompatActivity {

    Button btnFarmer, btnBuyer, btnCreateAccount; // Added btnCreateAccount
    EditText etFullName, etEmail, etPassword; // 1. Added EditText declarations
    String selectedRole = "Farmer";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 2. Initialize the views (Make sure IDs match your XML)
        btnFarmer = findViewById(R.id.btnFarmer);
        btnBuyer = findViewById(R.id.btnBuyer);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        updateRoleSelection();

        btnFarmer.setOnClickListener(v -> {
            selectedRole = "Farmer";
            updateRoleSelection();
        });

        btnBuyer.setOnClickListener(v -> {
            selectedRole = "Buyer";
            updateRoleSelection();
        });

        // 3. PASTE THE DATABASE LOGIC HERE
        btnCreateAccount.setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Create the User object
                User newUser = new User();
                newUser.fullName = name;
                newUser.email = email;
                newUser.password = password;
                newUser.role = selectedRole;

                // Save to Room Database
                AppDatabase.getInstance(this).userDao().insertUser(newUser);

                Toast.makeText(this, "Account Registered Successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Goes back to MainActivity (Login)
            }
        });

        // --- Your existing Login Link code ---
        TextView loginLink = findViewById(R.id.loginLink);
        String text = "Already have an account? Log In";
        SpannableString ss = new SpannableString(text);
        int start = text.indexOf("Log In");
        int end = start + "Log In".length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                finish();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(register.this, R.color.farm_mart_green));
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        loginLink.setText(ss);
        loginLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void updateRoleSelection() {
        if (selectedRole.equals("Farmer")) {
            btnFarmer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.farm_mart_green)));
            btnFarmer.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            btnBuyer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.darker_gray)));
            btnBuyer.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        } else {
            btnBuyer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.farm_mart_green)));
            btnBuyer.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            btnFarmer.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, android.R.color.darker_gray)));
            btnFarmer.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }
    }
}