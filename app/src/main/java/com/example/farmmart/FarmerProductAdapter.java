package com.example.farmmart;

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

    private List<Product> productList;
    private OnProductListener listener;

    public interface OnProductListener {
        void onDelete(Product product);
        void onEdit(Product product);
    }

    public FarmerProductAdapter(List<Product> productList, OnProductListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_farmer_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.name.setText(product.name);
        holder.category.setText(product.category);
        holder.price.setText(product.price);

        // ✅ SHOW UPLOADED IMAGE
        if (product.imagePath != null && !product.imagePath.isEmpty()) {
            try {
                holder.productImg.setImageURI(Uri.parse(product.imagePath));
            } catch (Exception e) {
                holder.productImg.setImageResource(R.drawable.logo);
            }
        } else {
            holder.productImg.setImageResource(R.drawable.logo);
        }

        holder.btnDelete.setOnClickListener(v -> listener.onDelete(product));
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, category, price;
        ImageView btnEdit, btnDelete, productImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_farmer_prod_name);
            category = itemView.findViewById(R.id.tv_farmer_prod_cat);
            price = itemView.findViewById(R.id.tv_farmer_prod_price);
            btnEdit = itemView.findViewById(R.id.btn_edit_prod);
            btnDelete = itemView.findViewById(R.id.btn_delete_prod);
            productImg = itemView.findViewById(R.id.img_farmer_prod); // ✅ Linked the ImageView
        }
    }
}