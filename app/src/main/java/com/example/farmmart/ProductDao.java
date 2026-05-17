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

    @Query("SELECT * FROM products ORDER BY id DESC")
    List<Product> getAllProducts();

    @Query("SELECT * FROM products WHERE category = :cat")
    List<Product> getProductsByCategory(String cat);

    // ✅ Added this to support the Edit functionality
    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    Product getProductById(int id);

    @Query("SELECT * FROM products ORDER BY id DESC")
    List<Product> getAllProductsLatest();

    @Query("SELECT * FROM products ORDER BY CAST(REPLACE(REPLACE(price, '₱', ''), ',', '') AS REAL) DESC")
    List<Product> getAllProductsByPriceHigh();

    @Query("SELECT * FROM products ORDER BY CAST(REPLACE(price, '₱', '') AS REAL) ASC")
    List<Product> getAllProductsByPriceLow();

}