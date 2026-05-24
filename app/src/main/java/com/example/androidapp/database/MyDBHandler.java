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
    private static final int DATABASE_VERSION = 3;

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
    // ΝΕΟ ΠΕΔΙΟ: Για το όνομα ή το μονοπάτι της φωτογραφίας
    private static final String COLUMN_IMAGE_PATH = "image_path";

    private static final String TABLE_WISHLIST = "wishlist";
    private static final String COLUMN_WISH_ID = "wish_id";

    private static final String TABLE_REMINDERS = "reminders";
    private static final String COLUMN_REMINDER_ID = "reminder_id";
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

        // Ενημερωμένος πίνακας GIFTS με το COLUMN_IMAGE_PATH
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
                COLUMN_IMAGE_PATH + " TEXT" + ")"; // Προσθήκη στήλης εικόνας
        db.execSQL(CREATE_GIFTS_TABLE);

        String CREATE_WISHLIST_TABLE = "CREATE TABLE " + TABLE_WISHLIST + "(" +
                COLUMN_WISH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USER_ID + " INTEGER," +
                COLUMN_GIFT_ID + " INTEGER" + ")";
        db.execSQL(CREATE_WISHLIST_TABLE);

        String CREATE_REMINDERS_TABLE = "CREATE TABLE " + TABLE_REMINDERS + "(" +
                COLUMN_REMINDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_USER_ID + " INTEGER," +
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
    // 🟢 ΠΡΟΣΘΗΚΗ 1: Μέθοδος για να διαβάζει τα reminders του συγκεκριμένου χρήστη
    public List<ReminderModel> getUserReminders(int userId) {
        List<ReminderModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Αντικατάστησε τα TABLE_REMINDERS, COLUMN_USER_ID, κλπ. με τις δικές σου μεταβλητές αν διαφέρουν
        String query = "SELECT * FROM reminders WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                ReminderModel reminder = new ReminderModel(
                        cursor.getInt(0),    // id (COLUMN_ID)
                        cursor.getInt(1),    // user_id (COLUMN_USER_ID)
                        cursor.getString(2), // eventName (COLUMN_EVENT_NAME)
                        cursor.getString(3)  // eventDate (COLUMN_EVENT_DATE)
                );
                list.add(reminder);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public void deleteReminder(int reminderId) {
        // Αν το id είναι 0 ή αρνητικό, σταματάμε για να αποφύγουμε το κρασάρισμα
        if (reminderId <= 0) {
            return;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // Αντικατάστησε το "reminders" και "id" με τις δικές σου σταθερές αν διαφέρουν
            db.delete("reminders", "id = ?", new String[]{String.valueOf(reminderId)});
        } catch (Exception e) {
            e.printStackTrace(); // Καταγράφει το σφάλμα στο Logcat αντί να κρασάρει το app
        } finally {
            if (db != null && db.isOpen()) {
                db.close(); // Κλείνουμε πάντα τη βάση με ασφάλεια
            }
        }
    }
    public User getUserById(int userId) {
        User user = null;
        SQLiteDatabase db = this.getReadableDatabase();

        // Query για την εύρεση του χρήστη με βάση το user_id
        String query = "SELECT " + COLUMN_USERNAME + ", " + COLUMN_EMAIL +
                " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            String username = cursor.getString(0);
            String email = cursor.getString(1);

            // Δημιουργούμε το αντικείμενο User (κωδικό βάζουμε κενό μιας και δεν τον χρειαζόμαστε στο Profile)
            user = new User(username, email, "");
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

                    // Ασφάλεια: Αν η τιμή δεν είναι αριθμός (π.χ. "??"), βάζουμε 0.0 αντί να κρασάρει η εφαρμογή
                    try {
                        values.put(COLUMN_PRICE, Double.parseDouble(parts[1]));
                    } catch (NumberFormatException e) {
                        values.put(COLUMN_PRICE, 0.0);
                    }

                    values.put(COLUMN_CATEGORY, parts[2]);
                    values.put(COLUMN_RELATIONSHIP, parts[3]);

                    if (parts.length >= 5) {
                        values.put(COLUMN_IMAGE_PATH, parts[4]);
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

        String rawCategories = request.getCategory();

        // ΦΙΞ: Αν το String είναι τελείως κενό, βάζουμε μια default τιμή για να μην σπάσει το split
        if (rawCategories == null || rawCategories.trim().isEmpty()) {
            rawCategories = "all";
        }

        String[] categories = rawCategories.split(",\\s*");
        StringBuilder categoryQuery = new StringBuilder();
        List<String> queryArgs = new ArrayList<>();

        // Αν δεν έχουμε επιλεγμένα χόμπι, φέρνουμε όλες τις κατηγορίες
        if (categories.length == 1 && categories[0].equals("all")) {
            categoryQuery.append("1=1"); // Πάντα αληθές στην SQLite (φέρνει τα πάντα)
        } else {
            categoryQuery.append("(");
            for (int i = 0; i < categories.length; i++) {
                categoryQuery.append(COLUMN_CATEGORY).append(" LIKE ?");
                queryArgs.add("%" + categories[i].trim() + "%");
                if (i < categories.length - 1) {
                    categoryQuery.append(" OR ");
                }
            }
            categoryQuery.append(")");
        }

        // 🟢 ΕΞΥΠΝΟ QUERY: Φέρνει το δώρο αν ταιριάζει η κατηγορία ΚΑΙ η τιμή,
        // και εμφανίζει το δώρο είτε αν ταιριάζει η σχέση, είτε αν η σχέση είναι γενική ('general' ή κενή),
        // είτε αν ο χρήστης ζήτησε συγκεκριμένα ένα χόμπι (γιατί το χόμπι έχει μεγαλύτερη σημασία!)
        String finalQuery = "SELECT * FROM " + TABLE_GIFTS +
                " WHERE " + categoryQuery.toString() +
                " AND " + COLUMN_PRICE + " <= ?" +
                " AND (" + COLUMN_RELATIONSHIP + " LIKE ? " +
                " OR " + COLUMN_RELATIONSHIP + " = 'general' " +
                " OR " + COLUMN_RELATIONSHIP + " = '' " +
                " OR " + categoryQuery.toString() + ")"; // Επιτρέπει στο χόμπι να παρακάμψει τον περιορισμό σχέσης

        // Προσθήκη του budget στα ορίσματα
        queryArgs.add(String.valueOf(request.getMaxPrice()));

        // Προσθήκη της σχέσης στα ορίσματα
        queryArgs.add("%" + request.getRelationship() + "%");

        // Επειδή βάλαμε το categoryQuery δύο φορές στο SQL, πρέπει να ξαναπεράσουμε τα ορίσματα των κατηγοριών για τη δεύτερη φορά
        if (!categories[0].equals("all")) {
            for (String cat : categories) {
                queryArgs.add("%" + cat.trim() + "%");
            }
        }

        String[] argsArray = queryArgs.toArray(new String[0]);

        // Εκτέλεση του query
        Cursor cursor = db.rawQuery(finalQuery, argsArray);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndexOrThrow(COLUMN_GIFT_ID);
            int titleIndex = cursor.getColumnIndexOrThrow(COLUMN_TITLE);
            int priceIndex = cursor.getColumnIndexOrThrow(COLUMN_PRICE);
            int categoryIndex = cursor.getColumnIndexOrThrow(COLUMN_CATEGORY);
            int hobbyIndex = cursor.getColumnIndexOrThrow(COLUMN_HOBBY);
            int occasionIndex = cursor.getColumnIndexOrThrow(COLUMN_OCCASION);
            int relationshipIndex = cursor.getColumnIndexOrThrow(COLUMN_RELATIONSHIP);
            int ageIndex = cursor.getColumnIndexOrThrow(COLUMN_AGE);
            int descriptionIndex = cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION);
            int imageIndex = cursor.getColumnIndexOrThrow(COLUMN_IMAGE_PATH);

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

    public void addReminder(ReminderModel reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("user_id", reminder.getUserId()); // 🟢 Εδώ αποθηκεύεται το ID του σωστού χρήστη!
        values.put("event_name", reminder.getEventName());
        values.put("event_date", reminder.getEventDate());

        db.insert("reminders", null, values);
        db.close();
    }

    public boolean addGiftToWishlist(int userId, int giftId) {
        SQLiteDatabase db = this.getWritableDatabase();

        String checkQuery = "SELECT 1 FROM " + TABLE_WISHLIST +
                " WHERE " + COLUMN_USER_ID + "=? AND " + COLUMN_GIFT_ID + "=?";
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