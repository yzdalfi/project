package com.example.project3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "finance_record.db";
    private static final int DATABASE_VERSION = 1;

    // Konstanta tabel users
    private static final String TABLE_USER = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_TRANSACTION = "transactions";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT)";

        String createTransactionTable = "CREATE TABLE " + TABLE_TRANSACTION + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_AMOUNT + " REAL, " +
                COLUMN_CATEGORY + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_USER_ID + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "))";

        db.execSQL(createUserTable);
        db.execSQL(createTransactionTable);
    }

    // Tambahkan fungsi untuk register user
    public boolean registerUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_USER, null, values);
        return result != -1;
    }

    // Tambahkan fungsi untuk login user
    public boolean loginUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " +
                COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{username, password});
        return cursor.getCount() > 0;
    }

    // Mendapatkan ID user berdasarkan username
    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USER + " WHERE " +
                COLUMN_USERNAME + " = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        cursor.close();
        return -1;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        onCreate(db);
    }

    public Cursor getTransactionsByUserIdAndDate(int userId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM " + TABLE_TRANSACTION +
                        " WHERE " + COLUMN_USER_ID + " = ? AND " + COLUMN_DATE + " = ?",
                new String[]{String.valueOf(userId), date}
        );
    }

    // Update insertTransaction untuk menyimpan user_id
    public boolean insertTransaction(int userId, double amount, String category, String description, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_AMOUNT, amount);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_DESCRIPTION, description);

        long result = db.insert(TABLE_TRANSACTION, null, values);
        return result != -1;
    }

    // Mendapatkan transaksi berdasarkan user_id
    public Cursor getTransactionsByUserId(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
    }

    public Cursor getTransactionsByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_DATE + " = ?", new String[]{date});
    }

    public Cursor getTransactionById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TRANSACTION + " WHERE " + COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getAllTransactions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_TRANSACTION, null);
    }

    public boolean updateTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_AMOUNT, transaction.getAmount());
        contentValues.put(COLUMN_CATEGORY, transaction.getCategory());
        contentValues.put(COLUMN_DESCRIPTION, transaction.getDescription());
        contentValues.put(COLUMN_DATE, transaction.getDate());

        int result = db.update(TABLE_TRANSACTION, contentValues, "id = ?", new String[]{String.valueOf(transaction.getId())});
        return result > 0;
    }

    public boolean deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TRANSACTION, "id = ?", new String[]{String.valueOf(id)});
        return result > 0;
    }

}
