package com.anadol.rememberwords.myList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import com.anadol.rememberwords.database.DbSchema;
import com.anadol.rememberwords.database.MyCursorWrapper;

import java.util.UUID;

public abstract class DoInBackground extends AsyncTask<String, Void, Boolean> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        return doIn(strings[0]);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        onPost(aBoolean);
    }

    public abstract Boolean doIn(String s);

    public abstract void onPost(boolean b);

    protected MyCursorWrapper queryTable(@NonNull SQLiteDatabase db, String table, String whereClause, String[] whereArgs){ // do in background
        Cursor cursor = db.query(
                table,
                null,
                whereClause,
                whereArgs,
                null,null,null
        );
        return new MyCursorWrapper(cursor);
    }
    protected ContentValues createGroupValue(UUID id, String name, int colorOne, int colorTwo, int colorThree) {
        ContentValues values = new ContentValues();
        values.put(DbSchema.Tables.Cols.UUID, id.toString());
        values.put(DbSchema.Tables.Cols.NAME_GROUP, name);
        values.put(DbSchema.Tables.Cols.COLOR_ONE, colorOne);
        values.put(DbSchema.Tables.Cols.COLOR_TWO, colorTwo);
        values.put(DbSchema.Tables.Cols.COLOR_THREE, colorThree);
        return values;
    }

    protected ContentValues createWordsValue(UUID id,
                                             String nameGroup,
                                             String orig,
                                             String trans,
                                             String transcript,
                                             String comment,
                                             int isMultiTrans){
        ContentValues values = new ContentValues();
        values.put(DbSchema.Tables.Cols.UUID, id.toString());
        values.put(DbSchema.Tables.Cols.NAME_GROUP, nameGroup);
        values.put(DbSchema.Tables.Cols.ORIGINAL, orig);
        values.put(DbSchema.Tables.Cols.TRANSLATE, trans);
        values.put(DbSchema.Tables.Cols.TRANSCRIPTION, transcript);
        values.put(DbSchema.Tables.Cols.COMMENT, comment);
        values.put(DbSchema.Tables.Cols.IS_MULTI_TRANS, isMultiTrans);

        return values;
    }

}