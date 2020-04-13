package com.palmtreesoftware.experimentandroid5_1

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

// 権限の許可の求め方について、以下のサイトが非常にわかりやすく書かれていて参考になりました。
// https://techbooster.org/android/application/17223/

class PermissionManager private constructor(private val context: Context) {

    @RequiresApi(Build.VERSION_CODES.M)
    fun requestPermission(activity: Activity) {
        // 許可されていないパーミッションを見つける
        val notAllowedPermissionNames =
            allPermissions.filter { !checkPermission(context, it) }.toTypedArray()
        if (notAllowedPermissionNames.isEmpty()) {
            // 許可されていないパーミッションが見つからない場合は何もしない
            return
        }

        // 【重要】 shouldShowRequestPermissionRationale() はあくまでも
        // 「ユーザが以前にそのパーミッションに対して永続的な不許可(二度と表示しない)を選択していたかどうか」
        // を返す関数である。
        // shouldShowRequestPermissionRationale() 単体で何らかの画面を表示したりユーザの判断を求めるようなことはない
        if (notAllowedPermissionNames.any {
                ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
            }) {
            // 持っていないパーミッションのうち、少なくとも一つは永続的に拒否されていないパーミッションがある場合

            // 不足しているパーミッションを挙げ、パーミッションが必要な理由をユーザに説明する

            val messageText = String.format(
                "このアプリでは現在位置の気象情報を表示する機能を使用することができます。\nそのためには以下の権限が許可されている必要があります。\n\n%s\n\n次に表示される画面で上記の権限を許可してください。\n許可されない場合は、現在位置の気象情報を表示する機能を使用することができませんが、それ以外の機能は使用できます。",
                getPermissionDescriptions(notAllowedPermissionNames).joinToString(separator = "\n") {
                    String.format(
                        "・%s",
                        it
                    )
                }
            )
            AlertDialog.Builder(activity)
                .setTitle("このアプリの権限について")
                .setMessage(messageText)
                .setPositiveButton(
                    android.R.string.ok
                ) { _, _ ->
                    requestPermissions(activity, notAllowedPermissionNames)
                }
                .create()
                .show()
        } else {
            // 持っていないパーミッションのうち、すべてのパーミッションが永続的に拒否されている場合

            requestPermissions(activity, notAllowedPermissionNames)
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

        // 許可されなかったパーミッションを抽出する
        val notAllowedPermissionNames = permissions.zip(grantResults.toTypedArray())
            .filter { it.second != PackageManager.PERMISSION_GRANTED }.map { it.first }
            .toTypedArray()

        if (notAllowedPermissionNames.isEmpty()) {
            // 許可されなかったパーミッションはない場合
            return true
        } else {
            // 許可されなかったパーミッションがある場合
            if (notAllowedPermissionNames.none {
                    !ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        it
                    )
                }) {
                // すべてのパーミッションが、「二度と表示しない」ではないが許可されなかった

                // 何もしない
            } else {
                // 許可されなかったパーミッションのうち、永続的に拒否されているものが存在する場合
                // ユーザがその権限が必要な理由を忘れている可能性もあるので、不足しているパーミッションとそれが必要な理由を再表示する
                val messageText = String.format(
                    "以下の権限が許可されませんでした。\n\n%s\n\n上記の権限が許可されない場合は、現在位置の気象情報を表示する機能を使用することができませんが、それ以外の機能は使用できます。\nOKボタンをタッチすると、このアプリの設定画面が開きますので、権限を確認または変更できます。",
                    getPermissionDescriptions(notAllowedPermissionNames).joinToString(separator = "\n") {
                        String.format(
                            "・%s",
                            it
                        )
                    }
                )
                AlertDialog.Builder(activity)
                    .setTitle("権限取得エラー")
                    .setMessage(messageText)
                    .setPositiveButton(
                        android.R.string.ok
                    ) { _, _ ->
                        openApplicationSettingDialog(activity)
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ -> }
                    .create()
                    .show()
            }
            return true
        }
    }

    // 不足している権限と、それがアプリに必要な理由をユーザに説明するための権限の表示名を取得する方法がない。そもそもシステムでも画面によって権限の表示名に表記のブレがある。
    // 仕方がないので、パーミッションIDから自分で切り分けた
    // 最適解とはとても言い難いがとりあえずこれしかない
    // ACCESS_FINE_LOCATION と ACCESS_COARSE_LOCATION はシステムでの表示が被っているので、この関数でも同一の名前にして、できたリストを distinct() している
    private fun getPermissionDescriptions(notAllowedPermissionNames: Array<String>): Array<String> {
        return notAllowedPermissionNames.map {
            when (it) {
                Manifest.permission.INTERNET -> "インターネットへのフルアクセス"
                Manifest.permission.ACCESS_FINE_LOCATION -> "位置情報"
                Manifest.permission.ACCESS_COARSE_LOCATION -> "位置情報"
                else -> throw Exception("${javaClass.canonicalName}.getPermissionDescriptions: Unnown permission: $it")
            }
        }.distinct().toTypedArray()
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

    companion object {
        private const val REQUEST_CODE_PERMISSION = 1000
        private val allPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET
        )

        fun createInstance(context: Context): PermissionManager {
            return PermissionManager(context)
        }

        // アプリの動作に最低限必要な権限があるかどうかを調べる
        fun checkMinimumPermission(context: Context): Boolean {
            return (checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ||
                    checkPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)) &&
                    checkPermission(context, Manifest.permission.INTERNET)
        }

        fun checkPermission(context: Context, permission: String): Boolean {
            return ActivityCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}
