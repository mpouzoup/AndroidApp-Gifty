package com.example.androidapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHandler extends SQLiteOpenHelper {

    // Ορισμός σταθερών για τη βάση δεδομένων
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "marketDB.db";
    public static final String TABLE_PRODUCTS = "products";

    // Στήλες του πίνακα
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PRODUCTNAME = "productname";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_MARKET = "marketname";

    // Constructor
    public MyDBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Δημιουργία του πίνακα SQL
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_PRODUCTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_PRODUCTNAME + " TEXT," +
                COLUMN_PRICE + " DOUBLE," +
                COLUMN_MARKET + " TEXT" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    // Αναβάθμιση της βάσης
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // Μέθοδος προσθήκης προϊόντος
    public void addProduct(String name, double price, String market) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCTNAME, name);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_MARKET, market);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
    }

    // Μέθοδος εύρεσης φθηνότερης τιμής για ένα προϊόν
    public Cursor getCheaperPrices(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS +
                " WHERE " + COLUMN_PRODUCTNAME + " LIKE ?" +
                " ORDER BY " + COLUMN_PRICE + " ASC";
        return db.rawQuery(query, new String[]{"%" + productName + "%"});
    }
}