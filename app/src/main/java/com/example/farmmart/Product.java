package com.example.farmmart;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product {
    @PrimaryKey(autoGenerate = true)
    public int id; // ✅ The primary key used as "product.id" in other files

    public String name;
    public String farm;
    public String price;
    public String category;
    public int stock;
    public String imagePath;
    public String description;

    // Constructor
    public Product(String name, String farm, String price, String category, int stock, String imagePath, String description) {
        this.name = name;
        this.farm = farm;
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.imagePath = imagePath;
        this.description = description;
    }

    // Getter for the name
    public String getName() {
        return name;
    }
}