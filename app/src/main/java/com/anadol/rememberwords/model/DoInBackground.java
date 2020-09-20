package com.anadol.rememberwords.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import com.anadol.rememberwords.model.MyCursorWrapper;

public abstract class DoInBackground extends AsyncTask<String, Void, Boolean> {
// TODO: extends AsyncTask<String, Void, Boolean>, заменить Boolean на SimpleParent
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

}