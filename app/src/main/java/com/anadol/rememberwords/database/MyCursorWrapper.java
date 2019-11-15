package com.anadol.rememberwords.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.anadol.rememberwords.myList.Group;
import com.anadol.rememberwords.database.DbSchema.Tables.Cols;
import com.anadol.rememberwords.myList.Word;

import java.util.UUID;

public class MyCursorWrapper extends CursorWrapper {

    public MyCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Group getGroup(){
        String uuidString = getString(getColumnIndex(Cols.UUID));
        int colorOne= getInt(getColumnIndex(Cols.COLOR_ONE));
        int colorTwo = getInt(getColumnIndex(Cols.COLOR_TWO));
        int colorThree = getInt(getColumnIndex(Cols.COLOR_THREE));
        String name = getString(getColumnIndex(Cols.NAME_GROUP));

        return new Group(UUID.fromString(uuidString),
                new int[]{colorOne,colorTwo,colorThree},
                name);
    }

    public Word getWord(){
        String uuidString = getString(getColumnIndex(Cols.UUID));
        String orig= getString(getColumnIndex(Cols.ORIGINAL));
        String trans = getString(getColumnIndex(Cols.TRANSLATE));
        String transcript = getString(getColumnIndex(Cols.TRANSCRIPTION));
        String name = getString(getColumnIndex(Cols.NAME_GROUP));
        String comment = getString(getColumnIndex(Cols.COMMENT));
        int isMultiTrans = getInt(getColumnIndex(Cols.IS_MULTI_TRANS));

        return new Word(UUID.fromString(uuidString), orig, trans, transcript, name, comment, isMultiTrans);
    }
}
