package com.example.farmmart;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    // ✅ Make sure these three lines are exactly like this:
    public String name;
    public String role;      // This will store "Farmer" or "Customer"
    public String createdAt; // This will store the "Member Since" date

    public String email;
    public String password;
}