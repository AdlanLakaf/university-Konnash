package com.konnash.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.konnash.models.Client;
import com.konnash.models.Tag;
import com.konnash.models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME    = "konnash.db";
    private static final int    DB_VERSION = 2;   // bumped for new tables

    // ── Transactions table ────────────────────────────────────────
    public static final String TABLE_TRANSACTIONS  = "transactions";
    public static final String COL_ID              = "id";
    public static final String COL_TYPE            = "type";
    public static final String COL_AMOUNT          = "amount";
    public static final String COL_BALANCE_AFTER   = "balance_after";
    public static final String COL_TIMESTAMP       = "timestamp";

    // ── Tags table ────────────────────────────────────────────────
    public static final String TABLE_TAGS          = "tags";
    public static final String COL_TAG_ID          = "id";
    public static final String COL_TAG_NAME        = "name";
    public static final String COL_TAG_COLOR       = "color";
    public static final String COL_TAG_CREATED_AT  = "created_at";

    // ── Clients table ─────────────────────────────────────────────
    public static final String TABLE_CLIENTS           = "clients";
    public static final String COL_CLIENT_ID           = "id";
    public static final String COL_CLIENT_NAME         = "name";
    public static final String COL_CLIENT_PHONE        = "phone";
    public static final String COL_CLIENT_ADDRESS      = "address";
    public static final String COL_CLIENT_CREATED_AT   = "created_at";

    // ── Client-Tag join table ─────────────────────────────────────
    public static final String TABLE_CLIENT_TAGS       = "client_tags";
    public static final String COL_CT_CLIENT_ID        = "client_id";
    public static final String COL_CT_TAG_ID           = "tag_id";

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
        // Transactions
        String createTx = "CREATE TABLE " + TABLE_TRANSACTIONS + " ("
                + COL_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TYPE          + " TEXT NOT NULL, "
                + COL_AMOUNT        + " REAL NOT NULL, "
                + COL_BALANCE_AFTER + " REAL NOT NULL, "
                + COL_TIMESTAMP     + " INTEGER NOT NULL"
                + ");";
        db.execSQL(createTx);

        // Tags
        String createTags = "CREATE TABLE " + TABLE_TAGS + " ("
                + COL_TAG_ID         + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TAG_NAME       + " TEXT NOT NULL, "
                + COL_TAG_COLOR      + " TEXT NOT NULL DEFAULT '#7BAED6', "
                + COL_TAG_CREATED_AT + " INTEGER NOT NULL"
                + ");";
        db.execSQL(createTags);

        // Clients
        String createClients = "CREATE TABLE " + TABLE_CLIENTS + " ("
                + COL_CLIENT_ID         + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CLIENT_NAME       + " TEXT NOT NULL, "
                + COL_CLIENT_PHONE      + " TEXT, "
                + COL_CLIENT_ADDRESS    + " TEXT, "
                + COL_CLIENT_CREATED_AT + " INTEGER NOT NULL"
                + ");";
        db.execSQL(createClients);

        // Client-Tag join
        String createClientTags = "CREATE TABLE " + TABLE_CLIENT_TAGS + " ("
                + COL_CT_CLIENT_ID + " INTEGER NOT NULL, "
                + COL_CT_TAG_ID    + " INTEGER NOT NULL, "
                + "PRIMARY KEY (" + COL_CT_CLIENT_ID + ", " + COL_CT_TAG_ID + "), "
                + "FOREIGN KEY (" + COL_CT_CLIENT_ID + ") REFERENCES " + TABLE_CLIENTS + "(" + COL_CLIENT_ID + ") ON DELETE CASCADE, "
                + "FOREIGN KEY (" + COL_CT_TAG_ID    + ") REFERENCES " + TABLE_TAGS    + "(" + COL_TAG_ID    + ") ON DELETE CASCADE"
                + ");";
        db.execSQL(createClientTags);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add new tables introduced in version 2
            String createTags = "CREATE TABLE IF NOT EXISTS " + TABLE_TAGS + " ("
                    + COL_TAG_ID         + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_TAG_NAME       + " TEXT NOT NULL, "
                    + COL_TAG_COLOR      + " TEXT NOT NULL DEFAULT '#7BAED6', "
                    + COL_TAG_CREATED_AT + " INTEGER NOT NULL"
                    + ");";
            db.execSQL(createTags);

            String createClients = "CREATE TABLE IF NOT EXISTS " + TABLE_CLIENTS + " ("
                    + COL_CLIENT_ID         + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_CLIENT_NAME       + " TEXT NOT NULL, "
                    + COL_CLIENT_PHONE      + " TEXT, "
                    + COL_CLIENT_ADDRESS    + " TEXT, "
                    + COL_CLIENT_CREATED_AT + " INTEGER NOT NULL"
                    + ");";
            db.execSQL(createClients);

            String createClientTags = "CREATE TABLE IF NOT EXISTS " + TABLE_CLIENT_TAGS + " ("
                    + COL_CT_CLIENT_ID + " INTEGER NOT NULL, "
                    + COL_CT_TAG_ID    + " INTEGER NOT NULL, "
                    + "PRIMARY KEY (" + COL_CT_CLIENT_ID + ", " + COL_CT_TAG_ID + ")"
                    + ");";
            db.execSQL(createClientTags);
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // ════════════════════════════════════════════════════════════
    //  TRANSACTIONS
    // ════════════════════════════════════════════════════════════

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

    public List<Transaction> getAllTransactions() {
        List<Transaction> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_TRANSACTIONS,
                null, null, null, null, null,
                COL_TIMESTAMP + " DESC"
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(cursorToTransaction(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    public double getTotalIncome()   { return sumByType(Transaction.TYPE_INCOME); }
    public double getTotalExpense()  { return sumByType(Transaction.TYPE_EXPENSE); }
    public double getCurrentBalance(){ return getTotalIncome() - getTotalExpense(); }

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

    private Transaction cursorToTransaction(Cursor cursor) {
        Transaction tx = new Transaction();
        tx.setId(           cursor.getLong(  cursor.getColumnIndexOrThrow(COL_ID)));
        tx.setType(         cursor.getString(cursor.getColumnIndexOrThrow(COL_TYPE)));
        tx.setAmount(       cursor.getDouble(cursor.getColumnIndexOrThrow(COL_AMOUNT)));
        tx.setBalanceAfter( cursor.getDouble(cursor.getColumnIndexOrThrow(COL_BALANCE_AFTER)));
        tx.setTimestamp(    cursor.getLong(  cursor.getColumnIndexOrThrow(COL_TIMESTAMP)));
        return tx;
    }

    // ════════════════════════════════════════════════════════════
    //  TAGS
    // ════════════════════════════════════════════════════════════

    /** Insert a new tag. Returns the new row id, or -1 on error. */
    public long insertTag(Tag tag) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(COL_TAG_NAME,       tag.getName());
        cv.put(COL_TAG_COLOR,      tag.getColor() != null ? tag.getColor() : "#7BAED6");
        cv.put(COL_TAG_CREATED_AT, System.currentTimeMillis());
        long id = db.insert(TABLE_TAGS, null, cv);
        db.close();
        return id;
    }

    /** Return all tags ordered by name. */
    public List<Tag> getAllTags() {
        List<Tag> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_TAGS, null, null, null, null, null, COL_TAG_NAME + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(cursorToTag(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    /** Delete a tag by id (cascade removes client_tags rows). */
    public void deleteTag(long tagId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TAGS, COL_TAG_ID + " = ?", new String[]{String.valueOf(tagId)});
        db.close();
    }

    private Tag cursorToTag(Cursor cursor) {
        Tag tag = new Tag();
        tag.setId(        cursor.getLong(  cursor.getColumnIndexOrThrow(COL_TAG_ID)));
        tag.setName(      cursor.getString(cursor.getColumnIndexOrThrow(COL_TAG_NAME)));
        tag.setColor(     cursor.getString(cursor.getColumnIndexOrThrow(COL_TAG_COLOR)));
        tag.setCreatedAt( cursor.getLong(  cursor.getColumnIndexOrThrow(COL_TAG_CREATED_AT)));
        return tag;
    }

    // ════════════════════════════════════════════════════════════
    //  CLIENTS
    // ════════════════════════════════════════════════════════════

    /** Insert a client; returns new row id. */
    public long insertClient(Client client) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(COL_CLIENT_NAME,       client.getName());
        cv.put(COL_CLIENT_PHONE,      client.getPhone());
        cv.put(COL_CLIENT_ADDRESS,    client.getAddress());
        cv.put(COL_CLIENT_CREATED_AT, System.currentTimeMillis());
        long id = db.insert(TABLE_CLIENTS, null, cv);
        db.close();
        return id;
    }

    /** Return all clients ordered by name. */
    public List<Client> getAllClients() {
        List<Client> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CLIENTS, null, null, null, null, null, COL_CLIENT_NAME + " ASC");
        if (cursor != null && cursor.moveToFirst()) {
            do {
                list.add(cursorToClient(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return list;
    }

    /** Return count of clients. */
    public int getClientCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_CLIENTS, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }

    /** Delete a client by id (cascade removes client_tags rows). */
    public void deleteClient(long clientId) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_CLIENTS, COL_CLIENT_ID + " = ?", new String[]{String.valueOf(clientId)});
        db.close();
    }

    // ── Client–Tag associations ───────────────────────────────

    /** Link a tag to a client. */
    public void addTagToClient(long clientId, long tagId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv  = new ContentValues();
        cv.put(COL_CT_CLIENT_ID, clientId);
        cv.put(COL_CT_TAG_ID,    tagId);
        db.insertWithOnConflict(TABLE_CLIENT_TAGS, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }

    /** Return all tag ids linked to a given client. */
    public List<Long> getTagIdsForClient(long clientId) {
        List<Long> ids = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_CLIENT_TAGS,
                new String[]{COL_CT_TAG_ID},
                COL_CT_CLIENT_ID + " = ?",
                new String[]{String.valueOf(clientId)},
                null, null, null
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ids.add(cursor.getLong(0));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return ids;
    }

    /** Return all tags linked to a given client. */
    public List<Tag> getTagsForClient(long clientId) {
        List<Tag> tags = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT t.* FROM " + TABLE_TAGS + " t "
                   + "INNER JOIN " + TABLE_CLIENT_TAGS + " ct ON t." + COL_TAG_ID + " = ct." + COL_CT_TAG_ID
                   + " WHERE ct." + COL_CT_CLIENT_ID + " = ? ORDER BY t." + COL_TAG_NAME + " ASC";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(clientId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                tags.add(cursorToTag(cursor));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return tags;
    }

    private Client cursorToClient(Cursor cursor) {
        Client c = new Client();
        c.setId(        cursor.getLong(  cursor.getColumnIndexOrThrow(COL_CLIENT_ID)));
        c.setName(      cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENT_NAME)));
        c.setPhone(     cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENT_PHONE)));
        c.setAddress(   cursor.getString(cursor.getColumnIndexOrThrow(COL_CLIENT_ADDRESS)));
        c.setCreatedAt( cursor.getLong(  cursor.getColumnIndexOrThrow(COL_CLIENT_CREATED_AT)));
        return c;
    }
}
