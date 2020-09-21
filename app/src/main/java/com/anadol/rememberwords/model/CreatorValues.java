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
                                                  @NonNull int[] colors) {

        ContentValues values = new ContentValues();
        values.put(Groups.UUID, uuid.toString());
        values.put(Groups.NAME_GROUP, name);
        values.put(Groups.DRAWABLE, drawable);
        //TODO удалить, когда версия DB будет равна 7 (Сейчас 5 (21.09.2020))
        values.put(Groups.COLOR_ONE, colors[0]);
        values.put(Groups.COLOR_TWO, colors[1]);
        values.put(Groups.COLOR_THREE, colors[2]);
        return values;
    }

    public static ContentValues createWordsValues(@NonNull UUID uuid,
                                                  @NonNull String uuidGroup,
                                                  @NonNull String orig,
                                                  @NonNull String association,
                                                  @NonNull String translate,
                                                  @NonNull String comment) {

        ContentValues values = new ContentValues();
        values.put(Words.UUID, uuid.toString());
        values.put(Words.UUID_GROUP, uuidGroup);
        values.put(Words.ORIGINAL, orig);
        values.put(Words.ASSOCIATION, association);
        values.put(Words.TRANSLATE, translate);
        values.put(Words.COMMENT, comment);

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

        return values;
    }

}
