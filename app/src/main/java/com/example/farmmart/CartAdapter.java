package com.example.farmmart;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartList;
    private Context context;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onDataChanged(); // Used to refresh total price in the Fragment
    }

    public CartAdapter(Context context, List<CartItem> cartList, OnCartChangeListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ✅ Make sure your XML file is named item_cart_product.xml
        View v = LayoutInflater.from(context).inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartList.get(position);
        AppDatabase db = AppDatabase.getInstance(context);

        holder.name.setText(item.productName);
        holder.price.setText(item.productPrice);
        holder.qty.setText(String.valueOf(item.quantity));

        // ✅ Reset listener to null before setting state to prevent unwanted triggers during recycling
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(item.isSelected);

        if (item.imagePath != null && !item.imagePath.isEmpty()) {
            try {
                holder.image.setImageURI(Uri.parse(item.imagePath));
            } catch (Exception e) {
                holder.image.setImageResource(R.drawable.logo);
            }
        } else {
            holder.image.setImageResource(R.drawable.logo);
        }

        // ✅ Checkbox Logic: Updates database and notifies Fragment to refresh Total Price
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.isSelected = isChecked;
            new Thread(() -> {
                db.cartDao().updateCartItem(item);
                if (context instanceof FragmentActivity) {
                    ((FragmentActivity) context).runOnUiThread(() -> {
                        if (listener != null) listener.onDataChanged();
                    });
                }
            }).start();
        });

        // ✅ Plus Logic: Increases quantity and updates DB
        holder.btnPlus.setOnClickListener(v -> {
            item.quantity++;
            notifyItemChanged(holder.getAdapterPosition());
            new Thread(() -> {
                db.cartDao().updateCartItem(item);
                if (context instanceof FragmentActivity) {
                    ((FragmentActivity) context).runOnUiThread(() -> {
                        if (listener != null) listener.onDataChanged();
                    });
                }
            }).start();
        });

        // ✅ Minus Logic: Decreases quantity (minimum 1)
        holder.btnMinus.setOnClickListener(v -> {
            if (item.quantity > 1) {
                item.quantity--;
                notifyItemChanged(holder.getAdapterPosition());
                new Thread(() -> {
                    db.cartDao().updateCartItem(item);
                    if (context instanceof FragmentActivity) {
                        ((FragmentActivity) context).runOnUiThread(() -> {
                            if (listener != null) listener.onDataChanged();
                        });
                    }
                }).start();
            }
        });

        // ✅ Delete Logic: Removes item from Database and the local List
        holder.btnDelete.setOnClickListener(v -> {
            new Thread(() -> {
                db.cartDao().deleteFromCart(item);
                if (context instanceof FragmentActivity) {
                    ((FragmentActivity) context).runOnUiThread(() -> {
                        int currentPos = holder.getAdapterPosition();
                        if (currentPos != RecyclerView.NO_POSITION) {
                            cartList.remove(currentPos);
                            notifyItemRemoved(currentPos);
                            notifyItemRangeChanged(currentPos, cartList.size());
                            if (listener != null) listener.onDataChanged();
                        }
                    });
                }
            }).start();
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, qty, btnPlus, btnMinus;
        ImageView image, btnDelete;
        CheckBox checkBox;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_cart_name);
            price = itemView.findViewById(R.id.tv_cart_price);
            qty = itemView.findViewById(R.id.tv_cart_qty);
            btnPlus = itemView.findViewById(R.id.btn_cart_plus);
            btnMinus = itemView.findViewById(R.id.btn_cart_minus);
            image = itemView.findViewById(R.id.img_cart_product);
            btnDelete = itemView.findViewById(R.id.btn_delete_cart);
            checkBox = itemView.findViewById(R.id.cb_select_item);
        }
    }
}