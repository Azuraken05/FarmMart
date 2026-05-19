package com.example.farmmart;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecentOrdersDashboardAdapter extends RecyclerView.Adapter<RecentOrdersDashboardAdapter.ViewHolder> {

    private Context context;
    private List<OrderItem> recentList;
    private AppDatabase db;

    public RecentOrdersDashboardAdapter(Context context, List<OrderItem> recentList) {
        this.context = context;
        this.recentList = recentList;
        this.db = AppDatabase.getInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem order = recentList.get(position);

        // 1. Basic text fields setup
        holder.tvOrderId.setText("#ORD-99" + order.orderId);
        holder.tvItemsSummary.setText(order.quantity + "x " + order.productName);
        holder.tvPrice.setText(order.productPrice.contains("₱") ? order.productPrice : "₱" + order.productPrice);
        holder.tvPaymentType.setText("CASH ON DELIVERY"); // Default status string value fallback

        // 2. Dynamic State Color Matrix Tints (Pending, Shipping, Completed matching your screenshot)
        if ("To Ship".equalsIgnoreCase(order.status)) {
            holder.tvStatusBadge.setText("Pending");
            holder.tvStatusBadge.setTextColor(Color.parseColor("#D32F2F"));
            holder.tvStatusBadge.setBackgroundColor(Color.parseColor("#FCECEB")); // Pink Tint
        } else if ("To Receive".equalsIgnoreCase(order.status)) {
            holder.tvStatusBadge.setText("Shipping");
            holder.tvStatusBadge.setTextColor(Color.parseColor("#0B5ED7"));
            holder.tvStatusBadge.setBackgroundColor(Color.parseColor("#E1F5FE")); // Blue Tint
        } else if ("Completed".equalsIgnoreCase(order.status)) {
            holder.tvStatusBadge.setText("Completed");
            holder.tvStatusBadge.setTextColor(Color.parseColor("#388E3C"));
            holder.tvStatusBadge.setBackgroundColor(Color.parseColor("#E8F5E9")); // Green Tint
        } else {
            holder.tvStatusBadge.setText(order.status);
        }

        // 3. User Async DB lookup to generate custom name strings and initials circle badges
        new Thread(() -> {
            User buyer = db.userDao().getUserById(order.userId);
            if (buyer != null && holder.itemView.getContext() != null) {
                // Generate Initials (e.g. "Maria Santos" -> "MS")
                String initials = "C";
                if (buyer.name != null && !buyer.name.trim().isEmpty()) {
                    String[] parts = buyer.name.trim().split("\\s+");
                    if (parts.length >= 2) {
                        initials = "" + parts[0].toUpperCase().charAt(0) + parts[1].toUpperCase().charAt(0);
                    } else if (parts.length == 1) {
                        initials = "" + parts[0].toUpperCase().charAt(0);
                    }
                }

                final String initialBadgeText = initials;
                ((android.app.Activity) context).runOnUiThread(() -> {
                    holder.tvCustName.setText(buyer.name);
                    holder.tvAvatarInitials.setText(initialBadgeText);
                });
            }
        }).start();
    }

    @Override
    public int getItemCount() {
        return recentList != null ? recentList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatarInitials, tvCustName, tvOrderId, tvItemsSummary, tvPrice, tvPaymentType, tvStatusBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatarInitials = itemView.findViewById(R.id.tv_recent_avatar_txt);
            tvCustName = itemView.findViewById(R.id.tv_recent_cust_name);
            tvOrderId = itemView.findViewById(R.id.tv_recent_order_id);
            tvItemsSummary = itemView.findViewById(R.id.tv_recent_items);
            tvPrice = itemView.findViewById(R.id.tv_recent_price);
            tvPaymentType = itemView.findViewById(R.id.tv_recent_payment);
            tvStatusBadge = itemView.findViewById(R.id.tv_recent_status_badge);
        }
    }
}