package com.example.farmmart;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface OrderDao {

    // ✅ Used by CheckoutActivity to save new orders
    @Insert
    void insertOrder(OrderItem order);

    // ✅ Used by FarmerOrdersFragment to update an existing order item row's properties
    @Update
    void updateOrder(OrderItem orderItem);

    // ✅ NEW STOCK MANAGEMENT: Reads current product stock levels before accepting a checkout order task
    @Query("SELECT stock FROM products WHERE id = :productId LIMIT 1")
    int getProductStock(int productId);

    // ✅ NEW STOCK MANAGEMENT: Deducts purchased amounts automatically from the product's live inventory
    @Query("UPDATE products SET stock = stock - :quantity WHERE id = :productId")
    void decreaseProductStock(int productId, int quantity);

    // ✅ Filters pending incoming orders by both the farmer's ID and the structural status string
    @Query("SELECT * FROM order_items WHERE farmerId = :farmerId AND status = :status")
    List<OrderItem> getOrdersByFarmerAndStatus(int farmerId, String status);

    // ✅ Filters by both status AND the specific User ID (Customer View)
    @Query("SELECT * FROM order_items WHERE status = :status AND userId = :userId")
    List<OrderItem> getOrdersByStatus(String status, int userId);

    // ✅ Used by OrderAdapter to update status when "Order Received" is clicked
    @Query("UPDATE order_items SET status = :newStatus WHERE orderId = :orderId")
    void updateOrderStatus(int orderId, String newStatus);

    /**
     * ✅ Farmer Dashboard feature: Sums up sales total based on Completed order states.
     * It strips currency symbols dynamically to compute real math totals safely.
     */
    @Query("SELECT IFNULL(SUM(CAST(REPLACE(REPLACE(productPrice, '₱', ''), ',', '') AS REAL) * quantity), 0.0) FROM order_items WHERE farmerId = :farmerId AND status = 'Completed'")
    double getTotalSalesByFarmer(int farmerId);

    /**
     * ✅ Farmer Dashboard feature: Tracks counter elements for incoming Pending order items
     * 🛠️ UPDATED: Changed status string from 'Pending' to 'To Ship' to match your active pipeline states!
     */
    @Query("SELECT COUNT(*) FROM order_items WHERE farmerId = :farmerId AND status = 'To Ship'")
    int getPendingCountByFarmer(int farmerId);

    /**
     * ✅ Farmer Dashboard feature: Pulls the latest 5 incoming client orders for row views
     */
    @Query("SELECT * FROM order_items WHERE farmerId = :farmerId ORDER BY orderId DESC LIMIT 5")
    List<OrderItem> getRecentOrdersForFarmer(int farmerId);

}