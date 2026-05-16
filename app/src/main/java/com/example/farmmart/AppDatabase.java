package com.example.farmmart;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// ✅ Includes all three tables: User, Product, and CartItem
@Database(entities = {User.class, Product.class, CartItem.class}, version = 4)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract ProductDao productDao();

    // ✅ This allows your Cart Module to talk to the database
    public abstract CartDao cartDao();

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "farm_mart_database")
                    // ✅ fallbackToDestructiveMigration will wipe old data to apply the new
                    // CartItem table structure without crashing the app.
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}