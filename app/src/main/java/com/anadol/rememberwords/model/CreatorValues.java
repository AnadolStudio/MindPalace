package com.anadol.rememberwords.model;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import com.anadol.rememberwords.model.DataBaseSchema.Groups;
import com.anadol.rememberwords.model.DataBaseSchema.Words;

import java.util.UUID;

public class CreatorValues {

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

    public static ContentValues createWordsValues(@NonNull UUID uuid,
                                                  @NonNull String uuidGroup,
                                                  @NonNull String orig,
                                                  @NonNull String association,
                                                  @NonNull String translate,
                                                  @NonNull String comment,
                                                  @NonNull String difficult) {

        ContentValues values = new ContentValues();
        values.put(Words.UUID, uuid.toString());
        values.put(Words.UUID_GROUP, uuidGroup);
        values.put(Words.ORIGINAL, orig);
        values.put(Words.ASSOCIATION, association);
        values.put(Words.TRANSLATE, translate);
        values.put(Words.COMMENT, comment);
        values.put(Words.DIFFICULT, difficult);

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
        values.put(Words.DIFFICULT, word.getDifficult().toString());

        return values;
    }

}
