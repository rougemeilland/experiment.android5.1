package com.palmtreesoftware.experimentandroid5_1

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_test.*


class TestActivity : AppCompatActivity() {

    private class SelectionItem(val index: Int, val value: String, val text: String)

    private val timeZoneFormAccesser: TimeZoneForm = object : TimeZoneForm {
        override val isDefault: Boolean
            get() =
                (testActivityTimeZoneId.selectedItem as SelectionItem).value == "-"

        override fun getSymbol(): String? {
            return (testActivityTimeZoneId.selectedItem as SelectionItem).let {
                if (it.value == "-") null
                else if (it.value == "#") null
                else it.value
            }
        }

        override fun getTimeDifference(): Triple<Int, Int, Int>? {
            return testActivityHour.value.let { hour ->
                // 0 -> -12
                // 1 -> -11
                // 12 -> -00
                // 13 -> +00
                // 14 -> +01
                // 27 -> +14
                if (hour >= 13)
                    Triple(hour - 13, testActivityMinute.value, 0)
                else
                    Triple(hour - 12, -testActivityMinute.value, -0)
            }
        }

        override fun reset() {
            setDefault()
        }

        override fun set(timeZoneId: String) {
            getTimeZoneIndex(timeZoneId).let {
                if (it == null)
                    setDefault()
                else
                    testActivityTimeZoneId.setSelection(it)
            }
        }

        override fun set(hours: Int, minutes: Int) {
            getTimeZoneIndex("#").let {
                if (it == null)
                    setDefault()
                else {
                    testActivityTimeZoneId.setSelection(it)
                    if (hours >= 0 && minutes >= 0) {
                        testActivityHour.value = hours + 13
                        testActivityMinute.value = minutes
                    } else if (hours <= 0 && minutes <= 0) {
                        testActivityHour.value = hours + 12
                        testActivityMinute.value = -minutes
                    } else
                        throw Exception("")
                }
            }
        }

        private fun setDefault() {
            testActivityTimeZoneId.setSelection(getTimeZoneIndex("-") ?: 0)
            testActivityHour.value = 13
            testActivityMinute.value = 0
        }

        private fun getTimeZoneIndex(timeZoneId: String): Int? =
            selectionItems
                .firstOrNull { it.value == timeZoneId }
                .let { it?.index }
    }

    private val selectionItems: ArrayList<SelectionItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        selectionItems.add(
            SelectionItem(
                0,
                "-",
                "システムデフォルト"
            )
        )
        selectionItems.add(
            SelectionItem(
                1,
                "GMT",
                getString(R.string.time_zone_short_name_GMT)
            )
        )
        selectionItems.add(
            SelectionItem(
                2,
                "Asia/Tokyo",
                getString(R.string.time_zone_short_name_JST)
            )
        )
        selectionItems.add(
            SelectionItem(
                3,
                "America/New_York",
                getString(R.string.time_zone_short_name_EST)
            )
        )
        selectionItems.add(
            SelectionItem(
                4,
                "America/Los_Angeles",
                getString(R.string.time_zone_short_name_PST)
            )
        )
        selectionItems.add(
            SelectionItem(
                5,
                "Europe/Berlin",
                getString(R.string.time_zone_short_name_CET)
            )
        )
        selectionItems.add(
            SelectionItem(
                6,
                "Europe/Lisbon",
                getString(R.string.time_zone_short_name_WET)
            )
        )
        selectionItems.add(
            SelectionItem(
                7,
                "#",
                "時差を入力"
            )
        )

        testActivityTimeZoneId.adapter = object : ArrayAdapter<SelectionItem>(
            this,
            android.R.layout.simple_spinner_item,
            selectionItems
        ) {
            init {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return (super.getView(position, convertView, parent) as TextView).also {
                    it.text = getItem(position)?.text
                }
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return (super.getDropDownView(position, convertView, parent) as TextView).also {
                    it.text = getItem(position)?.text
                }
            }
        }
        testActivityTimeZoneId.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (parent!!.id) {
                    R.id.testActivityTimeZoneId -> {
                        val selectedItem = parent.selectedItem as SelectionItem
                        if (selectedItem.value == "#") {
                            testActivityTimeDifference.visibility = View.VISIBLE
                            testActivityHour.requestFocus()
                        } else {
                            testActivityTimeDifference.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        testActivityHour.displayedValues = (-13..14).map {
            if (it < -1) "%+03d".format(it + 1)
            else if (it == -1) "-00"
            else "%+03d".format(it)
        }.toTypedArray()
        testActivityHour.minValue = 0
        testActivityHour.maxValue = testActivityHour.displayedValues.size - 1
        testActivityHour.value = 14
        testActivityMinute.displayedValues = (0..59).map { "%02d".format(it) }.toTypedArray()
        testActivityMinute.minValue = 0
        testActivityMinute.maxValue = testActivityMinute.displayedValues.size - 1
        testActivityMinute.value = 0

        testActivityOk.setOnClickListener { view ->
            setResult(Activity.RESULT_OK)
            finishAndRemoveTask()
        }
        testActivityCancel.setOnClickListener { view ->
            setResult(Activity.RESULT_CANCELED)
            finishAndRemoveTask()
        }
        testActivityRun.setOnClickListener { view ->
            testActivityResult.text = TimeZone.of(timeZoneFormAccesser).id
        }

        timeZoneFormAccesser.reset()
    }

    fun setTimeZone(timeZone: TimeZone) {
        timeZone.setToForm(timeZoneFormAccesser)
    }
}
