package com.anadol.rememberwords.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import com.anadol.rememberwords.model.MyCursorWrapper;

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


    protected MyCursorWrapper queryTable(@NonNull SQLiteDatabase db, String table,String[] columns,String whereClause, String[] whereArgs){
        Cursor cursor = db.query(
                table,
                columns,
                whereClause,
                whereArgs,
                null,null,null
        );
        return new MyCursorWrapper(cursor);
    }
}