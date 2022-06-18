package com.anadol.mindpalace.data.group;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anadol.mindpalace.data.group.DataBaseSchema.Groups;
import com.anadol.mindpalace.data.group.DataBaseSchema.Words;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 9;
    public static final String DB_NAME = "rememberWords";// TODO Rename
    private static final String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Update code
        createGroupsTable(db);
        createWordsTable(db);
    }

    private void createGroupsTable(SQLiteDatabase db) {
        db.execSQL("create table " + Groups.TABLE_NAME + "(" +
                Groups._ID + " integer primary key autoincrement," +
                Groups.UUID + " text," +
                Groups.DRAWABLE + " text," +
                Groups.TYPE + " text," +
                Groups.NAME_GROUP + " text)");
    }

    private void createWordsTable(SQLiteDatabase db) {
        db.execSQL("create table " + Words.TABLE_NAME + "(" +
                Words._ID + " integer primary key autoincrement," +
                Words.UUID + " text ," +
                Words.UUID_GROUP + " text," +
                Words.ORIGINAL + " text," +
                Words.ASSOCIATION + " text," +
                Words.TRANSLATE + " text," +
                Words.COMMENT + " text," +
                Words.COUNT_LEARN + " integer," +
                Words.TIME + " integer," +
                Words.EXAM + " integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { // ПРОБЕЛЫ ОЧЕНЬ ВАЖНЫ
        switch (oldVersion) {
            case 7:
                db.execSQL("alter table " + Words.TABLE_NAME + " add column difficult");
                break;
            case 8:
                db.execSQL("alter table " + Words.TABLE_NAME + " rename to " + "words_tmp");
                createWordsTable(db);
                db.execSQL("insert into " + Words.TABLE_NAME + "(" +
                        Words.UUID + ", " +
                        Words.UUID_GROUP + ", " +
                        Words.ORIGINAL + ", " +
                        Words.ASSOCIATION + ", " +
                        Words.TRANSLATE + ", " +
                        Words.COMMENT + ")" +
                        "select " +
                        Words.UUID + ", " +
                        Words.UUID_GROUP + ", " +
                        Words.ORIGINAL + ", " +
                        Words.ASSOCIATION + ", " +
                        Words.TRANSLATE + ", " +
                        Words.COMMENT + " " +
                        "from words_tmp");
                db.execSQL("drop table " + "words_tmp");
                break;
        }
        Log.i(TAG, "onUpgrade. New version: " + newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
