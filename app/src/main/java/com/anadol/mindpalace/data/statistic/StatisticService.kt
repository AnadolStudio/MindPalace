package com.anadol.mindpalace.data.statistic

import android.annotation.SuppressLint
import android.content.Context
import com.anadol.mindpalace.data.group.DataBaseSchema
import com.anadol.mindpalace.data.group.GroupCursorWrapper
import com.anadol.mindpalace.domain.utils.getFirstInt

object StatisticService {

    @SuppressLint("Recycle")
    fun getGroupStatistic(context: Context): List<GroupStatisticItem> {
        val statisticItems: MutableList<GroupStatisticItem> = ArrayList()
        val contentResolver = context.contentResolver

        GroupCursorWrapper(
            contentResolver.query(DataBaseSchema.Groups.CONTENT_URI, null, null, null, null)
        ).use { groupCursor ->
            if (groupCursor.count != 0) {
                groupCursor.moveToFirst()

                var item: GroupStatisticItem
                var name: String?
                var uuidGroup: String

                while (!groupCursor.isAfterLast) {
                    name = groupCursor.getString(groupCursor.getColumnIndex(DataBaseSchema.Groups.NAME_GROUP))
                    uuidGroup = groupCursor.getString(groupCursor.getColumnIndex(DataBaseSchema.Groups.UUID))

                    // Need to learn
                    val needToLearn = contentResolver.query(
                        DataBaseSchema.Words.CONTENT_URI, arrayOf("COUNT(" + DataBaseSchema.Words._ID + ") AS count"),
                        DataBaseSchema.Words.UUID_GROUP + " = ? AND (" + DataBaseSchema.Words.TIME + " = ? OR " + DataBaseSchema.Words.TIME
                                + " IS NULL)", arrayOf(uuidGroup, "0"), null
                    )?.getFirstInt() ?: 0

                    // Learning
                    val learning = contentResolver.query(
                        DataBaseSchema.Words.CONTENT_URI,
                        arrayOf("COUNT(" + DataBaseSchema.Words._ID + ") AS count"),
                        DataBaseSchema.Words.UUID_GROUP + " = ? AND " + DataBaseSchema.Words.TIME + " != ? AND " + DataBaseSchema.Words.EXAM + " = ?",
                        arrayOf(uuidGroup, "0", "0"),
                        null
                    )?.getFirstInt() ?: 0

                    // Learned
                    val learned = contentResolver.query(
                        DataBaseSchema.Words.CONTENT_URI,
                        arrayOf("COUNT(" + DataBaseSchema.Words._ID + ") AS count"),
                        DataBaseSchema.Words.UUID_GROUP + " = ? AND " + DataBaseSchema.Words.EXAM + " = ?",
                        arrayOf(uuidGroup, "1"),
                        null
                    )?.getFirstInt() ?: 0

                    item = GroupStatisticItem(name, needToLearn, learning, learned)
                    statisticItems.add(item)
                    groupCursor.moveToNext()
                }
            }
        }

        return statisticItems
    }

}
