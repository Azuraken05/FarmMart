package com.example.farmmart;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {

    @Insert
    void insertUser(User user);

    // Existing Login Query
    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password LIMIT 1")
    User login(String email, String password);

    // ✅ NEW: Query to fetch user details for the Profile screen
    // Note: Make sure the table name matches (user_table)
    @Query("SELECT * FROM user_table WHERE id = :id LIMIT 1")
    User getUserById(int id);
}