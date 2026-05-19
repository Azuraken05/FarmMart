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

    // ✅ FIXED: Added farmerId to track which seller gets paid for this item!
    public int farmerId;

    // ✅ Updated Constructor to accept farmerId
    public CartItem(int productId, String productName, String productPrice, String imagePath, int quantity, boolean isSelected, int farmerId) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.imagePath = imagePath;
        this.quantity = quantity;
        this.isSelected = isSelected;
        this.farmerId = farmerId; // Assign the value
    }

    public int getQuantity() {
        return quantity;
    }
}