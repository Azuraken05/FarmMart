package com.example.farmmart;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// ✅ Added Product.class and bumped version to 2
@Database(entities = {User.class, Product.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();
    // ✅ Added the ProductDao
    public abstract ProductDao productDao();

    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "farm_mart_database")
                    // ✅ .fallbackToDestructiveMigration() helps during development
                    // if you change the schema again.
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}