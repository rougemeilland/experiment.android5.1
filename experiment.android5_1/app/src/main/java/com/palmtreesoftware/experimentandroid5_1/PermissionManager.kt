package com.palmtreesoftware.experimentandroid5_1

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

// 権限の許可の求め方について、以下のサイトが非常にわかりやすく書かれていて参考になりました。
// https://techbooster.org/android/application/17223/

/**
 * パーミッションを管理するクラスのスーパークラスです。
 * @param expectedPermissions このクラスのインスタンスで管理すべきパーミッションのIDの配列です。
 */
abstract class PermissionManager(private val expectedPermissions: Array<String>) {
    /**
     * expectedPermissions で与えられたパーミッションの要求を非同期処理で開始します。
     * @param activity アプリケーションのアクティビティです。
     */
    fun requestPermission(activity: Activity) {
        // 許可されていないパーミッションを見つける
        val notAllowedPermissions =
            expectedPermissions.filter {
                ActivityCompat.checkSelfPermission(
                    activity,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }
                .toTypedArray()
        if (notAllowedPermissions.isEmpty()) {
            // 許可されていないパーミッションが見つからない場合は何もしない
            onRequested(activity, arrayOf(), arrayOf())
            return
        }

        // 【重要】 shouldShowRequestPermissionRationale() はあくまでも
        // 「ユーザが以前にそのパーミッションに対して永続的な拒否(二度と表示しない)を選択していたかどうか」
        // を返す関数である。
        // shouldShowRequestPermissionRationale() 単体で何らかの画面を表示したりユーザの判断を求めるようなことはない
        if (notAllowedPermissions.any {
                ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
            }) {
            // 許可されていないパーミッションのうち、少なくとも一つは永続的に拒否されていないパーミッションがある場合
            // パーミッションの説明ダイアログを表示した後、パーミッションを要求する
            beforeRequesting(activity, notAllowedPermissions) {
                requestPermissions(activity, notAllowedPermissions)
            }
        } else {
            // 持っていないパーミッションのうち、すべてのパーミッションが永続的に拒否されている場合

            // 既に過去に説明済みであるはずなので、説明なしでパーミッションを要求する
            requestPermissions(activity, notAllowedPermissions)
        }
    }

    /**
     * システムによって通知されたパーミッション要求処理結果を受けるメソッドです。
     * このメソッドは、アクティビティでオーバーライドされた onRequestPermissionsResult にて呼び出す必要があります。
     * また、このメソッドの復帰値が false であった場合に super.onRequestPermissionsResult(requestCode, permission, grantResults) を実行するといいでしょう。
     * @param activity アプリケーションのアクティビティです。
     * @param requestCode アクティビティでオーバーライドされた onRequestPermissionsResult の第1パラメタをそのまま渡してください。
     * @param permissions アクティビティでオーバーライドされた onRequestPermissionsResult の第2パラメタをそのまま渡してください。
     * @param grantResults アクティビティでオーバーライドされた onRequestPermissionsResult の第3パラメタをそのまま渡してください。
     * @return 通知されたパーミッション処理結果を適切に処理できたなら true 、そうではないのなら false を返します。
     */
    fun onRequestPermissionsResult(
        activity: Activity,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        return when (requestCode) {
            REQUEST_CODE_PERMISSION -> {
                permissions
                    .zip(grantResults.toTypedArray())
                    .filter { it.second != PackageManager.PERMISSION_GRANTED }
                    .map { it.first }
                    .toTypedArray()
                    .let { deniedPermissions ->
                        onRequested(activity, deniedPermissions, deniedPermissions
                            .filter { deniedPermission ->
                                !ActivityCompat.shouldShowRequestPermissionRationale(
                                    activity,
                                    deniedPermission
                                )
                            }
                            .toTypedArray()
                        )
                    }

                // 「処理済み」を示す true を返す
                true
            }
            else -> {
                //未知のリクエストコードである場合、「未処理」を示す false を返す
                false
            }
        }
    }

    /**
     * アプリケーションの動作に最低限必要なパーミッションが許可されているかどうかを調べます。
     * 既定の実装は expectedPermissions で与えられたパーミッションがすべて許可されているかどうかを返しますが、
     * メソッドをオーバーライドすることによりこの動作をカスタマイズできます。
     * @param context Contextオブジェクトです。
     * @return アプリケーションの動作に最低限必要なパーミッションが許可されていれば true 、そうではないのなら false です。
     */
    open fun isAllowedMinimumPermission(context: Context): Boolean =
        expectedPermissions.all {
            ActivityCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

    /**
     * コンストラクタに与えれられたすべてのパーミッションが許可されているかどうかを調べます。
     * @param context Contextオブジェクトです。
     * @return コンストラクタに与えれられたすべてのパーミッションが許可されていれば true 、そうではないのなら false です。
     */
    fun isAllowedAllermission(context: Context): Boolean =
        expectedPermissions.all {
            ActivityCompat.checkSelfPermission(
                context,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

    /**
     * コンストラクタで与えられたパーミッションのうち、現在許可されておらずかつ永続的には拒否されていないパーミッションが少なくとも1つある場合に呼び出されます。
     * このメソッドのオーバーライドでは、現在許可されていないパーミッションの必要性について説明するダイアログを表示し、ユーザーにパーミッションの要求の意思がある場合はダイアログが閉じるときに onContinued() を実行すべきです。
     * @param activity 依頼元のアクティビティです。
     * @param notAllowedPermissions コンストラクタで与えられたパーミッションのうち、現在許可されていないパーミッションのIDの配列です。
     * @param onContinued notAllowedPermissions で与えられたパーミッションの要求処理を続行するメソッドです。 beforeRequestingPermissions メソッドの処理が完了したときに、 onContinued を同期的または非同期的に呼び出すことができます。
     */
    protected abstract fun beforeRequesting(
        activity: Activity,
        notAllowedPermissions: Array<String>,
        onContinued: () -> Unit
    )

    /**
     * パーミッションの要求処理が完了したときに呼び出されます。
     * 通常、このメソッドをオーバーライドする必要はありませんが、
     * deniedPermanentlyPermissions が空ではない場合に以下の動作をするダイアログを表示するのも
     * いいかもしれません。
     * - ユーザーがアプリに対するパーミッションの必要性を忘れないように注意を喚起する。
     * - アプリのパーミッションを設定する画面へ誘導する。
     * @param activity 依頼元のアクティビティです。
     * @param deniedPermissions 拒否されたパーミッションのIDの配列です。
     * @param deniedPermanentlyPermissions 永続的に拒否されたパーミッションのIDの配列です。
     */
    protected open fun onRequested(
        activity: Activity,
        deniedPermissions: Array<String>,
        deniedPermanentlyPermissions: Array<String>
    ) {
        // NOP
    }

    companion object {
        private const val REQUEST_CODE_PERMISSION = 1000

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
    }
}
