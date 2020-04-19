package com.palmtreesoftware.experimentandroid5_1

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_test.*
import java.util.*

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        testActivityOk.setOnClickListener { _ ->
            setResult(Activity.RESULT_OK)
            finishAndRemoveTask()
        }
        testActivityCancel.setOnClickListener { _ ->
            setResult(Activity.RESULT_CANCELED)
            finishAndRemoveTask()
        }
        testActivityRun.setOnClickListener { _ ->
            testActivityResult.text = testActivityTimeZone.value.id
            textActivityCurrentWeather.update(
                37.485125,
                139.911997,
                Locale.getDefault(),
                { _, _ ->

                })
        }
    }

    fun setTimeZone(timeZone: TimeZone) {
        testActivityTimeZone.value = timeZone
    }
}
