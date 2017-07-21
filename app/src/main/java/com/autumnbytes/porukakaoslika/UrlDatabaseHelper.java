package com.autumnbytes.porukakaoslika;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.autumnbytes.porukakaoslika.UrlContractClass.COLUMN_URL;
import static com.autumnbytes.porukakaoslika.UrlContractClass.DATABASE_NAME;
import static com.autumnbytes.porukakaoslika.UrlContractClass.DATABASE_VERSION;
import static com.autumnbytes.porukakaoslika.UrlContractClass.TABLE_NAME;

/**
 * Created by Simona Tošić on 14-May-17.
 */

public class UrlDatabaseHelper extends SQLiteOpenHelper {

    public UrlDatabaseHelper (Context context){
        super (context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.e("DATABASE OPERATIONS", "Database created/opened");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(UrlContractClass.CREATE_TABLE);
        Log.e("DATABASE OPERATIONS", "Table created");
    }

    public void addUrl (String url, SQLiteDatabase sqLiteDatabase){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_URL, url);
        sqLiteDatabase.insert(TABLE_NAME, UrlContractClass._ID, contentValues);
        Log.e("DATABASE OPERATIONS", "One row inserted");
    }

    public Cursor getUrl (SQLiteDatabase sqLiteDatabase){
        Cursor cursor;
        String [] columnName = {COLUMN_URL};
        cursor = sqLiteDatabase.query(TABLE_NAME, columnName, null, null, null, null, null);
        return cursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
