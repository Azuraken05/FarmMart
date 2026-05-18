package com.example.farmmart;

import android.content.Context;
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
    private boolean isFarmerView; // ✅ Added to track which UI to show

    // Interface for handling Edit/Delete (Farmer) or Details (Shop)
    public interface OnProductActionListener {
        void onEditClick(Product product);
        void onDeleteClick(Product product);
    }

    // ✅ Updated Constructor with isFarmerView boolean
    public FarmerProductAdapter(Context context, List<Product> productList, boolean isFarmerView, OnProductActionListener listener) {
        this.context = context;
        this.productList = productList;
        this.isFarmerView = isFarmerView;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ✅ Choose layout based on context
        int layoutId = isFarmerView ? R.layout.item_farmer_product : R.layout.item_product_card;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ViewHolder(view, isFarmerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvName.setText(product.name);
        holder.tvPrice.setText(product.price + " /kg");

        if (product.imagePath != null && !product.imagePath.isEmpty()) {
            try {
                holder.imgProduct.setImageURI(Uri.parse(product.imagePath));
            } catch (Exception e) {
                holder.imgProduct.setImageResource(R.drawable.logo);
            }
        }

        // ✅ Handle interactions based on View Type
        if (isFarmerView) {
            // Farmer Management View
            if (holder.btnEdit != null) {
                holder.btnEdit.setOnClickListener(v -> listener.onEditClick(product));
            }
            if (holder.btnDelete != null) {
                holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(product));
            }
        } else {
            // Customer Shop View - Clicking the card opens details
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(product); // We reuse onEditClick to trigger Detail Activity
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvCategory;
        ImageView imgProduct, btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView, boolean isFarmerView) {
            super(itemView);
            if (isFarmerView) {
                // ✅ IDs for item_farmer_product.xml
                tvName = itemView.findViewById(R.id.tv_farmer_prod_name);
                tvPrice = itemView.findViewById(R.id.tv_farmer_prod_price);
                tvCategory = itemView.findViewById(R.id.tv_farmer_prod_cat);
                imgProduct = itemView.findViewById(R.id.img_farmer_prod);
                btnEdit = itemView.findViewById(R.id.btn_edit_prod);
                btnDelete = itemView.findViewById(R.id.btn_delete_prod);
            } else {
                // ✅ IDs for item_product_card.xml
                tvName = itemView.findViewById(R.id.tv_product_name);
                tvPrice = itemView.findViewById(R.id.tv_product_price);
                imgProduct = itemView.findViewById(R.id.img_product);
            }
        }
    }
}