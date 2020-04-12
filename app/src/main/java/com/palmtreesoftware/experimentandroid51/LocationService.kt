package com.palmtreesoftware.experimentandroid51

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// 権限の許可の求め方について、以下のサイトが非常にわかりやすく書かれていて参考になりました。
// https://techbooster.org/android/application/17223/

enum class LocationPermissionStatus
{
    NOT_ALLOWED_AT_ALL,
    PARTIALLY_ALLOWED,
    ALL_ALLOWED
}

abstract class LocationService(context: Context) {

    private class InnerLocationService(val context: Context, val parent: LocationService) :
        LocationListener {
        private val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        private val handler = Handler()
        private var currentServiceStatus: Boolean = false

        override fun onLocationChanged(location: Location?) {
            if (location != null) {
                raiseOnLocationChanged(location.latitude, location.longitude)
            }
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            stop()
            start()
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }

        @SuppressLint("MissingPermission")
        fun start() {
            raiseOnServiceStopped()
            val allowedPermission = getAllowedPermissions(context)
            if (allowedPermission.isEmpty()) {
                raiseOnServiceStopped()
            } else {
                val criteria = Criteria()
                criteria.accuracy =
                    if (allowedPermission.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Criteria.ACCURACY_FINE
                    } else {
                        Criteria.ACCURACY_COARSE
                    }
                criteria.powerRequirement = Criteria.POWER_LOW
                locationManager.getBestProvider(criteria, true).let { provider ->
                    if (provider != null) {
                        try {
                            locationManager.requestLocationUpdates(
                                provider,
                                5000,
                                10.toFloat(),
                                this
                            )
                        } catch (ex: Exception) {
                            raiseOnServiceStopped()
                        }
                    } else {
                        raiseOnServiceStopped()
                    }
                }
            }
        }

        fun stop() {
            locationManager.removeUpdates(this)
            raiseOnServiceStopped()
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun requestPermission(activity: Activity) {
            val notAllowedPermissions = arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
                .filter { permission ->
                    ContextCompat.checkSelfPermission(
                        activity,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                }.toTypedArray()
            if (notAllowedPermissions.isEmpty()) {
                // 指定された権限をすべて持っている場合
                return
            }

            // 何れかの権限も持っていない場合
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    notAllowedPermissions[0]
                )
            ) {
                // ユーザーが以前にリクエストを拒否した場合
                AlertDialog.Builder(activity)
                    .setTitle("権限の追加説明")
                    .setMessage(
                        "このアプリで現在位置の気象情報を表示するためには「現在位置」の権限を許可されている必要があります。\n" +
                                "「現在位置」の権限が許可されていなくてもアプリは使用できますが、現在位置の気象情報を表示する機能が使用できなくなります。"
                    )
                    .setPositiveButton(
                        android.R.string.ok
                    ) { _, _ ->
                        requestPermissions(activity, notAllowedPermissions)
                    }
                    .create()
                    .show()
            } else {
                // ユーザーがパーミッションを拒否し、パーミッション リクエスト ダイアログで [今後表示しない] を選択した場合、
                // またはデバイス ポリシーによってパーミッションが禁止されている場合
                requestPermissions(activity, notAllowedPermissions)
            }
        }

        // 既知のリクエストコードを処理できたなら true, リクエストコードが未知であったならば false を返す
        @RequiresApi(Build.VERSION_CODES.M)
        fun onRequestPermissionsResult(
            activity: Activity,
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
        ): Boolean {
            if (requestCode != REQUEST_CODE_PERMISSION) {
                // 未知のリクエストコードである
                return false
            }

            // 許可されなかった権限を抽出する
            val notAllowedPermissions = permissions.zip(grantResults.toTypedArray())
                .filter { it.second != PackageManager.PERMISSION_GRANTED }.map { it.first }
                .toTypedArray()

            if (notAllowedPermissions.isEmpty()) {
                // 許可されなかった権限はない
                return true
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    notAllowedPermissions[0]
                )
            ) {
                AlertDialog.Builder(activity)
                    .setTitle("権限取得エラー")
                    .setMessage("再試行する場合は、OKボタンを押してください。")
                    .setPositiveButton(
                        android.R.string.ok
                    ) { _, _ ->
                        requestPermissions(activity, notAllowedPermissions)
                    }.setNegativeButton(android.R.string.cancel) { _, _ -> }
                    .create()
                    .show()
                return true
            } else {
                AlertDialog.Builder(activity)
                    .setTitle("権限取得エラー")
                    .setMessage(
                        "「位置情報」の権限について「許可しない」が選択されました。\n" +
                                "このアプリで現在位置の気象情報を表示するためには「現在位置」の権限を許可されている必要があります。\n" +
                                "「現在位置」の権限が許可されていなくてもアプリは使用できますが、現在位置の気象情報を表示する機能が使用できなくなります。\n" +
                                "アプリの権限を確認するためにはOKボタンを押してください。(権限をON/OFFすることで状態はリセットされます)"
                    )
                    .setPositiveButton(
                        android.R.string.ok
                    ) { _, _ ->
                        openApplicationSettingDialog(activity)
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ -> }
                    .create()
                    .show()
                return true
            }
        }

