package com.anadol.rememberwords.model;

import android.content.ContentValues;

import androidx.annotation.NonNull;

import com.anadol.rememberwords.database.DbSchema;

import java.util.UUID;

public class CreatorValues {

    public static ContentValues createGroupValues(@NonNull UUID id,
                                             @NonNull String name,
                                             int colorOne,
                                             int colorTwo,
                                             int colorThree) {

        ContentValues values = new ContentValues();
        values.put(DbSchema.Tables.Cols.UUID, id.toString());
        values.put(DbSchema.Tables.Cols.NAME_GROUP, name);
        values.put(DbSchema.Tables.Cols.COLOR_ONE, colorOne);
        values.put(DbSchema.Tables.Cols.COLOR_TWO, colorTwo);
        values.put(DbSchema.Tables.Cols.COLOR_THREE, colorThree);

        return values;
    }

    public static ContentValues createWordsValues(@NonNull UUID id,
                                             @NonNull String nameGroup,
                                             @NonNull String orig,
                                             @NonNull String trans,
                                             @NonNull String transcript,
                                             @NonNull String comment,
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
