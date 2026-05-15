package com.example.farmmart;

public class Product {
    String name;
    String farm;
    String price;
    int imageRes; // Resource ID for the drawable

    public Product(String name, String farm, String price, int imageRes) {
        this.name = name;
        this.farm = farm;
        this.price = price;
        this.imageRes = imageRes;
    }
}