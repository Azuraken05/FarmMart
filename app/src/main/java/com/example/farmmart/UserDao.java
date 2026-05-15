package com.example.farmmart;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password LIMIT 1")
    User login(String email, String password);
}