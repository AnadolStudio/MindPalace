package com.anadol.mindpalace.data.group;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;

import com.anadol.mindpalace.data.group.DataBaseSchema.Groups;
import com.anadol.mindpalace.data.group.DataBaseSchema.Words;

import java.util.UUID;

public class GroupCursorWrapper extends CursorWrapper {

    private static final String TAG = GroupCursorWrapper.class.getName();

    public GroupCursorWrapper(Cursor cursor) {
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
        int countLear = getInt(getColumnIndex(Words.COUNT_LEARN));
        long time = getLong(getColumnIndex(Words.TIME));
        boolean exam = getInt(getColumnIndex(Words.EXAM)) == 1;

        return new Word(
                tableId,
                UUID.fromString(uuidString),
                UUID.fromString(uuidGroupString),
                original,
                association,
                translate,
                comment,
                countLear,
                time,
                exam);
    }

    public void getWordExam(Word word) {
        int countLear = getInt(getColumnIndex(Words.COUNT_LEARN));
        long time = getLong(getColumnIndex(Words.TIME));
        boolean exam = getInt(getColumnIndex(Words.EXAM)) == 1;

        word.setCountLearn(countLear);
        word.setTime(time);
        word.setExam(exam);
    }
}
