package com.palmtreesoftware.experimentandroid5_1

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.location.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


class MainActivity : AppCompatActivity() {
    //TODO("MODE_CHANGED_ACTION の受信時に、ロケーションサービスの再起動")

    private val TAG = "MainActivity"

    private class WeatherInfo(val iconUrl: Uri, var iconImage: Bitmap?, val weatherName: String)

    private val scope = CoroutineScope(Dispatchers.Default)
    private val handler: Handler = Handler()

    private val locationManager: LocationManager by lazy { getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    private var currentLatitude: Double = Double.NaN
    private var currentLongitude: Double = Double.NaN
    private val locationListener by lazy {
        object : LocationListener {
            override fun onLocationChanged(location: Location?) {
                if (location != null) {
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                    AsyncUtility.getAddressFromLocation(
                        this@MainActivity,
                        scope,
                        currentLatitude,
                        currentLongitude, { address ->
                            if (address != null)
                                textview_city.text = address.locality
                        }, { ex ->
                            if (Log.isLoggable(TAG, Log.ERROR)) {
                                Log.e(TAG, ex.message, ex)
                            }
                        }
                    )
                    requestCurrentWeatherInfo(currentLatitude, currentLongitude)
                }
            }

            override fun onProviderDisabled(provider: String?) {
            }

            override fun onProviderEnabled(provider: String?) {
            }

            @Suppress("DEPRECATION")
            override fun onStatusChanged(
                provider: String?,
                status: Int,
                extras: Bundle?
            ) {
                when (status) {
                    LocationProvider.AVAILABLE -> {
                    }
                    LocationProvider.OUT_OF_SERVICE -> {
                    }
                    LocationProvider.TEMPORARILY_UNAVAILABLE -> {
                    }
                    else -> {
                    }
                }
            }
        }
    }
    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (context != null && intent != null) {
                when (intent.action) {
                    LocationManager.PROVIDERS_CHANGED_ACTION -> {
                        stopLocationService()
                        startLocationService()
                    }
                    else -> {
                    }
                }
            }
        }
    }

    private var imageUpdateRunnable: Runnable = Runnable {}
    private val updatingWeatherIconIntervalMilliSeconds: Long = 1000 * 2
    private val weatherInfos: MutableList<WeatherInfo> = mutableListOf()
    private var currentWeatherViewIndex = 0

    private var weatherPollingRunnable: Runnable = Runnable {}
    private val minimumWeatherPollingIntervalMilliSeconds: Long = 1000 * 60 * 10
    private val maximumWeatherPollingIntervalMilliSeconds: Long = 1000 * 60 * 60

    //TODO("イベントとなるのは以下の通り")
    //TODO("1) 位置情報の変化")
    //TODO("2) 気象アイコンのアップデート時期") => OK これは比較的独立して考えれるので考慮不要
    //TODO("3) 気象情報の再取得時期(最小) 気象情報の以前の取得からこの時間が経過しないうちは再取得を行ってはならない")
    //TODO("4) 気象情報の再取得時期(最大) この時間が経過したら必ず気象情報を再取得")
    //TODO("※キャッシュされた気象情報はウィジェットのすべてのインスタンスから共用されることを忘れないこと")


    val permissionManager: PermissionManager by lazy { PermissionManager.createInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        test_result.text = Platform.callTestCode() ?: "テストコードはありません"

        ok_button.setOnClickListener {
            finish()
        }

        cancel_button.setOnClickListener {
            finish()
        }

        Platform.sdK23Depended({
            // NOP
        }, {
            request_permission_button.visibility = View.GONE
        })

        request_permission_button.setOnClickListener {
            Platform.sdK23Depended(
                @RequiresApi(Build.VERSION_CODES.M) {
                    permissionManager.requestPermission(this)
                }, {
                    // NOP
                })
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationService()
    }

    override fun onPause() {
        handler.removeCallbacks(imageUpdateRunnable)
        stopLocationService()
        super.onPause()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (!permissionManager.onRequestPermissionsResult(
                this,
                requestCode,
                permissions,
                grantResults
            )
        )
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun startLocationService() {
        if (!PermissionManager.checkMinimumPermission(this))
            return
        registerReceiver(locationReceiver, IntentFilter(LocationManager.MODE_CHANGED_ACTION))
        arrayOf(
            Pair(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Criteria.ACCURACY_FINE
            ), Pair(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Criteria.ACCURACY_COARSE
            )
        )
            .mapNotNull {
                if (!PermissionManager.checkPermission(this, it.first))
                    null
                else {
                    val criteria = Criteria()
                    criteria.accuracy = it.second
                    criteria.powerRequirement = Criteria.POWER_LOW
                    criteria.isCostAllowed = false
                    locationManager.getBestProvider(criteria, true)
                }
            }.distinct().forEach {
                try {
                    locationManager.requestLocationUpdates(
                        it,
                        5000,
                        10.toFloat(),
                        locationListener
                    )
                } catch (ex: Exception) {
                    if (Log.isLoggable(TAG, Log.ERROR)) {
                        Log.e(TAG, ex.message, ex)
                    }
                    throw ex
                }
            }
    }

    private fun stopLocationService() {
        handler.removeCallbacks(imageUpdateRunnable)
        handler.removeCallbacks(weatherPollingRunnable)
        locationManager.removeUpdates(locationListener)
        unregisterReceiver(locationReceiver)
    }

    private fun requestCurrentWeatherInfo(latitude: Double, longitude: Double) {
        OpenWeatherMap.CurrentWeatherData.getInstance(
            this,
            scope,
            java.util.Locale.getDefault(),
            latitude,
            longitude, { current, _ ->
                updateWeatherView(current)
                handler.removeCallbacks(imageUpdateRunnable)
                if (weatherInfos.isEmpty()) {
                    weathwe_icon_image.setImageBitmap(null)
                    weathwe_name.text = ""
                } else {
                    requestToUpdateIconImage()
                }
                resetOpenWeatherPollingTimer(
                    if (current.isCached)
                        minimumWeatherPollingIntervalMilliSeconds
                    else
                        maximumWeatherPollingIntervalMilliSeconds
                )
            }, { ex ->
                if (Log.isLoggable(TAG, Log.ERROR)) {
                    Log.e(TAG, ex.message, ex)
                }
            }
        )
    }

    private fun updateWeatherView(current: OpenWeatherMap.CurrentWeatherData) {
        textview_datetime_observation.text =
            current.lastUpdated.atZone(TimeZone.getDefault()).format("HH:mm:ss")
        AsyncUtility.getAddressFromLocation(
            this,
            scope,
            current.coord.latitude,
            current.coord.longitude, { address ->
                if (address != null) {
                    textview_address_observation.text =
                        with(address) {
                            (0..maxAddressLineIndex).map { getAddressLine(it) }
                        }.joinToString("\n")
                }
            }, { ex ->
                if (Log.isLoggable(TAG, Log.ERROR)) {
                    Log.e(TAG, ex.message, ex)
                }
            }
        )
        current.sys.let {
            if (it.sunrise >= it.sunset) {
                textview_sunrise_or_sunset_label_1.text = "日の入り"
                textview_sunrise_or_sunset_1.text =
                    it.sunset.atZone(TimeZone.getDefault()).format("HH:mm:ss")
                textview_sunrise_or_sunset_label_2.text = "日の出"
                textview_sunrise_or_sunset_2.text =
                    it.sunrise.atZone(TimeZone.getDefault()).format("HH:mm:ss")
            } else {
                textview_sunrise_or_sunset_label_1.text = "日の出"
                textview_sunrise_or_sunset_1.text =
                    it.sunrise.atZone(TimeZone.getDefault()).format("HH:mm:ss")
                textview_sunrise_or_sunset_label_2.text = "日の入り"
                textview_sunrise_or_sunset_2.text =
                    it.sunset.atZone(TimeZone.getDefault()).format("HH:mm:ss")
            }
        }
        weatherInfos.clear()
        current.weathers.map { WeatherInfo(it.iconUrl, null, it.description) }
            .forEach {
                weatherInfos.add(it)
            }
        currentWeatherViewIndex = 0
        weatherInfos.forEach {
            AsyncUtility.downloadImage(
                scope,
                it.iconUrl,
                { bitmap ->
                    it.iconImage = bitmap
                }, { ex ->
                    if (Log.isLoggable(TAG, Log.ERROR)) {
                        Log.e(TAG, ex.message, ex)
                    }
                }
            )
        }
    }

    private fun requestToUpdateIconImage() {
        handler.removeCallbacks(imageUpdateRunnable)
        imageUpdateRunnable = Runnable {
            handler.removeCallbacks(imageUpdateRunnable)
            if (weatherInfos.isEmpty()) {
                // NOP
            } else if (weatherInfos.count() == 1) {
                val weatherInfo = weatherInfos[0]
                if (weatherInfo.iconImage != null)
                    weathwe_icon_image.setImageBitmap(weatherInfo.iconImage)
                weathwe_name.text = weatherInfo.weatherName
                if (weatherInfo.iconImage == null) {
                    handler.postDelayed(
                        imageUpdateRunnable,
                        updatingWeatherIconIntervalMilliSeconds
                    )
                }
            } else {
                if (currentWeatherViewIndex >= weatherInfos.count())
                    currentWeatherViewIndex = 0
                val weatherInfo = weatherInfos[currentWeatherViewIndex]
                if (weatherInfo.iconImage != null)
                    weathwe_icon_image.setImageBitmap(weatherInfo.iconImage)
                weathwe_name.text = weatherInfo.weatherName
                ++currentWeatherViewIndex
                handler.postDelayed(
                    imageUpdateRunnable,
                    updatingWeatherIconIntervalMilliSeconds
                )
            }
        }
        handler.post(imageUpdateRunnable)
    }

    private fun resetOpenWeatherPollingTimer(interval: Long) {
        handler.removeCallbacks(weatherPollingRunnable)
        weatherPollingRunnable = Runnable {
            handler.removeCallbacks(weatherPollingRunnable)
            Pair(currentLatitude, currentLongitude).let {
                if (!it.first.isNaN() && !it.second.isNaN()) {
                    requestCurrentWeatherInfo(it.first, it.second)
                }
            }
        }
        handler.postDelayed(weatherPollingRunnable, interval)
    }
}