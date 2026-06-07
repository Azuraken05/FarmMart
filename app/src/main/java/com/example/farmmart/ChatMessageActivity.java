package com.example.farmmart;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog; // ✅ IMPORTED: Needed for slide menu
import java.util.ArrayList;
import java.util.List;

public class ChatMessageActivity extends AppCompatActivity {

    private RecyclerView rvBubbles;
    private EditText etInput;
    private View btnSend;
    private ImageView btnAttach; // ✅ ADDED: Reference view mapping target
    private ChatAdapter adapter;
    private List<ChatMessage> conversationFeed;
    private String counterPartyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        // 1. Extract intent payload bundles context configuration properties
        counterPartyName = getIntent().getStringExtra("SELECTED_USER_NAME");
        if (counterPartyName == null) counterPartyName = "FarmMart User";

        // 2. Map structural view layouts
        rvBubbles = findViewById(R.id.rv_live_message_bubbles);
        etInput = findViewById(R.id.et_message_box);
        btnSend = findViewById(R.id.layout_send_button_click);
        btnAttach = findViewById(R.id.btn_chat_attach); // ✅ CONNECTED: Bound paperclip view layout reference
        ImageView btnBack = findViewById(R.id.btn_chat_back);
        TextView tvHeaderName = findViewById(R.id.tv_header_user_name);
        TextView tvHeaderAvatar = findViewById(R.id.tv_header_avatar_text);

        tvHeaderName.setText(counterPartyName);
        if (!counterPartyName.trim().isEmpty()) {
            tvHeaderAvatar.setText(String.valueOf(counterPartyName.trim().toUpperCase().charAt(0)));
        } else {
            tvHeaderAvatar.setText("U");
        }

        btnBack.setOnClickListener(v -> finish());

        // 3. Build Recycler stream pipelines structures
        conversationFeed = new ArrayList<>();
        conversationFeed.add(new ChatMessage("Hello! Welcome to FarmMart chat.", "09:00 AM", false));

        adapter = new ChatAdapter(this, conversationFeed);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvBubbles.setLayoutManager(layoutManager);
        rvBubbles.setAdapter(adapter);

        // 4. On-Click Interaction Handlers Mapping
        btnSend.setOnClickListener(v -> performMessageSubmissionTransmission());

        // ✅ ATTACH BUTTON FUNCTION: Set up dialog launcher behavior click handler block
        btnAttach.setOnClickListener(v -> showAttachmentOptionsDialog());
    }

    private void performMessageSubmissionTransmission() {
        String inputMessage = etInput.getText().toString().trim();
        if (inputMessage.isEmpty()) return;

        conversationFeed.add(new ChatMessage(inputMessage, "Just Now", true));
        adapter.notifyItemInserted(conversationFeed.size() - 1);
        rvBubbles.scrollToPosition(conversationFeed.size() - 1);

        etInput.setText("");
        evaluateAutomatedResponseTriggerMatrix(inputMessage.toLowerCase());
    }

    /**
     * ✅ SHOW SLIDE-UP BOTTOM SHEET PANEL:
     * Generates a structural drawer view menu loaded with interactive sharing click behaviors.
     * Fixed with background theme overrides to show custom clean curved background drawables.
     */
    private void showAttachmentOptionsDialog() {
        // ✅ FIXED: Instantiated using the official Design Light theme style container constraint flag
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, com.google.android.material.R.style.Theme_Design_Light_BottomSheetDialog);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_attach_options, null);
        bottomSheetDialog.setContentView(dialogView);

        // ✅ FIXED: Forces the system container view window frame to drop its background color fill
        // This stops the sharp black background corners from masking your custom curve radius shapes!
        View bottomSheetInternalContainer = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheetInternalContainer != null) {
            bottomSheetInternalContainer.setBackgroundResource(android.R.color.transparent);
        }

        // Initialize action row elements from sheet layout
        LinearLayout actionFile = dialogView.findViewById(R.id.action_attach_file);
        LinearLayout actionCamera = dialogView.findViewById(R.id.action_attach_camera);
        LinearLayout actionGallery = dialogView.findViewById(R.id.action_attach_gallery);

        // Option A: Handle Document Attachments Selection Action
        actionFile.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Toast.makeText(this, "Accessing storage documents path...", Toast.LENGTH_SHORT).show();

            // Inject a mock uploaded asset bubble card row straight into conversation list frame array feed
            conversationFeed.add(new ChatMessage("📄 Attached: product_invoice.pdf (1.2 MB)", "Just Now", true));
            adapter.notifyItemInserted(conversationFeed.size() - 1);
            rvBubbles.scrollToPosition(conversationFeed.size() - 1);
        });

        // Option B: Handle Camera Launch Actions Click Tracker
        actionCamera.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Toast.makeText(this, "Opening camera lens workspace module...", Toast.LENGTH_SHORT).show();
        });

        // Option C: Handle Photo Selection Gallery Browser Activities Callouts
        actionGallery.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Toast.makeText(this, "Opening device media photos gallery library...", Toast.LENGTH_SHORT).show();
        });

        bottomSheetDialog.show();
    }

    private void evaluateAutomatedResponseTriggerMatrix(String messageText) {
        final String responseContent;

        if (messageText.contains("hi") || messageText.contains("hello") || messageText.contains("how are you")) {
            responseContent = "Hello there! Welcome to FarmMart. Hope you are having a wonderful day!";
        } else if (messageText.contains("available") || messageText.contains("stock") || messageText.contains("fresh")) {
            responseContent = "Yes, all our produce items are harvested fresh this morning and ready to ship!";
        } else if (messageText.contains("honey") || messageText.contains("wildflower")) {
            responseContent = "Our honey is unprocessed, purely raw, and cold-pressed for maximum nutrition benefits!";
        } else if (messageText.contains("delivery") || messageText.contains("shipping")) {
            responseContent = "We deliver daily around Mabuhay Rd. and across the local downtown market areas!";
        } else if (messageText.contains("price") || messageText.contains("how much")) {
            responseContent = "Our standard pricing updates are on our main catalog tab! Bulk orders get sweet deals.";
        } else {
            responseContent = "Got it! Thanks for reaching out. Let me look up the current fresh supply checklist and get back to you.";
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (conversationFeed != null && adapter != null && rvBubbles != null) {
                conversationFeed.add(new ChatMessage(responseContent, "Just Now", false));
                adapter.notifyItemInserted(conversationFeed.size() - 1);
                rvBubbles.scrollToPosition(conversationFeed.size() - 1);
            }
        }, 1200);
    }
}