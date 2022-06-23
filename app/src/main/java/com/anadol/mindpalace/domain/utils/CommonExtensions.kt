package com.anadol.mindpalace.domain.utils

import android.database.Cursor

fun <T : Any> T.isDuplicate(t: T): Boolean = (this == t)

fun Cursor.getFirstInt(): Int = this.getFirst { it.getInt(0) }

fun <T : Any> Cursor.getFirst(block: (Cursor) -> T): T = this.use {
    this.moveToFirst()

    block.invoke(this)
}
