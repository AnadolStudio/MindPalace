package com.anadolstudio.core.util

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

interface PermissionUtil {

    fun checkPermission(activity: AppCompatActivity): Boolean

    fun shouldShowRequestPermissionRationale(activity: AppCompatActivity): Boolean

    fun requestPermission(
        activity: AppCompatActivity,
        requestCode: Int
    )

    abstract class Abstract(private val permission: String) : PermissionUtil {

        companion object {
            const val DEFAULT_REQUEST_CODE = 1
        }

        override fun requestPermission(
            activity: AppCompatActivity,
            requestCode: Int
        ) = ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)

        override fun checkPermission(activity: AppCompatActivity): Boolean = ActivityCompat
            .checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED

        override fun shouldShowRequestPermissionRationale(activity: AppCompatActivity): Boolean =
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity, permission
            )

    }

    object WriteExternalStorage : Abstract(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    object ReadExternalStorage : Abstract(Manifest.permission.READ_EXTERNAL_STORAGE)
}