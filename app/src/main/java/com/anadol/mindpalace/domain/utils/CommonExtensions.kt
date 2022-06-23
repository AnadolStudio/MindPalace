package com.anadol.mindpalace.domain.utils

fun <T : Any> T.isDuplicate(t: T): Boolean = (this == t)
