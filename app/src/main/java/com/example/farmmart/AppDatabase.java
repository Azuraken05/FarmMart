package com.example.farmmart;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// ✅ FIXED: Added OrderItem.class to the entities list
@Database(entities = {User.class, Product.class, CartItem.class, OrderItem.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    public abstract ProductDao productDao();
    public abstract CartDao cartDao();
    public abstract OrderDao orderDao();

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "farm_mart_database")
                    // ✅ fallbackToDestructiveMigration will wipe old data to apply the new
                    // schema changes (like adding OrderItem) without crashing the app.
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}