package com.anadolstudio.core.tasks

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread

interface ProgressListener<ProgressData> {

    @MainThread
    fun onProgress(data: ProgressData)

    abstract class Abstract<ProgressData> : ProgressListener<ProgressData> {

        override fun onProgress(data: ProgressData) {
            Handler(Looper.getMainLooper()).post {
                doMain(data)
            }
        }

        abstract fun doMain(data: ProgressData)
    }
}