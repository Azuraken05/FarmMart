package com.example.farmmart;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface OrderDao {
    @Insert
    void insertOrder(OrderItem order); // ✅ This is what CheckoutActivity is looking for

    @Query("SELECT * FROM order_items WHERE status = :status")
    List<OrderItem> getOrdersByStatus(String status);
}