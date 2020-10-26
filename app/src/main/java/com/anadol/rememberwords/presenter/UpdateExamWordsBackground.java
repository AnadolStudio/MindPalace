package com.anadol.rememberwords.presenter;

import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;

import com.anadol.rememberwords.model.DataBaseSchema;
import com.anadol.rememberwords.model.MyCursorWrapper;
import com.anadol.rememberwords.model.Word;

import java.util.ArrayList;

public class UpdateExamWordsBackground extends AsyncTask<Word, Void, Boolean> {

    private ContentResolver contentResolver;
    private ArrayList<Word> mWords;
    private OnPost mPost;
    public interface OnPost{
        void doOnPost();
    }

    public UpdateExamWordsBackground(Context context, ArrayList<Word> word, OnPost onPost) {
        contentResolver = context.getContentResolver();
        mWords = word;
        mPost = onPost;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Word... words) {
        MyCursorWrapper cursor = null;

        try {

            for (Word word : mWords) {

                cursor = new MyCursorWrapper(contentResolver.query(
                        DataBaseSchema.Words.CONTENT_URI,
                        null,
                        DataBaseSchema.Words.UUID + " = ?",
                        new String[]{word.getUUIDString()}, null));

                if (cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    cursor.getWordExam(word);
                }
            }

            if (cursor != null) {
                cursor.close();
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (mPost != null){
            mPost.doOnPost();
        }
    }
}

