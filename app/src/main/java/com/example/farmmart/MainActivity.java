package com.example.farmmart;

import android.content.Intent;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    Button signInButton;
    EditText emailEditText, passwordEditText;
    TextView registerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        signInButton = findViewById(R.id.SignIn);
        emailEditText = findViewById(R.id.email_address);
        passwordEditText = findViewById(R.id.password);
        registerTextView = findViewById(R.id.registerTextView);

        // --- 1. CLICKABLE REGISTER LOGIC ---
        String fullText = getString(R.string.no_account_register);
        SpannableString spannableString = new SpannableString(fullText);
        int start = fullText.indexOf("Register");
        int end = start + "Register".length();

        if (start != -1) {
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    Intent intent = new Intent(MainActivity.this, register.class);
                    startActivity(intent);
                }
                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(ContextCompat.getColor(MainActivity.this, R.color.farm_mart_green));
                    ds.setUnderlineText(false);
                }
            };
            spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            registerTextView.setText(spannableString);
            registerTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }

        // --- 2. LOGIN LOGIC (ADMIN + DATABASE) ---
        signInButton.setOnClickListener(v -> {
            String emailInput = emailEditText.getText().toString().trim();
            String passwordInput = passwordEditText.getText().toString().trim();

            if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // A. CHECK HARDCODED ADMIN ACCOUNTS
            if (emailInput.equals("user") && passwordInput.equals("1234")) {
                Intent intent = new Intent(MainActivity.this, Dashboard.class);
                intent.putExtra("USER_NAME", "Standard User"); // Sending admin name
                startActivity(intent);
                finish();
            }
            else if (emailInput.equals("farmer") && passwordInput.equals("1234")) {
                Intent intent = new Intent(MainActivity.this, farmer_dashboard.class);
                intent.putExtra("USER_NAME", "Admin Farmer"); // Sending admin name
                startActivity(intent);
                finish();
            }

            // B. CHECK LOCAL ROOM DATABASE
            else {
                User loggedInUser = AppDatabase.getInstance(this).userDao().login(emailInput, passwordInput);

                if (loggedInUser != null) {
                    Intent intent;
                    if (loggedInUser.role.equals("Farmer")) {
                        intent = new Intent(MainActivity.this, farmer_dashboard.class);
                    } else {
                        intent = new Intent(MainActivity.this, Dashboard.class);
                    }

                    // ✅ CRITICAL: Pass the real name from the database to the dashboard
                    intent.putExtra("USER_NAME", loggedInUser.name);

                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}