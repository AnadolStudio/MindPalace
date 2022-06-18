package com.anadolstudio.core.util

interface DoubleClickExit {

    fun click(listener: (Boolean) -> Unit)

    class Base(val delay: Long = 2000) : DoubleClickExit {
        var backPressed: Long = 0L

        override fun click(listener: (Boolean) -> Unit) {
            val currentTime = System.currentTimeMillis()
            listener.invoke(backPressed + delay > currentTime)
            backPressed = currentTime
        }
    }
}
