package com.example.farmmart;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List; // ✅ ADDED: Required to return a list of users

@Dao
public interface UserDao {

    @Insert
    void insertUser(User user);

    // Existing Login Query
    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password LIMIT 1")
    User login(String email, String password);

    // ✅ NEW: Query to fetch user details for the Profile screen
    @Query("SELECT * FROM user_table WHERE id = :id LIMIT 1")
    User getUserById(int id);

    // ✅ FIXED: Added query to fetch everyone for the universal chat list matching user_table
    @Query("SELECT * FROM user_table")
    List<User> getAllUsers();

}