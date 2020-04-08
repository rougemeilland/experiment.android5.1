package com.palmtreesoftware.experimentandroid51

import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val locationService: LocationService by lazy {
        object : LocationService(this) {
            override fun onServiceStatusChanged(isRunning: Boolean) {
                if (!isRunning) {
                    textview_latitude.text = "????????"
                    textview_longitude.text = "????????"
                }
            }

            override fun onLocationChanged(latitude: Double, longitude: Double) {
                textview_latitude.text = latitude.toString()
                textview_longitude.text = longitude.toString()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ok_button.setOnClickListener {
            finish()
        }

        cancel_button.setOnClickListener {
            finish()
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            request_permission_button.visibility = View.GONE
        }

        request_permission_button.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                locationService.requestPermission(this)
            }
        }
        getString(R.string.apikey)
    }

    override fun onResume() {
        super.onResume()
        locationService.start()
    }

    override fun onPause() {
        locationService.stop()
        super.onPause()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (!locationService.onRequestPermissionsResult(this, requestCode, permissions, grantResults))
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}