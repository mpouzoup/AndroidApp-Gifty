package com.example.androidapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.androidapp.R;
import com.example.androidapp.model.GiftSuggestion;
import com.example.androidapp.model.WishlistItem;
import java.util.ArrayList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GiftGuider.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_GIFTS = "gifts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_GIFTNAME = "giftname";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_TARGET = "target";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_IS_WISHLIST = "is_wishlist";
    public static final String COLUMN_PRIORITY = "priority";

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
                COLUMN_CATEGORY + " TEXT," +
                COLUMN_TARGET + " TEXT," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_PRIORITY + " INTEGER," +
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
        String[] giftList = myContext.getResources().getStringArray(R.array.initial_gifts);
        for (String giftEntry : giftList) {
            String[] parts = giftEntry.split("\\|");
            if (parts.length == 4) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_GIFTNAME, parts[0]);
                values.put(COLUMN_PRICE, Double.parseDouble(parts[1]));
                values.put(COLUMN_CATEGORY, parts[2]);
                values.put(COLUMN_TARGET, parts[3]);
                db.insert(TABLE_GIFTS, null, values);
            }
        }
    }

    public void addWishlistItem(WishlistItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GIFTNAME, item.getTitle());
        values.put(COLUMN_PRICE, item.getPrice());
        values.put(COLUMN_CATEGORY, item.getCategory());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_PRIORITY, item.getPriority());
        values.put(COLUMN_IS_WISHLIST, 1);

        db.insert(TABLE_GIFTS, null, values);
        db.close();
    }

    public void toggleWishlistStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_WISHLIST, status); // 1 για προσθήκη, 0 για αφαίρεση

        db.update(TABLE_GIFTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<WishlistItem> getAllWishlist() {
        List<WishlistItem> wishlist = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_GIFTS + " WHERE " + COLUMN_IS_WISHLIST + "=1";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                WishlistItem item = new WishlistItem(
                        cursor.getString(1),
                        cursor.getInt(0),
                        0,
                        cursor.getDouble(2),
                        cursor.getString(3),
                        cursor.getString(5),
                        cursor.getInt(6)
                );
                wishlist.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return wishlist;
    }

//    public void deleteGift(int id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_GIFTS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
//        db.close();
//    }

    public List<GiftSuggestion> getRecommendedGifts(String interest, String target) {
        List<GiftSuggestion> suggestions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_GIFTS +
                " WHERE " + COLUMN_CATEGORY + " = ? AND " + COLUMN_TARGET + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{interest, target}); //

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);
            int nameIndex = cursor.getColumnIndexOrThrow(COLUMN_GIFTNAME);
            int categoryIndex = cursor.getColumnIndexOrThrow(COLUMN_CATEGORY);
            int priceIndex = cursor.getColumnIndexOrThrow(COLUMN_PRICE);
            int targetIndex = cursor.getColumnIndexOrThrow(COLUMN_TARGET);

            do {
                GiftSuggestion suggestion = new GiftSuggestion(
                        cursor.getInt(idIndex),
                        cursor.getString(nameIndex),
                        cursor.getString(categoryIndex),
                        cursor.getDouble(priceIndex),
                        cursor.getDouble(priceIndex),
                        "", "",
                        cursor.getString(targetIndex),
                        0, ""
                );
                suggestions.add(suggestion);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return suggestions;
    }

    public void updateGiftPriority(int id, int newPriority) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRIORITY, newPriority);

        db.update(TABLE_GIFTS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}