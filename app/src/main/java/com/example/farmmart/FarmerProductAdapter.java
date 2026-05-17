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
    private OnProductListener listener;

    // ✅ 1. Interface for handling clicks
    public interface OnProductListener {
        void onProductClick(Product product);
    }

    // ✅ 2. Constructor accepting Context, List, and Listener
    public FarmerProductAdapter(Context context, List<Product> productList, OnProductListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the card layout for each item
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        // Bind data to views
        holder.tvName.setText(product.name);
        holder.tvPrice.setText(product.price);

        // Load image from URI if it exists
        if (product.imagePath != null && !product.imagePath.isEmpty()) {
            try {
                holder.imgProduct.setImageURI(Uri.parse(product.imagePath));
            } catch (Exception e) {
                // Fallback icon if the image URI is invalid
                holder.imgProduct.setImageResource(R.drawable.logo);
            }
        }

        // ✅ 3. Trigger the listener when the entire card is clicked
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView imgProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_product_price);
            imgProduct = itemView.findViewById(R.id.img_product);
        }
    }
}