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
    public String imagePath;
    public String description;

    // ✅ Added farmerId to track which farmer added this product
    public int farmerId;

    // Updated Constructor to include farmerId
    public Product(String name, String farm, String price, String category, int stock, String imagePath, String description, int farmerId) {
        this.name = name;
        this.farm = farm;
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.imagePath = imagePath;
        this.description = description;
        this.farmerId = farmerId;
    }

    // Getter for the name
    public String getName() {
        return name;
    }
}