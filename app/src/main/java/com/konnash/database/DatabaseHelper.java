package com.konnash.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.konnash.models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "konnash.db";
    private static final int    DB_VERSION = 1;

    // Transactions table
    public static final String TABLE_TRANSACTIONS  = "transactions";
    public static final String COL_ID              = "id";
    public static final String COL_TYPE            = "type";
    public static final String COL_AMOUNT          = "amount";
    public static final String COL_BALANCE_AFTER   = "balance_after";
    public static final String COL_TIMESTAMP       = "timestamp";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context ctx) {
        if (instance == null) {
            instance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTx = "CREATE TABLE " + TABLE_TRANSACTIONS + " ("
                + COL_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TYPE          + " TEXT NOT NULL, "
                + COL_AMOUNT        + " REAL NOT NULL, "
                + COL_BALANCE_AFTER + " REAL NOT NULL, "
                + COL_TIMESTAMP     + " INTEGER NOT NULL"
                + ");";
        db.execSQL(createTx);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    // ─── INSERT ──────────────────────────────────────────────

    public long insertTransaction(Transaction tx) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(COL_TYPE,          tx.getType());
        cv.put(COL_AMOUNT,        tx.getAmount());
        cv.put(COL_BALANCE_AFTER, tx.getBalanceAfter());
        cv.put(COL_TIMESTAMP,     tx.getTimestamp());
        long id = db.insert(TABLE_TRANSACTIONS, null, cv);
        db.close();
        return id;
    }

    // ─── READ ALL (newest first) ──────────────────────────────

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_TRANSACTIONS,
                null,
                null, null, null, null,
                COL_TIMESTAMP + " DESC"
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Transaction tx = cursorToTransaction(cursor);
                list.add(tx);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    // ─── TOTALS ───────────────────────────────────────────────

    public double getTotalIncome() {
        return sumByType(Transaction.TYPE_INCOME);
    }

    public double getTotalExpense() {
        return sumByType(Transaction.TYPE_EXPENSE);
    }

    public double getCurrentBalance() {
        return getTotalIncome() - getTotalExpense();
    }

    private double sumByType(String type) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_TRANSACTIONS
                        + " WHERE " + COL_TYPE + " = ?",
                new String[]{type}
        );
        double total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getDouble(0);
            cursor.close();
        }
        db.close();
        return total;
    }

    // ─── DELETE ───────────────────────────────────────────────

    public void deleteTransaction(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, COL_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAllTransactions() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, null, null);
        db.close();
    }

    // ─── HELPER ───────────────────────────────────────────────

    private Transaction cursorToTransaction(Cursor cursor) {
        Transaction tx = new Transaction();
        tx.setId(           cursor.getLong(  cursor.getColumnIndexOrThrow(COL_ID)));
        tx.setType(         cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)));
        tx.setAmount(       cursor.getDouble(cursor.getColumnIndexOrThrow(COL_AMOUNT)));
        tx.setBalanceAfter( cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BALANCE_AFTER)));
        tx.setTimestamp(    cursor.getLong(  cursor.getColumnIndexOrThrow(COL_TIMESTAMP)));
        return tx;
    }
}
