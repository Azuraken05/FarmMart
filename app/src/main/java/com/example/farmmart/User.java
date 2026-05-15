package com.example.farmmart;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String fullName;
    public String email;
    public String password;
    public String role; // Stores "Farmer" or "Buyer"
}