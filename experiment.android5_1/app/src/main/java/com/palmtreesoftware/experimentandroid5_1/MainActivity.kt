package com.palmtreesoftware.experimentandroid5_1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private class ViewSettings {
        fun save(activity: MainActivity) {
            activity.getSharedPreferences(activity.packageName, 0)
                .edit()
                .apply {
                    putBoolean(
                        "isWeatherReportEnabled",
                        activity.mainActivityWeatherForecastSettingSetting.isWeatherReportEnabled
                    )
                    putBoolean(
                        "isPowerSavingMode",
                        activity.mainActivityWeatherForecastSettingSetting.locationRequestSetting == LocationRequestSetting.POWER_SAVING_MODE
                    )
                    apply()
                }
        }

        fun load(activity: MainActivity) {
            activity.getSharedPreferences(activity.packageName, 0)
                .apply {
                    activity.mainActivityWeatherForecastSettingSetting.isWeatherReportEnabled =
                        getBoolean("isWeatherReportEnabled", false)
                    activity.mainActivityWeatherForecastSettingSetting.locationRequestSetting =
                        if (getBoolean("isPowerSavingMode", false))
                            LocationRequestSetting.POWER_SAVING_MODE
                        else
                            LocationRequestSetting.NORMAL_MODE
                }
        }
    }

    private val handler = Handler()
    private val viewSetting = ViewSettings()

    private val locationWatcher by lazy {
        object : LocationWatcher(this@MainActivity) {
            override fun onLocationChanged(locationWatcherResult: LocationWatcherResult) {
                mainActivityCurrentWeather
                    .update(
                        locationWatcherResult.coordinates,
                        locationWatcherResult.accuracy,
                        java.util.Locale.getDefault()
                    ) { _, _ -> }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewSetting.load(this)
        mainActivityCurrentWeather.visibility =
            if (mainActivityWeatherForecastSettingSetting.isWeatherReportEnabled) {
                View.VISIBLE
            } else {
                View.GONE
            }

        mainActivityWeatherForecastSettingSetting.setOnSettingsChangedListener { view ->
            if (view.isWeatherReportEnabled) {
                mainActivityCurrentWeather.visibility = View.VISIBLE
                startLocationWatcher()
            } else {
                mainActivityCurrentWeather.visibility = View.GONE
                stopLocationWatcher()
            }
            viewSetting.save(this)
        }

        mainActivityOk.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finishAndRemoveTask()
        }
        mainActivityCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finishAndRemoveTask()
        }
        mainActivityRun.setOnClickListener {
            mainActivityResult.text = mainActivityTimeZone.value.id
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (Dispatchers.IO + job).cancel()
        (Dispatchers.Default + job).cancel()
    }

    override fun onPause() {
        stopLocationWatcher()
        mainActivityWeatherForecastSettingSetting.updateView()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mainActivityWeatherForecastSettingSetting.updateView()
        startLocationWatcher()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        handler.post {
            mainActivityWeatherForecastSettingSetting.updateView()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        handler.post {
            mainActivityWeatherForecastSettingSetting.updateView()
        }
    }

    private fun stopLocationWatcher() {
        locationWatcher.stop()
    }

    private fun startLocationWatcher() {
        mainActivityWeatherForecastSettingSetting.check { isEnabled, locationResultSetting ->
            if (isEnabled) {
                locationWatcher.start(
                    locationResultSetting
                ) { ex ->
                    if (Log.isLoggable(TAG, Log.ERROR)) {
                        Log.e(TAG, ex.message, ex)
                    }
                }
            } else {
                mainActivityCurrentWeather.reset()
            }
        }
    }

    companion object {
        private const val TAG = "TestActivity"
    }
}
