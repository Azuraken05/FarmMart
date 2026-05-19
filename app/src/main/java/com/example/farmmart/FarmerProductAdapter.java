package com.example.farmmart;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FarmerProductAdapter extends RecyclerView.Adapter<FarmerProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductActionListener listener;
    private boolean isFarmerView; // Tracks which UI view type matrix shell to populate

    // Interface for handling Edit/Delete (Farmer) or Details (Shop)
    public interface OnProductActionListener {
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }

    // Updated Constructor with isFarmerView boolean tracker
    public FarmerProductAdapter(Context context, List<Product> productList, boolean isFarmerView, OnProductActionListener listener) {
        this.context = context;
        this.productList = productList;
        this.isFarmerView = isFarmerView;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Choose layout structure dynamically based on user identity type context
        int layoutId = isFarmerView ? R.layout.item_farmer_product : R.layout.item_product_card;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ViewHolder(view, isFarmerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvName.setText(product.name);

        // Match the layout style clean strings (e.g., ₱20 /kg)
        if (product.price != null && !product.price.contains("₱")) {
            holder.tvPrice.setText("₱" + product.price + " /kg");
        } else {
            holder.tvPrice.setText(product.price + " /kg");
        }

        if (product.imagePath != null && !product.imagePath.isEmpty()) {
            try {
                // If you are hiding product image thumbs in management view,
                // item_farmer_product handles its display rule gracefully now via its layout attributes
                holder.imgProduct.setImageURI(Uri.parse(product.imagePath));
            } catch (Exception e) {
                holder.imgProduct.setImageResource(R.drawable.logo);
            }
        } else {
            holder.imgProduct.setImageResource(R.drawable.logo);
        }

        // Handle interactions and extra views unique to the Farmer view segment matrix
        if (isFarmerView) {
            // ✅ Bind Category text element metric
            if (holder.tvCategory != null) {
                holder.tvCategory.setText(product.description != null ? product.description : "Vegetables");
            }

            // ✅ Bind Stock count details element metric
            if (holder.tvStock != null) {
                holder.tvStock.setText(String.valueOf(product.stock));
            }

            // ✅ DYNAMIC STATUS BADGE SETUP: Handles background pill rules based on live availability parameters
            if (holder.tvStatusBadge != null) {
                if (product.stock > 0) {
                    holder.tvStatusBadge.setText("ACTIVE");
                    holder.tvStatusBadge.setTextColor(Color.parseColor("#435334")); // Dark green text matching design tokens
                    holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_active); // Light green pill shape background sheet
                } else {
                    holder.tvStatusBadge.setText("INACTIVE");
                    holder.tvStatusBadge.setTextColor(Color.parseColor("#7F7F7F")); // Dull neutral text color
                    holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_circle_action); // Fallback light gray container background tint frame
                }
            }

            // Setup action element callbacks
            if (holder.btnEdit != null) {
                holder.btnEdit.setOnClickListener(v -> listener.onEditClick(product));
            }
            if (holder.btnDelete != null) {
                holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(product));
            }
        } else {
            // Customer Shop View - Clicking the layout card shell opens product item detail sheets
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(product); // We reuse onEditClick path string parameters to launch detail sheets
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvCategory, tvStock, tvStatusBadge; // ✅ Added tvStock and tvStatusBadge fields
        ImageView imgProduct, btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView, boolean isFarmerView) {
            super(itemView);
            if (isFarmerView) {
                // IDs assigned to match item_farmer_product.xml perfectly
                tvName = itemView.findViewById(R.id.tv_farmer_prod_name);
                tvPrice = itemView.findViewById(R.id.tv_farmer_prod_price);
                tvCategory = itemView.findViewById(R.id.tv_farmer_prod_cat);
                tvStock = itemView.findViewById(R.id.tv_farmer_prod_stock); // ✅ Maps Stock parameters safely
                tvStatusBadge = itemView.findViewById(R.id.tv_product_status_badge); // ✅ Maps live Status parameters safely
                imgProduct = itemView.findViewById(R.id.img_farmer_prod);
                btnEdit = itemView.findViewById(R.id.btn_edit_prod);
                btnDelete = itemView.findViewById(R.id.btn_delete_prod);
            } else {
                // IDs assigned to match item_product_card.xml perfectly
                tvName = itemView.findViewById(R.id.tv_product_name);
                tvPrice = itemView.findViewById(R.id.tv_product_price);
                imgProduct = itemView.findViewById(R.id.img_product);
            }
        }
    }
}