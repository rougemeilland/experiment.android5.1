package com.palmtreesoftware.experimentandroid5_1

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.google.android.gms.common.api.ResolvableApiException
import kotlinx.android.synthetic.main.weather_forecast_setting_view.view.*


class WeatherForecastSettingView(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    interface OnSettingsChangedListener {
        fun onSettingsChanged(view: WeatherForecastSettingView)
    }

    private var onSettingsChangedListener: OnSettingsChangedListener? = null

    private val permissionManager =
        object : PermissionManager(
            Platform.sdK29Depended(
                @RequiresApi(Build.VERSION_CODES.Q) {
                    arrayOf(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION/*,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION*/
                    )
                }, {
                    arrayOf(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                })
        ) {
            /**
             * コンストラクタで与えられたパーミッションのうち、現在許可されておらずかつ永続的には拒否されていないパーミッションが少なくとも1つある場合に呼び出されます。
             * このメソッドのオーバーライドでは、現在許可されていないパーミッションの必要性について説明するダイアログを表示し、ダイアログが閉じるときに onContinued() を実行すべきです。
             * @param activity 依頼元のアクティビティです。
             * @param notAllowedPermissions コンストラクタで与えられたパーミッションのうち、現在許可されていないパーミッションのIDの配列です。
             * @param onContinued notAllowedPermissions で与えられたパーミッションの要求処理を続行するメソッドです。 beforeRequestingPermissions メソッドの処理が完了したときに、 onContinued を同期的または非同期的に呼び出す必要があります。
             */
            override fun beforeRequesting(
                activity: Activity,
                notAllowedPermissions: Array<String>,
                onContinued: () -> Unit
            ) {
                val messageText = String.format(
                    "このアプリでは現在位置の気象情報を表示する機能を使用することができます。\n" +
                            "そのためには以下の権限が許可されている必要があります。\n%s\n" +
                            "次に表示される画面で上記の権限を許可してください。\n" +
                            "許可されない場合は、現在位置の気象情報を表示する機能を使用することができませんが、それ以外の機能は使用できます。",
                    notAllowedPermissions
                        .mapNotNull {
                            when (it) {
                                Manifest.permission.INTERNET -> "インターネットへのフルアクセス"
                                Manifest.permission.ACCESS_FINE_LOCATION -> "位置情報"
                                Manifest.permission.ACCESS_COARSE_LOCATION -> "位置情報"
                                //Manifest.permission.ACCESS_BACKGROUND_LOCATION -> "位置情報"
                                else -> null
                            }
                        }
                        .distinct()
                        .joinToString(separator = "\n", prefix = "\n", postfix = "\n") {
                            String.format("- %s", it)
                        }
                )
                AlertDialog.Builder(activity)
                    .setTitle("このアプリの権限について")
                    .setMessage(messageText)
                    .setPositiveButton("次へ") { _, _ -> onContinued() }
                    .setNegativeButton(android.R.string.cancel) { _, _ -> }
                    .create()
                    .show()
            }

            override fun onRequested(
                activity: Activity,
                deniedPermissions: Array<String>,
                deniedPermanentlyPermissions: Array<String>
            ) {
            }
        }

    init {
        View.inflate(context, R.layout.weather_forecast_setting_view, this)
        applyIsWeatherReportEnabled(weatherForecastSettingViewIsWeatherForecastEnabled.isChecked)
        applyLocationRequestSetting(weatherForecastSettingViewPowerSaving.isChecked)
        weatherForecastSettingViewIsWeatherForecastEnabled.setOnCheckedChangeListener { _, isChecked ->
            applyIsWeatherReportEnabled(isChecked)
            updateView()
            raiseOnSettingChanged()
        }
        weatherForecastSettingViewPowerSaving.setOnCheckedChangeListener { _, isChecked ->
            applyLocationRequestSetting(isChecked)
            updateView()
            raiseOnSettingChanged()
        }

        weatherForecastSettingViewPermissions.setOnClickListener {
            if (context is Activity) {
                weatherForecastSettingViewPermissions.visibility = View.GONE
                weatherForecastSettingViewAfterPermissions.visibility = View.VISIBLE
                permissionManager.requestPermission(context)
            }
        }

        weatherForecastSettingViewAfterPermissions.setOnClickListener {
            updateView()
        }

        weatherForecastSettingViewLocationSetting.setOnClickListener {
            if (context is Activity) {
                weatherForecastSettingViewLocationSetting.visibility = View.GONE
                weatherForecastSettingViewAfterLocationSetting.visibility = View.VISIBLE
                val messageText =
                    "このアプリでは現在位置の気象情報を表示する機能を使用することができます。\n" +
                            "そのためには位置情報の機能が使用できる必要があります。\n%s\n" +
                            "次に表示される画面で「位置情報の使用」をONにしてください。\n" +
                            "また、「モード」の項目がある場合は「高精度」を選択することをお勧めします。\n" +
                            "なお、操作方法は機種によって異なる場合があります。"
                AlertDialog.Builder(context)
                    .setTitle("位置情報の設定について")
                    .setMessage(messageText)
                    .setPositiveButton("次へ") { _, _ ->
                        // 更に位置情報の設定をチェックする
                        if (permissionManager.isAllowedMinimumPermission(context)) {

                            LocationWatcher.checkLocationRequestSetting(
                                context,
                                locationRequestSetting,
                                { },
                                { ex ->
                                    if (ex is ResolvableApiException) {
                                        try {
                                            // startResolutionForResult で表示される画面でやってくれるのは、位置情報機能のそのもののの活性化で、
                                            // 活性化できたかどうかは復帰値からは判断できない(RESULT_OKは成功ではなく別の意味を持っているため)
                                            // しかも、bluetoothやらWifiやらを位置情報のソースととして使う設定はいじってくれないので、別途何とかする必要がある
                                            ex.startResolutionForResult(
                                                context,
                                                REQUEST_CODE_LOCATION_REQUEST_SETTING
                                            )
                                            // 表示した resolution の結果をチェックするなら元アクティビティの onActivityResult をオーバーライドしなければならないが、
                                            // とくに結果は調べなくてもいいので、実装しない
                                        } catch (e: IntentSender.SendIntentException) {
                                            // NOP
                                        }
                                    } else {
                                        if (Log.isLoggable(TAG, Log.ERROR)) {
                                            Log.e(TAG, ex.message, ex)
                                        }
                                    }
                                }
                            )
                        }
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ -> }
                    .create()
                    .show()
            }
        }

        weatherForecastSettingViewAfterLocationSetting.setOnClickListener {
            updateView()
        }

        weatherForecastSettingViewLocationOptionalSetting.setOnClickListener {
            if (context is Activity) {
                val messageText =
                    "このアプリでは現在位置の気象情報を表示する機能を使用することができます。\n" +
                            "通常は位置情報を取得する機器としてGPSが利用できますが、電力消費が比較的大きいことや屋内などで利用できないことが多いなどの欠点もあるため、Wi-FiやBluetooth、モバイルネットワークなどとの併用をお勧めします。\n" +
                            "「続ける」を押して表示される画面で、位置情報を取得するために利用する機器の設定を行ってください。\n" +
                            "なお、操作方法は機種によって異なる場合があります。\n" +
                            "例) 「モード」の項目をタッチする、「Wi-FiのスキャンとBluetoothのスキャン」の項目をタッチする、など"
                AlertDialog.Builder(context)
                    .setTitle("バッテリーを節約するために")
                    .setMessage(messageText)
                    .setPositiveButton("次へ") { _, _ ->
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ -> }
                    .create()
                    .show()
            }
        }
    }

    var isWeatherReportEnabled: Boolean
        get() =
            weatherForecastSettingViewIsWeatherForecastEnabled.isChecked
        set(value) {
            weatherForecastSettingViewIsWeatherForecastEnabled.isChecked = value
        }

    var locationRequestSetting: LocationRequestSetting
        get() =
            if (weatherForecastSettingViewPowerSaving.isChecked)
                LocationRequestSetting.POWER_SAVING_MODE
            else
                LocationRequestSetting.NORMAL_MODE
        set(value) {
            weatherForecastSettingViewPowerSaving.isChecked =
                (value == LocationRequestSetting.POWER_SAVING_MODE)
        }

    fun check(onCompleted: (Boolean, LocationRequestSetting) -> Unit) {
        if (permissionManager.isAllowedMinimumPermission(context)) {
            LocationWatcher.checkLocationRequestSetting(
                context,
                locationRequestSetting,
                {
                    onCompleted(true, locationRequestSetting)
                },
                { ex ->
                    if (Log.isLoggable(TAG, Log.ERROR)) {
                        Log.e(TAG, ex.message, ex)
                    }
                    onCompleted(false, locationRequestSetting)
                }
            )
        } else {
            onCompleted(false, locationRequestSetting)
        }
    }

    fun updateView() {
        weatherForecastSettingViewAfterPermissions.visibility = View.GONE
        weatherForecastSettingViewAfterLocationSetting.visibility = View.GONE
        if (isWeatherReportEnabled) {
            // 天気予報をする場合

            if (permissionManager.isAllowedAllermission(context)) {
                // パーミッションがすべて許可されている場合

                weatherForecastSettingViewPermissions.visibility = View.GONE

                // LocationWatcher.checkLocationRequestSetting の処理は非同期に進むため、各項目をあらかじめリセットしておく
                weatherForecastSettingViewLocationSetting.visibility = View.GONE
                weatherForecastSettingViewLocationOptionalSetting.visibility = View.GONE
                weatherForecastSettingViewError.visibility = View.GONE

                // 位置情報取得のパラメタを検査する
                LocationWatcher.checkLocationRequestSetting(
                    context,
                    locationRequestSetting,
                    {
                        // 位置情報の設定に問題がない場合
                        weatherForecastSettingViewLocationSetting.visibility = View.GONE
                        weatherForecastSettingViewLocationOptionalSetting.visibility = View.VISIBLE
                        weatherForecastSettingViewError.visibility = View.GONE
                    },
                    {
                        // 位置情報の設定に問題がある場合
                        weatherForecastSettingViewLocationSetting.visibility = View.VISIBLE
                        weatherForecastSettingViewLocationOptionalSetting.visibility = View.GONE
                        weatherForecastSettingViewError.text = "位置情報が利用できないため、端末の現在位置を知ることができません。"
                        weatherForecastSettingViewError.visibility = View.VISIBLE
                    }
                )
            } else {
                // 許可されていないパーミッションがある場合
                weatherForecastSettingViewPermissions.visibility = View.VISIBLE
                weatherForecastSettingViewLocationSetting.visibility = View.GONE
                weatherForecastSettingViewLocationOptionalSetting.visibility = View.GONE
                weatherForecastSettingViewError.text = "許可されていない権限があるため、天気予報を表示する機能が使用できません。"
                weatherForecastSettingViewError.visibility = View.VISIBLE
            }

        } else {
            // 天気予報をしない場合
            weatherForecastSettingViewPermissions.visibility = View.GONE
            weatherForecastSettingViewError.visibility = View.GONE
        }
    }

    fun setOnSettingsChangedListener(listener: OnSettingsChangedListener?) {
        onSettingsChangedListener = listener
    }

    fun setOnSettingsChangedListener(listener: ((WeatherForecastSettingView) -> Unit)?) {
        onSettingsChangedListener =
            if (listener == null)
                null
            else
                object : OnSettingsChangedListener {
                    override fun onSettingsChanged(view: WeatherForecastSettingView) {
                        listener(view)
                    }
                }
    }

    private fun applyIsWeatherReportEnabled(isChecked: Boolean) {
        if (isChecked) {
            weatherForecastSettingViewMain.visibility = View.VISIBLE
        } else {
            weatherForecastSettingViewMain.visibility = View.GONE
        }
    }

    private fun applyLocationRequestSetting(isChecked: Boolean) {
        if (isChecked) {
            weatherForecastSettingViewPowerSavingDescription.text =
                "省電力モードがオンになっています。\n天気予報の更新間隔は約60分です。\n位置情報の精度は最悪で約10km程度になる可能性があるため、場合によってはやや精度不足に感じるかもしれません。"
        } else {
            weatherForecastSettingViewPowerSavingDescription.text =
                "省電力モードがオフになっています。\n天気予報の更新間隔は約10分です。\n位置情報は天気予報のためには十分な精度が期待できます。"
        }
    }

    private fun raiseOnSettingChanged() {
        onSettingsChangedListener?.onSettingsChanged(this)
    }

    companion object {
        private const val TAG = "WeatherForecastSetting"
        private const val REQUEST_CODE_LOCATION_REQUEST_SETTING = 1000
    }
}


