package com.anadol.rememberwords.model;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

import com.anadol.rememberwords.model.DataBaseSchema.Groups;
import com.anadol.rememberwords.model.DataBaseSchema.Words;

import java.util.UUID;

public class MyCursorWrapper extends CursorWrapper {

    private static final String TAG = MyCursorWrapper.class.getName();

    public MyCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Group getGroup() {
        int tableId = getInt(getColumnIndex(Groups._ID));
        String uuidString = getString(getColumnIndex(Groups.UUID));
        String name = getString(getColumnIndex(Groups.NAME_GROUP));
        String drawable = getString(getColumnIndex(Groups.DRAWABLE));
        int type = getInt(getColumnIndex(Groups.TYPE));

        return new Group(
                tableId,
                UUID.fromString(uuidString),
                drawable,
                name,
                type);
    }

    public Word getWord() {
        int tableId = getInt(getColumnIndex(Words._ID));
        String uuidString = getString(getColumnIndex(Words.UUID));
        String original = getString(getColumnIndex(Words.ORIGINAL));
        String translate = getString(getColumnIndex(Words.TRANSLATE));
        String association = getString(getColumnIndex(Words.ASSOCIATION));
        String uuidGroupString = getString(getColumnIndex(Words.UUID_GROUP));
        String comment = getString(getColumnIndex(Words.COMMENT));
        String difficult = getString(getColumnIndex(Words.DIFFICULT));

        return new Word(
                tableId,
                UUID.fromString(uuidString),
                UUID.fromString(uuidGroupString),
                original,
                association,
                translate,
                comment,
                difficult);
    }
}
