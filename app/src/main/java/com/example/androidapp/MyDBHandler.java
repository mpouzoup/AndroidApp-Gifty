package com.example.androidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GiftGuider.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_GIFTS = "gifts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_GIFTNAME = "giftname";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_INTEREST = "interest";
    public static final String COLUMN_TARGET = "target";
    public static final String COLUMN_IS_WISHLIST = "is_wishlist";

    private Context myContext;

    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GIFTS_TABLE = "CREATE TABLE " + TABLE_GIFTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_GIFTNAME + " TEXT," +
                COLUMN_PRICE + " DOUBLE," +
                COLUMN_INTEREST + " TEXT," +
                COLUMN_TARGET + " TEXT," +
                COLUMN_IS_WISHLIST + " INTEGER DEFAULT 0" + ")";
        db.execSQL(CREATE_GIFTS_TABLE);

        seedDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GIFTS);
        onCreate(db);
    }

    private void seedDatabase(SQLiteDatabase db) {
        // Get the array from strings.xml
        String[] giftList = myContext.getResources().getStringArray(R.array.initial_gifts);

        for (String giftEntry : giftList) {
            String[] parts = giftEntry.split("\\|");
            if (parts.length == 4) {
                insertGift(db, parts[0], Double.parseDouble(parts[1]), parts[2], parts[3]);
            }
        }
    }

    private void insertGift(SQLiteDatabase db, String name, double price, String interest, String target) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_GIFTNAME, name);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_INTEREST, interest);
        values.put(COLUMN_TARGET, target);
        db.insert(TABLE_GIFTS, null, values);
    }

    // Method to fetch gifts based on Interest and Target
    public Cursor getRecommendations(String interest, String target) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.query(TABLE_GIFTS,
                null,
                COLUMN_INTEREST + "=? AND " + COLUMN_TARGET + "=?",
                new String[]{interest, target},
                null, null, COLUMN_PRICE + " ASC");
    }
    public Cursor getGiftsByInterest(String interest) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_GIFTS +
                " WHERE " + COLUMN_INTEREST + " = ?";
        return db.rawQuery(query, new String[]{interest});
    }

    public void deleteGift(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GIFTS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public Cursor getWishlistItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_GIFTS + " WHERE " + COLUMN_IS_WISHLIST + "=1", null);
    }

    // Method to add or remove an item from the wishlist
    public void toggleWishlist(int id, int isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_WISHLIST, isFavorite); // 1 to add, 0 to remove
        db.update(TABLE_GIFTS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }
}