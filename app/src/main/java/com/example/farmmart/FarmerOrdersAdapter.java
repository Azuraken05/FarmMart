package com.example.farmmart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FarmerOrdersAdapter extends RecyclerView.Adapter<FarmerOrdersAdapter.ViewHolder> {

    private final Context context;
    private final List<OrderItem> orderList;
    private final OnOrderActionListener listener;
    private final AppDatabase db;

    public interface OnOrderActionListener {
        void onShipOrderClick(OrderItem orderItem);
    }

    public FarmerOrdersAdapter(Context context, List<OrderItem> orderList, OnOrderActionListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
        this.db = AppDatabase.getInstance(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_farmer_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem order = orderList.get(position);

        // 1. Map order details using verified layout variables
        holder.tvOrderId.setText("ORD-2026-0" + order.orderId);
        holder.tvOrderTotal.setText(order.productPrice.contains("₱") ? order.productPrice : "₱" + order.productPrice);
        holder.tvItemSummary.setText(order.quantity + "x " + order.productName);

        if (holder.tvPaymentMethod != null) {
            holder.tvPaymentMethod.setText("G-CASH PAID");
        }

        // 2. Dynamic Status Badge Text
        if (holder.tvStatusBadge != null) {
            if ("To Ship".equalsIgnoreCase(order.status)) {
                holder.tvStatusBadge.setText("Pending");
            } else if ("To Receive".equalsIgnoreCase(order.status)) {
                holder.tvStatusBadge.setText("Shipping");
            } else if ("Completed".equalsIgnoreCase(order.status)) {
                holder.tvStatusBadge.setText("Completed");
            } else {
                holder.tvStatusBadge.setText(order.status);
            }
        }

        // 3. Dynamic Customer Info Thread Lookup
        new Thread(() -> {
            User customer = db.userDao().getUserById(order.userId);
            if (customer != null && holder.itemView.getContext() != null) {
                ((android.app.Activity) context).runOnUiThread(() -> {
                    if (holder.tvCustomerName != null) {
                        holder.tvCustomerName.setText(customer.name);
                    }
                });
            }
        }).start();

        // 4. Action Button Management
        if (holder.btnShipOrder != null) {
            if ("To Ship".equalsIgnoreCase(order.status)) {
                holder.btnShipOrder.setVisibility(View.VISIBLE);
                holder.btnShipOrder.setText("Ship Order");
                holder.btnShipOrder.setOnClickListener(v -> {
                    if (listener != null) listener.onShipOrderClick(order);
                });
            } else {
                holder.btnShipOrder.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderList != null ? orderList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvCustomerName, tvItemSummary, tvOrderTotal, tvPaymentMethod, tvStatusBadge;
        Button btnShipOrder;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // ✅ FIXED: Mapped IDs to match the exact XML layout structure shown in image_3c0c25.png
            tvOrderId = itemView.findViewById(R.id.tv_recent_order_id);
            tvCustomerName = itemView.findViewById(R.id.tv_recent_cust_name);
            tvItemSummary = itemView.findViewById(R.id.tv_recent_items);
            tvOrderTotal = itemView.findViewById(R.id.tv_recent_price);
            tvPaymentMethod = itemView.findViewById(R.id.tv_recent_payment);
            tvStatusBadge = itemView.findViewById(R.id.tv_recent_status_badge);
            btnShipOrder = itemView.findViewById(R.id.btn_ship_order);
        }
    }
}