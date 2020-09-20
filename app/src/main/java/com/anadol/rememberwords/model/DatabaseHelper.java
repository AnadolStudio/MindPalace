package com.anadol.rememberwords.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.anadol.rememberwords.database.DbSchema.Tables;
import com.anadol.rememberwords.database.DbSchema.Tables.Cols;

import static com.anadol.rememberwords.database.DbSchema.Tables.GROUPS;
import static com.anadol.rememberwords.database.DbSchema.Tables.WORDS;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 5;//6
    public static final String DB_NAME = "rememberWords";
    private static final String TAG = "DatabaseHelper";
 /*   public static final String DIALOG = "DIALOG";
    public static final String LANGUAGE = "LANGUAGE";
    public static final String SAVED = "SAVED";*/

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    // TODO: Остановился на том, что необходимо изменить таблицы в БД:
    //  1) Как сохранить все цвета для группы?
    //  2) Как сохраняется Translate?
    //  3) Реализовать Filter из DialogMultiTranslate в WordListHolder
    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("DROP TABLE DIALOG");
        db.execSQL("create table " + GROUPS + "(" +
                "_id integer primary key autoincrement," +
                Cols.UUID + " text," +
                Cols.COLOR_ONE + " integer," +
                Cols.COLOR_TWO + " integer," +
                Cols.COLOR_TWO + " integer," +
                Cols.NAME_GROUP + " text)"); // TODO: {COLOR_ONE, COLOR_TWO, COLOR_TWO} => Drawable, в котором будет храниться gradient из трех цветов в виде String, либо путь к картинке

        db.execSQL("create table " + Tables.WORDS + "(" +
                "_id integer primary key autoincrement," +
                Cols.UUID + " text ," +
                Cols.NAME_GROUP + " text," +
                Cols.ORIGINAL + " text," +
                Cols.TRANSLATE + " text," +
                Cols.TRANSCRIPTION + " text," +
                Cols.COMMENT + " text," +
                Cols.IS_MULTI_TRANS + " int)");
/*
        // Update code
        db.execSQL("create table " + Groups.TABLE_NAME + "(" +
                Groups._ID +" integer primary key autoincrement," +
                Groups.UUID + " text," +
                Groups.DRAWABLE + " integer," +
                Groups.NAME_GROUP + " text)");

        db.execSQL("create table " + Words.TABLE_NAME +"(" +
                Words._ID + " integer primary key autoincrement," +
                Words.UUID + " text ," +
                Words.UUID_GROUP + " text," +
                Words.ORIGINAL + " text," +
                Words.ASSOCIATION + " text," +
                Words.TRANSLATE + " text," +
                Words.COMMENT + " text)");
*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { // ПРОБЕЛЫ ОЧЕНЬ ВАЖНЫ
        switch (oldVersion) {
            case 2:
                db.execSQL("alter table " + WORDS +
                        " add column " + Cols.TRANSCRIPTION + " text");
                break;
            case 3:
                db.execSQL("alter table " + GROUPS + " rename to " + "groups_tmp");

                db.execSQL("create table " + GROUPS + "(" +
                        "_id integer primary key autoincrement," +
                        Cols.UUID + " text," +
                        Cols.COLOR_ONE + " integer," +
                        Cols.COLOR_TWO + " integer," +
                        Cols.COLOR_THREE + " integer," +
                        Cols.NAME_GROUP + " text)");

                db.execSQL("insert into " + GROUPS + "(" +
                        Cols.UUID + ", " +
                        Cols.COLOR_ONE + ", " +
                        Cols.COLOR_TWO + ", " +
                        Cols.NAME_GROUP + ")" +
                        "select " + Cols.UUID + ", " +
                        Cols.OLD_COLOR_START + ", " +
                        Cols.OLD_COLOR_END + "," +
                        Cols.NAME_GROUP + " " +
                        "from groups_tmp");
                break;
            case 4:
                db.execSQL("alter table " + WORDS +
                        " add column " + Cols.COMMENT + " text");
                db.execSQL("alter table " + WORDS +
                        " add column " + Cols.IS_MULTI_TRANS + " int");
                break;
            case 5:
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
