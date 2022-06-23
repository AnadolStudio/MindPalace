package com.anadol.mindpalace.view.adapters

import android.text.InputFilter
import android.text.Spanned

class MultipleFilter : InputFilter {

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        //source - это новые символы, а dest - все остальные
        if (end - start == 1) {

            when (source[start]) {
                '\n' -> {
                    if (dest.isNotEmpty() && dest[dend - 1] != ';') return ";$source"
                    if (dest.isEmpty()) return ""
                }
                ';' -> return if (dest.isNotEmpty()) "$source\n" else ""
            }
        }

        return null
    }
}
