package com.example.farmmart;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String farm;
    public String price;
    public String category;
    public int stock;
    public String imagePath; // ✅ Changed from int to String to store Image URIs

    public Product(String name, String farm, String price, String category, int stock, String imagePath) {
        this.name = name;
        this.farm = farm;
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.imagePath = imagePath;
    }

    // Getter for the name
    public String getName() {
        return name;
    }
}