        fun getCurrentPermissionStatus(): LocationPermissionStatus {
            val newLocationPermissionStatus = arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
                .map { permission ->
                    ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    )
                }.distinct().toTypedArray().let { array ->
                    when {
                        array.all { it == PackageManager.PERMISSION_GRANTED } -> {
                            LocationPermissionStatus.ALL_ALLOWED
                        }
                        array.all { it != PackageManager.PERMISSION_GRANTED } -> {
                            LocationPermissionStatus.NOT_ALLOWED_AT_ALL
                        }
                        else -> {
                            LocationPermissionStatus.PARTIALLY_ALLOWED
                        }
                    }
                }
            return newLocationPermissionStatus
        }

        private fun getAllowedPermissions(context: Context): Array<String> {
            return getPermissions(
                context
            ) { result -> result == PackageManager.PERMISSION_GRANTED }
        }

        /*
        private fun getNotAllowedPermissions(context: Context): Array<String> {
            return getPermissions(
                context,
                { result -> result != PackageManager.PERMISSION_GRANTED })
        }
        */

        private fun getPermissions(
            context: Context,
            predicate: (Int) -> Boolean
        ): Array<String> {
            return arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
                .mapNotNull { permission ->
                    if (predicate(
                            ActivityCompat.checkSelfPermission(
                                context,
                                permission
                            )
                        )
                    )
                        permission
                    else
                        null
                }.toTypedArray()
        }

        private fun openApplicationSettingDialog(
            activity: Activity
        ) {
            Intent().also {
                it.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                it.data = Uri.parse("package:${activity.packageName}")
                activity.startActivity(it)
            }
        }

        private fun requestPermissions(
            activity: Activity,
            permissions: Array<String>
        ) {
            ActivityCompat.requestPermissions(
                activity,
                permissions,
                REQUEST_CODE_PERMISSION
            )
        }

        private fun raiseOnServiceStopped() {
            handler.post {
                if (currentServiceStatus) {
                    currentServiceStatus = false
                    parent.onServiceStatusChanged(false)
                }
            }
        }

        private fun raiseOnLocationChanged(latitude: Double, longitude: Double){
            handler.post {
                if (!currentServiceStatus) {
                    currentServiceStatus = true
                    parent.onServiceStatusChanged(true)
                }
                parent.onLocationChanged(latitude, longitude)
            }
        }

        companion object {
            const val REQUEST_CODE_PERMISSION = 1000
        }
    }

    private val imp: InnerLocationService by lazy { InnerLocationService(context, this) }

    val currentPermissionStatus: LocationPermissionStatus
        get() = imp.getCurrentPermissionStatus()

    fun start() {
        imp.stop()
        imp.start()
    }

    fun stop() = imp.stop()

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermission(activity: Activity) = imp.requestPermission(activity)

    @RequiresApi(Build.VERSION_CODES.M)
    fun onRequestPermissionsResult(
        activity: Activity,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean = imp.onRequestPermissionsResult(activity, requestCode, permissions, grantResults)

    abstract fun onServiceStatusChanged(isRunning: Boolean)

    abstract fun onLocationChanged(latitude: Double, longitude: Double)
}
