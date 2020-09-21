package com.anadol.rememberwords.model;

import android.database.Cursor;
import android.database.CursorWrapper;

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

        if (drawable == null || drawable.equals("")) {
            //TODO удалить, когда версия DB будет равна 7 (Сейчас 5 (21.09.2020))
            int[] colors = new int[3];
            colors[0] = getInt(getColumnIndex(Groups.COLOR_ONE));
            colors[1] = getInt(getColumnIndex(Groups.COLOR_TWO));
            colors[2] = getInt(getColumnIndex(Groups.COLOR_THREE));
            drawable = Group.getColorsStringFromInts(colors);
        }

        return new Group(
                tableId,
                UUID.fromString(uuidString),
                drawable,
                name);
    }

    public Word getWord() {
        int tableId = getInt(getColumnIndex(Words._ID));
        String uuidString = getString(getColumnIndex(Words.UUID));
        String original = getString(getColumnIndex(Words.ORIGINAL));
        String translate = getString(getColumnIndex(Words.TRANSLATE));
        String association = getString(getColumnIndex(Words.ASSOCIATION));
        String uuidGroupString = getString(getColumnIndex(Words.UUID_GROUP));
        String comment = getString(getColumnIndex(Words.COMMENT));
        //TODO удалить, когда версия DB будет равна 7 (Сейчас 5 (21.09.2020))
        String nameGroup = getString(getColumnIndex(Words.NAME_GROUP));

        return new Word(
                tableId,
                UUID.fromString(uuidString),
                original,
                translate,
                association,
                nameGroup,
                uuidGroupString,
                comment);
    }
}
