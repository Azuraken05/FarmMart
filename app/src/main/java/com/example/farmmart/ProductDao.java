package com.example.farmmart;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface ProductDao {
    @Insert
    void insertProduct(Product product);

    @Update
    void updateProduct(Product product);

    @Delete
    void deleteProduct(Product product);

    /**
     * ✅ Farmer View: Returns only the products belonging to the logged-in farmer.
     * This prevents Farmers from seeing or editing each other's items.
     */
    @Query("SELECT * FROM products WHERE farmerId = :farmerId ORDER BY id DESC")
    List<Product> getProductsByFarmer(int farmerId);

    /**
     * ✅ Farmer Dashboard feature: Dynamic active inventory counter block
     */
    @Query("SELECT COUNT(*) FROM products WHERE farmerId = :farmerId")
    int getProductCountByFarmer(int farmerId);

    /**
     * ✅ NEW COUNTER: Counts ALL products listed by a specific farmer for the Products Management view
     */
    @Query("SELECT COUNT(*) FROM products WHERE farmerId = :farmerId")
    int getTotalProductCount(int farmerId);

    /**
     * ✅ NEW COUNTER: Counts only active products that are in stock (STOCK > 0) for a specific farmer
     */
    @Query("SELECT COUNT(*) FROM products WHERE farmerId = :farmerId AND stock > 0")
    int getActiveProductCount(int farmerId);

    /**
     * ✅ OPTIONAL OPTIMIZATION: Pulls only the latest 10 items for the horizontal dashboard row view
     */
    @Query("SELECT * FROM products WHERE farmerId = :farmerId ORDER BY id DESC LIMIT 10")
    List<Product> getProductsByFarmerLatest(int farmerId);

    // --- General Queries for the Shop (Customer View) ---
    // Customers can see products from ALL farmers combined.

    @Query("SELECT * FROM products ORDER BY id DESC")
    List<Product> getAllProducts();

    @Query("SELECT * FROM products WHERE category = :cat")
    List<Product> getProductsByCategory(String cat);

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    Product getProductById(int id);

    @Query("SELECT * FROM products ORDER BY id DESC")
    List<Product> getAllProductsLatest();

    @Query("SELECT * FROM products ORDER BY CAST(REPLACE(REPLACE(price, '₱', ''), ',', '') AS REAL) DESC")
    List<Product> getAllProductsByPriceHigh();

    @Query("SELECT * FROM products ORDER BY CAST(REPLACE(REPLACE(price, '₱', ''), ',', '') AS REAL) ASC")
    List<Product> getAllProductsByPriceLow();
}