package com.example.farmmart;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

        // ✅ FIX: Removed manual "₱" to prevent double peso sign (₱₱)
        // Since your database already stores it as "₱4", this will now show correctly.
        holder.tvPrice.setText(order.productPrice);

        holder.tvStatus.setText(order.status);

        // ✅ Robust Price Calculation
        try {
            // Clean the string from currency symbols and commas to prevent crashes
            String cleanPrice = order.productPrice.replace("₱", "").replace(",", "").trim();
            double priceValue = Double.parseDouble(cleanPrice);
            double total = priceValue * order.quantity;

            // We keep the "₱" here because 'total' is a raw number (double)
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

        holder.btnViewDetails.setOnClickListener(v -> {
            // Future logic for shipping details
        });
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