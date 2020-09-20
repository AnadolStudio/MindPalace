package com.anadol.rememberwords.model;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.anadol.rememberwords.database.DbSchema.Tables.Cols;
import com.anadol.rememberwords.model.DataBaseSchema.Groups;
import com.anadol.rememberwords.model.DataBaseSchema.Words;

import java.util.UUID;

public class MyCursorWrapper extends CursorWrapper {

    public MyCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Group getGroup() {
        int tableId = getInt(getColumnIndex(Groups._ID));
        String uuidString = getString(getColumnIndex(Groups.UUID));
        String name = getString(getColumnIndex(Groups.NAME_GROUP));
        String drawable = getString(getColumnIndex(Groups.DRAWABLE));

        return new Group(
                tableId,
                UUID.fromString(uuidString),
                drawable,
                name);
    }

    public Word getWord() {
        int tableId = getInt(getColumnIndex(Words._ID));
        String uuidString = getString(getColumnIndex(Cols.UUID));
        String orig = getString(getColumnIndex(Cols.ORIGINAL));
        String trans = getString(getColumnIndex(Cols.TRANSLATE));
        String transcript = getString(getColumnIndex(Cols.TRANSCRIPTION));
        String uuidGroupString = getString(getColumnIndex(Cols.NAME_GROUP));
        String comment = getString(getColumnIndex(Cols.COMMENT));

        return new Word(
                tableId,
                UUID.fromString(uuidString),
                orig,
                trans,
                transcript,
                UUID.fromString(uuidGroupString),
                comment);
    }
}
