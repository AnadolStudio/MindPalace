package com.anadolstudio.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface

interface BitmapDecoder {

    fun decode(path: String, reqWidth: Int, reqHeight: Int): Bitmap

    abstract class Abstract : BitmapDecoder {

        open fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int,
            reqHeight: Int,
            isHard: Boolean = true
        ): Int {
            // Реальные размеры изображения
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > reqHeight || width > reqWidth) {
                val halfHeight = height / 2
                val halfWidth = width / 2

                while (
                    if (isHard) halfHeight / inSampleSize > reqHeight || halfWidth / inSampleSize > reqWidth
                    else halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth
                ) {
                    inSampleSize *= 2
                }

            }
            return inSampleSize
        }

        protected fun getDegree(orientation: Int): Int = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }

        protected fun rotate(source: Bitmap, degree: Float): Bitmap {
            if (degree == 0f) return source

            val matrix = Matrix().apply {
                postRotate(degree)
            }

            return Bitmap.createBitmap(
                source, 0, 0,
                source.width, source.height,
                matrix, true
            )
        }
    }

    class FromRealPath : Abstract() {

        override fun decode(path: String, reqWidth: Int, reqHeight: Int): Bitmap {

            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true

            //RealPath
            BitmapFactory.decodeFile(path, options)
            val orientation = ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)

            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, true)
            options.inJustDecodeBounds = false
            val bitmap: Bitmap = BitmapFactory.decodeFile(path, options)
            val degree: Int = getDegree(orientation)

            return rotate(bitmap, degree.toFloat())
        }

    }

    class FromContentPath(val context: Context) : Abstract() {

        override fun decode(path: String, reqWidth: Int, reqHeight: Int): Bitmap {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true

            // Content
            var pfd = context.contentResolver.openFileDescriptor(Uri.parse(path), "r")
                ?: throw IllegalArgumentException("ParcelFileDescriptor is null")

            val orientation = ExifInterface(pfd.fileDescriptor).getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 1
            )
            pfd.close()
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight, true)
            options.inJustDecodeBounds = false

            pfd = context.contentResolver.openFileDescriptor(Uri.parse(path), "r")
                    //необходимо опять его открыть
                ?: throw IllegalArgumentException("ParcelFileDescriptor is null")

            val bitmap =
                BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor, null, options)

            val degree: Int = getDegree(orientation)
            pfd.close()

            return rotate(bitmap, degree.toFloat())
        }

    }

    object Manager {
        const val CONTENT = "content:"

        fun decodeBitmapFromPath(context: Context, path: String, reqWidth: Int, reqHeight: Int): Bitmap =
            (if (path.contains(CONTENT)) FromContentPath(context) else FromRealPath())
                .decode(path, reqWidth, reqHeight)
    }
}