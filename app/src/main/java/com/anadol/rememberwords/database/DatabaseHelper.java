package com.anadol.rememberwords.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.anadol.rememberwords.database.DbSchema.Tables;
import com.anadol.rememberwords.database.DbSchema.Tables.Cols;
import com.anadol.rememberwords.myList.Group;
import com.anadol.rememberwords.myList.Word;

import static com.anadol.rememberwords.database.DbSchema.Tables.GROUPS;
import static com.anadol.rememberwords.database.DbSchema.Tables.WORDS;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 5;//3
    public static final String DB_NAME = "rememberWords";
 /*   public static final String DIALOG = "DIALOG";
    public static final String LANGUAGE = "LANGUAGE";
    public static final String SAVED = "SAVED";*/

    public DatabaseHelper(Context context){
        super(context, DB_NAME,null ,DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("DROP TABLE DIALOG");
        db.execSQL("create table " + GROUPS + "(" +
                "_id integer primary key autoincrement," +
                Cols.UUID + " text," +
                Cols.COLOR_ONE + " integer," +
                Cols.COLOR_TWO + " integer," +
                Cols.COLOR_THREE + " integer," +
                Cols.NAME_GROUP + " text)");

        db.execSQL("create table " + Tables.WORDS +"(" +
                "_id integer primary key autoincrement," +
                Cols.UUID + " text ," +
//                Tables.Cols.COLOR_START + " integer," + На будущее
//                Tables.Cols.COLOR_END + " integer," + -||-
                Cols.NAME_GROUP + " text," +
                Cols.ORIGINAL + " text," +
                Cols.TRANSLATE + " text," +
                Cols.TRANSCRIPTION + " text," +
                Cols.COMMENT + " text," +
                Cols.IS_MULTI_TRANS + " int)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { // ПРОБЕЛЫ ОЧЕНЬ ВАЖНЫ
        if (oldVersion == 2){
            db.execSQL("alter table " + WORDS +
                    " add column " + Cols.TRANSCRIPTION + " text");
        }else if(oldVersion == 3){
            db.execSQL("alter table " + GROUPS +" rename to "+ "groups_tmp");

            db.execSQL("create table " + GROUPS + "(" +
                    "_id integer primary key autoincrement," +
                    Cols.UUID + " text," +
                    Cols.COLOR_ONE + " integer," +
                    Cols.COLOR_TWO + " integer," +
                    Cols.COLOR_THREE + " integer," +
                    Cols.NAME_GROUP + " text)");

            db.execSQL("insert into " + GROUPS +"("+
                    Cols.UUID + ", " +
                    Cols.COLOR_ONE + ", " +
                    Cols.COLOR_TWO + ", " +
                    Cols.NAME_GROUP+ ")" +
                    "select "+ Cols.UUID + ", " +
                    Cols.OLD_COLOR_START + ", " +
                    Cols.OLD_COLOR_END + "," +
                    Cols.NAME_GROUP +" "+
                    "from groups_tmp");
        }else if (oldVersion == 4){
            db.execSQL("alter table " + WORDS +
                    " add column " + Cols.COMMENT + " text");
            db.execSQL("alter table " + WORDS +
                    " add column " + Cols.IS_MULTI_TRANS + " int");
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
