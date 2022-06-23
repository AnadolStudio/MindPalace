package com.anadol.mindpalace.data.statistic;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.anadol.mindpalace.data.group.DataBaseSchema;
import com.anadol.mindpalace.data.group.GroupCursorWrapper;

import java.util.ArrayList;
import java.util.List;

public class StatisticService {

    public static List<GroupStatisticItem> getGroupStatistic(Context context) {
        List<GroupStatisticItem> statisticItems = new ArrayList<>();

        ContentResolver contentResolver = context.getContentResolver();

        GroupCursorWrapper groupCursor = new GroupCursorWrapper(contentResolver.query(
                DataBaseSchema.Groups.CONTENT_URI,
                null, null, null, null));

        if (groupCursor.getCount() != 0) {
            groupCursor.moveToFirst();

            GroupStatisticItem item;
            String name;
            String uuidGroup;
            int needToLearn = 0;
            int learning = 0;
            int learned = 0;


            while (!groupCursor.isAfterLast()) {
                name = groupCursor.getString(groupCursor.getColumnIndex(DataBaseSchema.Groups.NAME_GROUP));
                uuidGroup = groupCursor.getString(groupCursor.getColumnIndex(DataBaseSchema.Groups.UUID));

                // Need to learn
                Cursor wordCursor = contentResolver.query(
                        DataBaseSchema.Words.CONTENT_URI,
                        new String[]{"COUNT(" + DataBaseSchema.Words._ID + ") AS count"},
                        DataBaseSchema.Words.UUID_GROUP + " = ? AND (" + DataBaseSchema.Words.TIME + " = ? OR " + DataBaseSchema.Words.TIME
                                + " IS NULL)",
                        new String[]{uuidGroup, "0"}, null);

                if (wordCursor != null) {
                    wordCursor.moveToFirst();
                    needToLearn = wordCursor.getInt(0);
                    wordCursor.close();
                }

                // Learning
                wordCursor = contentResolver.query(
                        DataBaseSchema.Words.CONTENT_URI,
                        new String[]{"COUNT(" + DataBaseSchema.Words._ID + ") AS count"},
                        DataBaseSchema.Words.UUID_GROUP + " = ? AND " + DataBaseSchema.Words.TIME + " != ? AND " + DataBaseSchema.Words.EXAM + " = ?",
                        new String[]{uuidGroup, "0", "0"}, null);

                if (wordCursor != null) {
                    wordCursor.moveToFirst();
                    learning = wordCursor.getInt(0);
                    wordCursor.close();
                }

                // Learned
                wordCursor = contentResolver.query(
                        DataBaseSchema.Words.CONTENT_URI,
                        new String[]{"COUNT(" + DataBaseSchema.Words._ID + ") AS count"},
                        DataBaseSchema.Words.UUID_GROUP + " = ? AND " + DataBaseSchema.Words.EXAM + " = ?",
                        new String[]{uuidGroup, "1"}, null);

                if (wordCursor != null) {
                    wordCursor.moveToFirst();
                    learned = wordCursor.getInt(0);
                    wordCursor.close();
                }

                item = new GroupStatisticItem(name, needToLearn, learning, learned);
                statisticItems.add(item);

                groupCursor.moveToNext();
            }
        }
        groupCursor.close();

        return statisticItems;
    }

}
