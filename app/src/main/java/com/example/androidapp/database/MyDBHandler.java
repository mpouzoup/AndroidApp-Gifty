package com.example.androidapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.androidapp.R;
import com.example.androidapp.model.Gift;
import com.example.androidapp.model.GiftRequest;
import com.example.androidapp.model.ReminderModel;
import com.example.androidapp.model.WishlistItem;
import com.example.androidapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class MyDBHandler extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GiftGuider.db";
    private static final int DATABASE_VERSION = 5; // 🟢 ΑΥΞΗΣΗ VERSION ΣΕ 5

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
    private static final String COLUMN_AGE = "age"; // 🟢 ΕΠΑΝΑΦΟΡΑ ΣΤΗΛΗΣ ΗΛΙΚΙΑΣ
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMAGE_PATH = "image_path";

    private static final String TABLE_WISHLIST = "wishlist";
    private static final String COLUMN_WISH_ID = "wish_id";

    private static final String TABLE_REMINDERS = "reminders";
    private static final String COLUMN_REMINDER_ID = "reminder_id";
    private static final String COLUMN_USER_ID_FK = "user_id";
    private static final String COLUMN_REMINDER_EVENT = "event_name";
    private static final String COLUMN_REMINDER_DATE = "event_date";

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

        // 🟢 Προσθήκη COLUMN_AGE ξανά στον πίνακα
        String CREATE_GIFTS_TABLE = "CREATE TABLE " + TABLE_GIFTS + "(" +
                COLUMN_GIFT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_TITLE + " TEXT," +
                COLUMN_PRICE + " DOUBLE," +
                COLUMN_CATEGORY + " TEXT," +
                COLUMN_HOBBY + " TEXT," +
                COLUMN_OCCASION + " TEXT," +
                COLUMN_RELATIONSHIP + " TEXT," +
                COLUMN_AGE + " INTEGER," +
                COLUMN_DESCRIPTION + " TEXT," +
                COLUMN_IMAGE_PATH + " TEXT" + ")";
        db.execSQL(CREATE_GIFTS_TABLE);

        String CREATE_WISHLIST_TABLE = "CREATE TABLE " + TABLE_WISHLIST + "(" +
                COLUMN_WISH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USER_ID + " INTEGER," +
                COLUMN_GIFT_ID + " INTEGER" + ")";
        db.execSQL(CREATE_WISHLIST_TABLE);

        String CREATE_REMINDERS_TABLE = "CREATE TABLE " + TABLE_REMINDERS + "(" +
                COLUMN_REMINDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USER_ID_FK + " INTEGER," +
                COLUMN_REMINDER_EVENT + " TEXT," +
                COLUMN_REMINDER_DATE + " TEXT" + ")";
        db.execSQL(CREATE_REMINDERS_TABLE);

        seedDatabase(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GIFTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISHLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
        onCreate(db);
    }

    public List<ReminderModel> getUserReminders(int userId) {
        List<ReminderModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_REMINDERS + " WHERE " + COLUMN_USER_ID_FK + " = ? ORDER BY " + COLUMN_REMINDER_DATE + " ASC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            do {
                list.add(new ReminderModel(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void deleteReminder(int reminderId) {
        if (reminderId <= 0) return;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_REMINDERS, COLUMN_REMINDER_ID + " = ?", new String[]{String.valueOf(reminderId)});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) db.close();
        }
    }

    public User getUserById(int userId) {
        User user = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_USERNAME + ", " + COLUMN_EMAIL + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor.moveToFirst()) {
            user = new User(cursor.getString(0), cursor.getString(1), "");
        }
        cursor.close();
        db.close();
        return user;
    }

    private void seedDatabase(SQLiteDatabase db) {
        try {
            String[] giftList = myContext.getResources().getStringArray(R.array.initial_gifts);
            for (String giftEntry : giftList) {
                String[] parts = giftEntry.split("\\|");
                if (parts.length >= 4) {
                    ContentValues values = new ContentValues();
                    values.put(COLUMN_TITLE, parts[0]);

                    try {
                        values.put(COLUMN_PRICE, Double.parseDouble(parts[1]));
                    } catch (NumberFormatException e) {
                        values.put(COLUMN_PRICE, 0.0);
                    }

                    values.put(COLUMN_CATEGORY, parts[2]);
                    values.put(COLUMN_RELATIONSHIP, parts[3]);

                    // 🟢 Διάβασμα ορίου ηλικίας από τη θέση 4
                    if (parts.length >= 5) {
                        try {
                            values.put(COLUMN_AGE, Integer.parseInt(parts[4]));
                        } catch (NumberFormatException e) {
                            values.put(COLUMN_AGE, 0);
                        }
                    } else {
                        values.put(COLUMN_AGE, 0);
                    }

                    // Διάβασμα εικόνας από τη θέση 5 (αν υπάρχει)
                    if (parts.length >= 6) {
                        values.put(COLUMN_IMAGE_PATH, parts[5]);
                    }
                    db.insert(TABLE_GIFTS, null, values);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Gift> getRecommendedGifts(GiftRequest request) {
        List<Gift> suggestions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // 1. Καθαρισμός Κατηγοριών
        String rawCategories = request.getCategory();
        if (rawCategories == null || rawCategories.trim().isEmpty()) {
            rawCategories = "all";
        }

        String[] categories = rawCategories.split(",\\s*");
        StringBuilder categoryQuery = new StringBuilder();
        List<String> queryArgs = new ArrayList<>();

        // Χτίσιμο του Query για τις κατηγορίες
        if (categories.length == 1 && categories[0].equals("all")) {
            categoryQuery.append("1=1");
        } else {
            categoryQuery.append("(");
            for (int i = 0; i < categories.length; i++) {
                categoryQuery.append("category LIKE ?");
                queryArgs.add("%" + categories[i].trim() + "%"); // 👈 ΑΥΤΑ ΜΠΑΙΝΟΥΝ ΠΡΩΤΑ ΣΤΗ ΛΙΣΤΑ
                if (i < categories.length - 1) {
                    categoryQuery.append(" OR ");
                }
            }
            categoryQuery.append(")");
        }

        // 2. 🟢 ΤΟ ΕΞΥΠΝΟ & ΚΑΘΑΡΟ SQL QUERY
        // Χρησιμοποιούμε "INSTR" ή "LIKE" με ειδικό τρόπο για να ξεχωρίζει το Friend από το Boyfriend!
        String finalQuery = "SELECT * FROM gifts WHERE " + categoryQuery.toString() +
                " AND price <= ?" +
                " AND age <= ?" +
                " AND (relationship LIKE ? OR relationship = 'general' OR relationship = '')";

        // 3. 🟢 ΠΡΟΣΘΗΚΗ ΥΠΟΛΟΙΠΩΝ ΟΡΙΣΜΑΤΩΝ ΜΕ ΤΗΝ ΑΥΣΤΗΡΑ ΣΩΣΤΗ ΣΕΙΡΑ

        // Γεμίζει το: price <= ?
        queryArgs.add(String.valueOf(request.getMaxPrice()));

        // Γεμίζει το: age <= ?
        queryArgs.add(String.valueOf(request.getAge()));

        // Γεμίζει το: relationship LIKE ?
        // Προσθέτουμε κόμματα στα boundries ή ελέγχουμε καθαρά το string για να μην μπερδεύεται το Friend με το Boyfriend
        String relation = request.getRelationship().trim();
        queryArgs.add("%" + relation + "%");

        // Μετατροπή της λίστας σε Array για την SQLite
        String[] argsArray = queryArgs.toArray(new String[0]);

        // Εκτέλεση του Query
        Cursor cursor = db.rawQuery(finalQuery, argsArray);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow("gift_id");
            int titleIndex = cursor.getColumnIndexOrThrow("title");
            int priceIndex = cursor.getColumnIndexOrThrow("price");
            int categoryIndex = cursor.getColumnIndexOrThrow("category");
            int hobbyIndex = cursor.getColumnIndexOrThrow("hobby");
            int occasionIndex = cursor.getColumnIndexOrThrow("occasion");
            int relationshipIndex = cursor.getColumnIndexOrThrow("relationship");
            int ageIndex = cursor.getColumnIndexOrThrow("age");
            int descriptionIndex = cursor.getColumnIndexOrThrow("description");
            int imageIndex = cursor.getColumnIndexOrThrow("image_path");

            do {
                Gift gift = new Gift(
                        cursor.getInt(idIndex),
                        cursor.getString(titleIndex),
                        cursor.getString(descriptionIndex),
                        cursor.getDouble(priceIndex),
                        cursor.getString(categoryIndex),
                        cursor.getString(hobbyIndex),
                        cursor.getString(occasionIndex),
                        cursor.getString(relationshipIndex),
                        cursor.getInt(ageIndex),
                        cursor.getString(imageIndex)
                );
                suggestions.add(gift);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return suggestions;
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
        String query = "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        int userId = -1;
        if (cursor.moveToFirst()) userId = cursor.getInt(0);
        cursor.close();
        db.close();
        return userId;
    }

    public void addReminder(ReminderModel reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", reminder.getUserId());
        values.put("event_name", reminder.getEventName());
        values.put("event_date", reminder.getEventDate());
        db.insert("reminders", null, values);
        db.close();
    }

    public boolean addGiftToWishlist(int userId, int giftId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String checkQuery = "SELECT 1 FROM " + TABLE_WISHLIST + " WHERE " + COLUMN_USER_ID + "=? AND " + COLUMN_GIFT_ID + "=?";
        Cursor cursor = db.rawQuery(checkQuery, new String[]{String.valueOf(userId), String.valueOf(giftId)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        if (exists) {
            db.close();
            return false;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_GIFT_ID, giftId);
        long id = db.insert(TABLE_WISHLIST, null, values);
        db.close();
        return id != -1;
    }

    public void removeGiftFromWishlist(int userId, int giftId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WISHLIST, COLUMN_USER_ID + "=? AND " + COLUMN_GIFT_ID + "=?", new String[]{String.valueOf(userId), String.valueOf(giftId)});
        db.close();
    }

    public List<WishlistItem> getUserWishlist(int userId) {
        List<WishlistItem> wishlist = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // 🟢 Προσθέσαμε το g.image_path στο SELECT για να το τραβάμε από τον πίνακα των δώρων
        String query = "SELECT w." + COLUMN_WISH_ID + ", g." + COLUMN_GIFT_ID + ", g." + COLUMN_TITLE + ", g." + COLUMN_PRICE + ", g.image_path" +
                " FROM " + TABLE_WISHLIST + " w " +
                " JOIN " + TABLE_GIFTS + " g ON w." + COLUMN_GIFT_ID + " = g." + COLUMN_GIFT_ID +
                " WHERE w." + COLUMN_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                // 🟢 Περνάμε το cursor.getString(4) ως τελευταίο όρισμα στον Constructor
                WishlistItem item = new WishlistItem(
                        cursor.getInt(0),    // wish_id
                        userId,              // user_id
                        cursor.getInt(1),    // gift_id
                        cursor.getString(2), // giftTitle
                        cursor.getDouble(3), // giftPrice
                        cursor.getString(4)  // imagePath (Αυτό διαβάζει το g.image_path!)
                );
                wishlist.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return wishlist;
    }

    public boolean updateUsername(int userId, String newUsername) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", newUsername);
        int rows = db.update("users", values, "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    public boolean updatePassword(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("password", newPassword);
        int rows = db.update("users", values, "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    public boolean deleteUserAccount(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("wishlist", "user_id = ?", new String[]{String.valueOf(userId)});
        db.delete("reminders", "user_id = ?", new String[]{String.valueOf(userId)});
        int rows = db.delete("users", "user_id = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rows > 0;
    }

    public boolean checkCurrentPassword(int userId, String currentPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM users WHERE user_id = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), currentPassword});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isValid;
    }
}