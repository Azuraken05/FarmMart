package com.example.farmmart;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final Context context;
    private final List<OrderItem> orderList;

    // ✅ Track the selected star rating locally inside the dialog scope
    private int chosenRating = 0;

    public OrderAdapter(Context context, List<OrderItem> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem order = orderList.get(position);

        holder.tvProductName.setText(order.productName);
        holder.tvPrice.setText(order.productPrice);
        holder.tvStatus.setText(order.status);

        // Robust Price Calculation
        try {
            String cleanPrice = order.productPrice.replace("₱", "").replace(",", "").trim();
            double priceValue = Double.parseDouble(cleanPrice);
            double total = priceValue * order.quantity;
            holder.tvTotalSummary.setText("Total " + order.quantity + " item: ₱" + String.format("%.2f", total));
        } catch (Exception e) {
            holder.tvTotalSummary.setText("Total " + order.quantity + " item: " + order.productPrice);
        }

        // Image Loading Logic
        if (order.imagePath != null && !order.imagePath.isEmpty()) {
            try {
                holder.imgProduct.setImageURI(Uri.parse(order.imagePath));
            } catch (Exception e) {
                holder.imgProduct.setImageResource(R.drawable.logo);
            }
        } else {
            holder.imgProduct.setImageResource(R.drawable.logo);
        }

        // DYNAMIC BUTTON LOGIC BASED ON STATUS
        if ("To Receive".equals(order.status)) {
            holder.btnViewDetails.setVisibility(View.VISIBLE);
            holder.layoutCompletedActions.setVisibility(View.GONE);

            holder.btnViewDetails.setText("ORDER RECEIVED");
            holder.btnViewDetails.setOnClickListener(v -> updateStatusToCompleted(order, position));

        } else if ("To Ship".equals(order.status)) {
            holder.btnViewDetails.setVisibility(View.VISIBLE);
            holder.layoutCompletedActions.setVisibility(View.GONE);

            holder.btnViewDetails.setText("View Shipping Details");
            holder.btnViewDetails.setOnClickListener(v ->
                    Toast.makeText(context, "Farmer is preparing your order", Toast.LENGTH_SHORT).show()
            );

        } else if ("Completed".equals(order.status)) {
            holder.btnViewDetails.setVisibility(View.GONE);
            holder.layoutCompletedActions.setVisibility(View.VISIBLE);

            // ✅ Triggers your updated layout configuration
            holder.btnRate.setOnClickListener(v -> showRatingDialog(order));

            holder.btnReturnRefund.setOnClickListener(v ->
                    Toast.makeText(context, "Return/Refund request has been submitted.", Toast.LENGTH_SHORT).show()
            );
        }
    }

    /**
     * ✅ UPDATED STEP 3: Uses an array of 5 separate ImageViews to build the rating system.
     * This handles the 5-star horizontal layout perfectly on every screen.
     */
    private void showRatingDialog(OrderItem order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rating, null);
        builder.setView(dialogView);

        // Bind the product name title and confirm button
        TextView tvDialogProductTitle = dialogView.findViewById(R.id.tv_dialog_product_title);
        Button btnConfirm = dialogView.findViewById(R.id.btn_dialog_confirm);

        tvDialogProductTitle.setText(order.productName);

        // ✅ Pack the 5 XML ImageViews into a local structural array
        ImageView[] stars = new ImageView[]{
                dialogView.findViewById(R.id.star1),
                dialogView.findViewById(R.id.star2),
                dialogView.findViewById(R.id.star3),
                dialogView.findViewById(R.id.star4),
                dialogView.findViewById(R.id.star5)
        };

        // Reset your state tracker variable on every dialog popup activation
        chosenRating = 0;

        // Loop through the array to set up clean click listeners on each star
        for (int i = 0; i < stars.length; i++) {
            final int starIndex = i;
            stars[i].setOnClickListener(v -> {
                chosenRating = starIndex + 1; // E.g., clicking index 2 sets a 3-star rating

                // Color loop: Tints selected stars FarmMart Green, rest stay Light Gray
                for (int j = 0; j < stars.length; j++) {
                    if (j <= starIndex) {
                        stars[j].setColorFilter(Color.parseColor("#435334")); // FarmMart Green
                    } else {
                        stars[j].setColorFilter(Color.parseColor("#e3e3e3")); // Light Gray
                    }
                }
            });
        }

        AlertDialog dialog = builder.create();

        // Round window bounds formatting layer
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();

        // Confirm Button click action
        btnConfirm.setOnClickListener(view -> {
            if (chosenRating == 0) {
                Toast.makeText(context, "Please select a star rating first!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Success feedback tracking message
            Toast.makeText(context, "Thank you! You rated " + chosenRating + " stars for " + order.productName, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    private void updateStatusToCompleted(OrderItem order, int position) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            db.orderDao().updateOrderStatus(order.orderId, "Completed");

            if (context instanceof MyPurchasesActivity) {
                ((MyPurchasesActivity) context).runOnUiThread(() -> {
                    orderList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, orderList.size());
                    Toast.makeText(context, "Order marked as Received!", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvPrice, tvTotalSummary, tvStatus;
        ImageView imgProduct;
        Button btnViewDetails;

        LinearLayout layoutCompletedActions;
        Button btnReturnRefund, btnRate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_order_name);
            tvPrice = itemView.findViewById(R.id.tv_order_price);
            tvTotalSummary = itemView.findViewById(R.id.tv_order_total_summary);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            imgProduct = itemView.findViewById(R.id.img_order_product);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);

            layoutCompletedActions = itemView.findViewById(R.id.layout_completed_actions);
            btnReturnRefund = itemView.findViewById(R.id.btn_return_refund);
            btnRate = itemView.findViewById(R.id.btn_rate);
        }
    }
}