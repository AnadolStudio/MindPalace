package com.anadol.rememberwords.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anadol.rememberwords.model.DataBaseSchema.Groups;
import com.anadol.rememberwords.model.DataBaseSchema.Words;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 6;//6
    public static final String DB_NAME = "rememberWords";
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
                Groups.COLOR_ONE + " integer," +
                Groups.COLOR_TWO + " integer," +
                Groups.COLOR_THREE + " integer," +
                Groups.NAME_GROUP + " text)");
    }

    private void createWordsTable(SQLiteDatabase db) {
        db.execSQL("create table " + Words.TABLE_NAME + "(" +
                Words._ID + " integer primary key autoincrement," +
                Words.UUID + " text ," +
                Words.UUID_GROUP + " text," +
                Words.NAME_GROUP + " text," +
                Words.ORIGINAL + " text," +
                Words.ASSOCIATION + " text," +
                Words.TRANSLATE + " text," +
                Words.COMMENT + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { // ПРОБЕЛЫ ОЧЕНЬ ВАЖНЫ
        switch (oldVersion) {
            case 5:
                db.execSQL("drop table if exists " + "groups_tmp");

                db.execSQL("alter table " + Groups.TABLE_NAME + " rename to " + "groups_tmp");
                createGroupsTable(db);
                db.execSQL("insert into " + Groups.TABLE_NAME + "(" +
                        Groups.UUID + ", " +
                        Groups.COLOR_ONE + ", " +
                        Groups.COLOR_TWO + ", " +
                        Groups.COLOR_THREE + ", " +
                        Groups.NAME_GROUP + ")" +
                        "select " +
                        Groups.UUID + ", " +
                        Groups.COLOR_ONE + ", " +
                        Groups.COLOR_TWO + ", " +
                        Groups.COLOR_THREE + ", " +
                        Groups.NAME_GROUP + " " +
                        "from groups_tmp");

                db.execSQL("drop table " + "groups_tmp");


                db.execSQL("alter table " + Words.TABLE_NAME + " rename to " + "words_tmp");
                createWordsTable(db);
                db.execSQL("insert into " + Words.TABLE_NAME + "(" +
                        Words.UUID + ", " +
                        Words.ORIGINAL + ", " +
                        Words.ASSOCIATION + ", " +
                        Words.TRANSLATE + ", " +
                        Words.COMMENT + ", " +
                        Words.NAME_GROUP + ")" +
                        "select " +
                        Words.UUID + ", " +
                        Words.ORIGINAL + ", " +
                        "transcription, " +
                        Words.TRANSLATE + ", " +
                        Words.COMMENT + ", " +
                        Words.NAME_GROUP + " " +
                        "from words_tmp");

                db.execSQL("drop table " + "words_tmp");
                break;
            case 6:
                break;
        }
        Log.i(TAG, "onUpgrade. New version: " + newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
