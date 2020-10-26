package com.anadol.rememberwords.model;

import android.content.ContentValues;
import android.util.Log;

import androidx.annotation.NonNull;

import com.anadol.rememberwords.model.DataBaseSchema.Groups;
import com.anadol.rememberwords.model.DataBaseSchema.Words;

import java.util.UUID;

public class CreatorValues {

    private static final String TAG = CreatorValues.class.getName();

    public static ContentValues createGroupValues(@NonNull UUID uuid,
                                                  @NonNull String name,
                                                  @NonNull String drawable,
                                                  int type) {

        ContentValues values = new ContentValues();
        values.put(Groups.UUID, uuid.toString());
        values.put(Groups.NAME_GROUP, name);
        values.put(Groups.DRAWABLE, drawable);
        values.put(Groups.TYPE, type);

        return values;
    }

    // Используется для создания новых слов
    public static ContentValues createWordsValues(@NonNull UUID uuid,
                                                  @NonNull String uuidGroup,
                                                  @NonNull String orig,
                                                  @NonNull String association,
                                                  @NonNull String translate,
                                                  @NonNull String comment,
                                                  int countLearn,
                                                  long time,
                                                  boolean isExam) {

        ContentValues values = new ContentValues();
        values.put(Words.UUID, uuid.toString());
        values.put(Words.UUID_GROUP, uuidGroup);
        values.put(Words.ORIGINAL, orig);
        values.put(Words.ASSOCIATION, association);
        values.put(Words.TRANSLATE, translate);
        values.put(Words.COMMENT, comment);
        values.put(Words.COUNT_LEARN, countLearn);
        values.put(Words.TIME, time);
        values.put(Words.EXAM, isExam);

        return values;
    }

    public static ContentValues createWordsLearnValues(int countLearn,
                                                  long time,
                                                  boolean isExam) {

        ContentValues values = new ContentValues();
        values.put(Words.COUNT_LEARN, countLearn);
        values.put(Words.TIME, time);
        values.put(Words.EXAM, isExam ? 1 : 0);
        Log.i(TAG, "createWordsLearnValues: " + values);
        return values;
    }
    public static ContentValues createWordsValues(@NonNull Word word) {

        ContentValues values = new ContentValues();
        values.put(Words.UUID, word.getUUIDString());
        values.put(Words.UUID_GROUP, word.getGroupUUIDString());
        values.put(Words.ORIGINAL, word.getOriginal());
        values.put(Words.ASSOCIATION, word.getAssociation());
        values.put(Words.TRANSLATE, word.getTranslate());
        values.put(Words.COMMENT, word.getComment());
        // Эти параметры сохраняютися только после Learn
//        values.put(Words.COUNT_LEARN, word.getCountLearn());
//        values.put(Words.TIME, word.getTime());
//        values.put(Words.EXAM, word.isExam());

        return values;
    }

}
