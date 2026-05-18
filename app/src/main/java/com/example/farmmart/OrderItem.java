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
    public String status; // "To Ship", "To Receive", "Completed"
    public int userId;    // To know which user bought it
}
