package com.example.androidapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.androidapp.R;
import com.example.androidapp.model.Gift;
import com.example.androidapp.model.GiftRequest;
import com.example.androidapp.model.WishlistItem;
import com.example.androidapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GiftGuider.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_GIFTS = "gifts";
    private static final String COLUMN_GIFT_ID = "gift_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_HOBBY = "hobby";
    private static final String COLUMN_OCCASION = "occasion";
    private static final String COLUMN_RELATIONSHIP = "relationship";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_DESCRIPTION = "description";

    private static final String TABLE_WISHLIST = "wishlist";
    private static final String COLUMN_WISH_ID = "wish_id";

    private Context myContext;

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "(" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USERNAME + " TEXT," +
                COLUMN_EMAIL + " TEXT," +
                COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        String CREATE_GIFTS_TABLE = "CREATE TABLE " + TABLE_GIFTS + "(" +
                COLUMN_GIFT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_PRICE + " DOUBLE," +
                COLUMN_CATEGORY + " TEXT," +
                COLUMN_HOBBY + " TEXT," +
                COLUMN_OCCASION + " TEXT," +
                COLUMN_RELATIONSHIP + " TEXT," +
                COLUMN_AGE + " INTEGER," +
                COLUMN_DESCRIPTION + " TEXT" + ")";
        db.execSQL(CREATE_GIFTS_TABLE);

        String CREATE_WISHLIST_TABLE = "CREATE TABLE " + TABLE_WISHLIST + "(" +
                COLUMN_WISH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USER_ID + " INTEGER," +
                COLUMN_GIFT_ID + " INTEGER" + ")";
        db.execSQL(CREATE_WISHLIST_TABLE);

        seedDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GIFTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISHLIST);
        onCreate(db);
    }

    private void seedDatabase(SQLiteDatabase db) {
        try {
            String[] giftList = myContext.getResources().getStringArray(R.array.initial_gifts);
            for (String giftEntry : giftList) {
                String[] parts = giftEntry.split("\\|");
                if (parts.length >= 4) {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_TITLE, parts[0]);
                    values.put(COLUMN_PRICE, Double.parseDouble(parts[1]));
                    values.put(COLUMN_CATEGORY, parts[2]);
                    values.put(COLUMN_RELATIONSHIP, parts[3]);
                    db.insert(TABLE_GIFTS, null, values);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUsername());
        values.put(COLUMN_EMAIL, user.getEmail());
        values.put(COLUMN_PASSWORD, user.getPassword());

        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id != -1;
    }

    public int checkUserLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS +
                " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        int userId = -1;
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return userId;
    }

    public List<Gift> getRecommendedGifts(GiftRequest request) {
        List<Gift> suggestions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_GIFTS +
                " WHERE " + COLUMN_CATEGORY + " = ? AND " + COLUMN_PRICE + " <= ?";

        Cursor cursor = db.rawQuery(query, new String[]{request.getCategory(), String.valueOf(request.getMaxPrice())});

        if (cursor.moveToFirst()) {
            do {
                Gift gift = new Gift(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(8),
                        cursor.getDouble(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getInt(7),
                        ""
                );
                suggestions.add(gift);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return suggestions;
    }

    public void addGiftToWishlist(int userId, int giftId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_GIFT_ID, giftId);

        db.insert(TABLE_WISHLIST, null, values);
        db.close();
    }

    public void removeGiftFromWishlist(int userId, int giftId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WISHLIST, COLUMN_USER_ID + "=? AND " + COLUMN_GIFT_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(giftId)});
        db.close();
    }

    public List<WishlistItem> getUserWishlist(int userId) {
        List<WishlistItem> wishlist = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT w." + COLUMN_WISH_ID + ", g." + COLUMN_GIFT_ID + ", g." + COLUMN_TITLE + ", g." + COLUMN_PRICE +
                " FROM " + TABLE_WISHLIST + " w " +
                " JOIN " + TABLE_GIFTS + " g ON w." + COLUMN_GIFT_ID + " = g." + COLUMN_GIFT_ID +
                " WHERE w." + COLUMN_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                WishlistItem item = new WishlistItem(
                        cursor.getInt(0),
                        userId,
                        cursor.getInt(1),
                        cursor.getString(2),
                        cursor.getDouble(3)
                );
                wishlist.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return wishlist;
    }
}