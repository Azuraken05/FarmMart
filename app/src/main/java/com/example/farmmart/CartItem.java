package com.example.farmmart;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart_items")
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    public int cartId;

    public int productId;      // Links back to the original Product id
    public String productName;
    public String productPrice;
    public String imagePath;
    public int quantity;
    public boolean isSelected; // Controls the "Checkmark" logic in your UI

    public CartItem(int productId, String productName, String productPrice, String imagePath, int quantity, boolean isSelected) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.imagePath = imagePath;
        this.quantity = quantity;
        this.isSelected = isSelected;
    }

    // Optional: Getter for quantity logic
    public int getQuantity() {
        return quantity;
    }
}