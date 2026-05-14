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
    TextView registerTextView; // Added for the clickable text

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

        // ✅ Initialize views
        signInButton = findViewById(R.id.SignIn);
        emailEditText = findViewById(R.id.email_address);
        passwordEditText = findViewById(R.id.password);
        registerTextView = findViewById(R.id.registerTextView); // Make sure this ID matches your XML

        // --- CLICKABLE REGISTER LOGIC ---
        String fullText = getString(R.string.no_account_register);
        SpannableString spannableString = new SpannableString(fullText);

        int start = fullText.indexOf("Register");
        int end = start + "Register".length();

        if (start != -1) { // Check to ensure "Register" exists in the string
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    // Navigate to your registration screen
                    // Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    // startActivity(intent);
                    Toast.makeText(MainActivity.this, "Opening Registration...", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    // Use your custom green color for "Register"
                    ds.setColor(ContextCompat.getColor(MainActivity.this, R.color.farm_mart_green));
                    ds.setUnderlineText(false); // Clean look without underline
                }
            };

            spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            registerTextView.setText(spannableString);
            registerTextView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        // --------------------------------

        signInButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.equals("user") && password.equals("1234")) {
                Intent intent = new Intent(MainActivity.this, Dashboard.class);
                startActivity(intent);
                finish();
            } else if (email.equals("farmer") && password.equals("1234")) {
                Intent intent = new Intent(MainActivity.this, farmer_dashboard.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}