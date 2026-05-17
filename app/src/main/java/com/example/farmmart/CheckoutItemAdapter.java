package com.example.farmmart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CheckoutItemAdapter extends RecyclerView.Adapter<CheckoutItemAdapter.ViewHolder> {
    private List<CartItem> items;

    public CheckoutItemAdapter(List<CartItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checkout_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.name.setText(item.productName + " (x" + item.quantity + ")");
        holder.price.setText(item.productPrice);
        if (item.imagePath != null && !item.imagePath.isEmpty()) {
            holder.image.setImageURI(Uri.parse(item.imagePath));
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageView image;
        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.tv_checkout_item_name);
            price = v.findViewById(R.id.tv_checkout_item_price);
            image = v.findViewById(R.id.img_checkout_item);
        }
    }
}