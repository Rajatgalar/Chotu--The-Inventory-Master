package com.itechnowizard.chotu.utils


import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class AppPermission {
    companion object {
        const val REQUEST_PERMISSION: Int = 123
//        fun permissionGranted(context: Context) =
//            ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED

        fun permissionGranted(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                true // Always return true for API level 33 or above
            } else {
                ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PERMISSION_GRANTED
            }
        }

        fun requestPermission(activity: Activity) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION
            )
        }
    }
}