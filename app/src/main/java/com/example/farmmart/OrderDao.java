package com.example.farmmart;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface OrderDao {

    // ✅ Used by CheckoutActivity to save new orders
    @Insert
    void insertOrder(OrderItem order);

    // ✅ UPDATED: Filters by both status AND the specific User ID
    @Query("SELECT * FROM order_items WHERE status = :status AND userId = :userId")
    List<OrderItem> getOrdersByStatus(String status, int userId);

    // ✅ Used by OrderAdapter to update status when "Order Received" is clicked
    @Query("UPDATE order_items SET status = :newStatus WHERE orderId = :orderId")
    void updateOrderStatus(int orderId, String newStatus);
}