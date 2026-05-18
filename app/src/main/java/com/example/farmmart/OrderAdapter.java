package com.example.farmmart;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context context;
    private List<OrderItem> orderList;

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

        // ✅ Robust Price Calculation
        try {
            String cleanPrice = order.productPrice.replace("₱", "").replace(",", "").trim();
            double priceValue = Double.parseDouble(cleanPrice);
            double total = priceValue * order.quantity;
            holder.tvTotalSummary.setText("TOTAL " + order.quantity + " ITEM: ₱" + String.format("%.2f", total));
        } catch (Exception e) {
            holder.tvTotalSummary.setText("TOTAL " + order.quantity + " ITEM: " + order.productPrice);
        }

        // ✅ Image Loading Logic
        if (order.imagePath != null && !order.imagePath.isEmpty()) {
            try {
                holder.imgProduct.setImageURI(Uri.parse(order.imagePath));
            } catch (Exception e) {
                holder.imgProduct.setImageResource(R.drawable.logo);
            }
        } else {
            holder.imgProduct.setImageResource(R.drawable.logo);
        }

        // ✅ Button Logic based on Status
        if ("To Receive".equals(order.status)) {
            holder.btnViewDetails.setText("ORDER RECEIVED");
            holder.btnViewDetails.setOnClickListener(v -> {
                updateStatusToCompleted(order, position);
            });
        } else if ("To Ship".equals(order.status)) {
            holder.btnViewDetails.setText("View Shipping Details");
            holder.btnViewDetails.setOnClickListener(v -> {
                Toast.makeText(context, "Farmer is preparing your order", Toast.LENGTH_SHORT).show();
            });
        } else {
            // Completed state
            holder.btnViewDetails.setText("Order Completed");
            holder.btnViewDetails.setEnabled(false);
            holder.btnViewDetails.setAlpha(0.5f); // Make it look disabled
        }
    }

    // ✅ Helper method to update database and refresh list
    private void updateStatusToCompleted(OrderItem order, int position) {
        new Thread(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            // Update status in database
            db.orderDao().updateOrderStatus(order.orderId, "Completed");

            // Update the UI
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tv_order_name);
            tvPrice = itemView.findViewById(R.id.tv_order_price);
            tvTotalSummary = itemView.findViewById(R.id.tv_order_total_summary);
            tvStatus = itemView.findViewById(R.id.tv_order_status);
            imgProduct = itemView.findViewById(R.id.img_order_product);
            btnViewDetails = itemView.findViewById(R.id.btn_view_details);
        }
    }
}