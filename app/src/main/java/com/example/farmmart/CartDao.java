package com.example.farmmart;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface CartDao {

    @Insert
    void addToCart(CartItem item);

    @Update
    void updateCartItem(CartItem item);

    @Delete
    void deleteFromCart(CartItem item);

    @Query("SELECT * FROM cart_items ORDER BY cartId DESC")
    List<CartItem> getAllCartItems();

    // ✅ Used for the "Select All" checkbox logic
    @Query("UPDATE cart_items SET isSelected = :selected")
    void setAllSelected(boolean selected);

    // ✅ Used for the "Delete" button (trash icon)
    @Query("DELETE FROM cart_items WHERE cartId = :id")
    void deleteById(int id);

    // ✅ Used to fetch only items the user wants to buy (for Order Summary)
    @Query("SELECT * FROM cart_items WHERE isSelected = 1")
    List<CartItem> getSelectedItems();

    // ✅ NEW: Used when "Place Order" is clicked to clear the checkout items
    @Query("DELETE FROM cart_items WHERE isSelected = 1")
    void deleteSelectedItems();
}