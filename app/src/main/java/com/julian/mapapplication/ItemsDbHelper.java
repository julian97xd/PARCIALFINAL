package com.julian.mapapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.julian.mapapplication.ItemsContract;

public class ItemsDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "items.db";

    public ItemsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Comandos SQL
        sqLiteDatabase.execSQL("CREATE TABLE " + ItemsContract.ItemsEntry.TABLE_NAME + " ("
                + ItemsContract.ItemsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ItemsContract.ItemsEntry.ID + " TEXT NOT NULL,"
                + ItemsContract.ItemsEntry.NAME + " TEXT NOT NULL,"
                + ItemsContract.ItemsEntry.CORDILLERA + " TEXT NOT NULL,"
                + ItemsContract.ItemsEntry.COUNTRY + " TEXT NOT NULL,"
                + ItemsContract.ItemsEntry.HEIGHT + " TEXT NOT NULL,"
                + ItemsContract.ItemsEntry.LAT + " TEXT NOT NULL,"
                + ItemsContract.ItemsEntry.LNG + " TEXT NOT NULL,"
                + "UNIQUE (" + ItemsContract.ItemsEntry.ID + "))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No hay operaciones
    }

    public long saveLawyer(Item item) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase.insert(
                ItemsContract.ItemsEntry.TABLE_NAME,
                null,
                item.toContentValues());

    }

    public Cursor getAllLawyers() {
        return getReadableDatabase()
                .query(
                        ItemsContract.ItemsEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
    }

    public Cursor getItemById(String itemId) {
        Cursor c = getReadableDatabase().query(
                ItemsContract.ItemsEntry.TABLE_NAME,
                null,
                ItemsContract.ItemsEntry.ID + " LIKE ?",
                new String[]{itemId},
                null,
                null,
                null);
        return c;
    }

    public int deleteItem(String itemId) {
        return getWritableDatabase().delete(
                ItemsContract.ItemsEntry.TABLE_NAME,
                ItemsContract.ItemsEntry.ID + " LIKE ?",
                new String[]{itemId});
    }

    public int updateItem(Item item, String itemId) {
        return getWritableDatabase().update(
                ItemsContract.ItemsEntry.TABLE_NAME,
                item.toContentValues(),
                ItemsContract.ItemsEntry.ID + " LIKE ?",
                new String[]{itemId}
        );
    }
}
