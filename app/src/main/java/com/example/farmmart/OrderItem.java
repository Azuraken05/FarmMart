package com.example.farmmart;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_items")
public class OrderItem {

    @PrimaryKey(autoGenerate = true)
    public int orderId;

    public String productName;
    public String productPrice;
    public String imagePath;
    public int quantity;

    // Status can be: "To Ship", "To Receive", or "Completed"
    public String status;

    // Links this order to the specific Customer ID from the User table
    public int userId;

    // ✅ FIXED: Added farmerId so Room can track sales and pending counts for each seller!
    public int farmerId;
}