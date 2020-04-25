package com.palmtreesoftware.experimentandroid5_1

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.time_zone_selecter_view.view.*

class TimeZoneSelecterView(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    private class SelectionItem(val index: Int, val value: String, val text: String)

    private val selectionItemPattern = Regex("^([^!]+)!([^!]+)$")

    private val selectionItems: Array<SelectionItem> =
        context.resources.getStringArray(R.array.time_zone_selecter_view_selection_items)
            .mapIndexedNotNull { index, sourceText ->
                selectionItemPattern.matchEntire(sourceText)?.let { match ->
                    SelectionItem(
                        index,
                        match.destructured.component1(),
                        match.destructured.component2()
                    )
                }
            }.toTypedArray()

    private val formAccesser: TimeZoneForm = object : TimeZoneForm {
        // android の NumberPicker は負数は扱えない模様
        // ※ minValue に負の数を入れてみたら、例外 (java.lang.IllegalArgumentException: minValue must be >= 0) が発生した
        // この問題を回避するため、以下の対策を行った
        // 1) NumberPicker に与える value, minValue, maxValue にゲタをはかせる
        // 2) 時の数値をタイムゾーンIDに変換するときはゲタを元に戻す。逆にタイムゾーンIDから時を取得するときは再びゲタをはかせる

        override val isDefault: Boolean
            get() =
                (timeZoneSelecterViewTimeZoneId.selectedItem as SelectionItem).value == "-"

        override fun getSymbol(): String? =
            (timeZoneSelecterViewTimeZoneId.selectedItem as SelectionItem).run {
                when (value) {
                    "-" -> null
                    "#" -> null
                    else -> value
                }
            }

        override fun getTimeDifference(): Triple<Int, Int, Int>? =
            timeZoneSelecterViewHour.value.let { hour ->
                if (hour >= 13)
                    Triple(hour - 13, timeZoneSelecterViewMinute.value, 0)
                else
                    Triple(hour - 12, -timeZoneSelecterViewMinute.value, -0)
            }

        override fun reset() {
            setDefault()
        }

        override fun set(timeZoneId: String) {
            getTimeZoneIndex(timeZoneId).let {
                if (it == null)
                    setDefault()
                else
                    timeZoneSelecterViewTimeZoneId.setSelection(it)
            }
        }

        override fun set(hours: Int, minutes: Int) {
            getTimeZoneIndex("#").let {
                if (it == null)
                    setDefault()
                else {
                    timeZoneSelecterViewTimeZoneId.setSelection(it)
                    if (hours >= 0 && minutes >= 0) {
                        timeZoneSelecterViewHour.value = hours + 13
                        timeZoneSelecterViewMinute.value = minutes
                    } else if (hours <= 0 && minutes <= 0) {
                        timeZoneSelecterViewHour.value = hours + 12
                        timeZoneSelecterViewMinute.value = -minutes
                    } else
                        throw Exception("")
                }
            }
        }

        private fun setDefault() {
            timeZoneSelecterViewTimeZoneId.setSelection(getTimeZoneIndex("-") ?: 0)
            timeZoneSelecterViewHour.value = 13
            timeZoneSelecterViewMinute.value = 0
        }

        private fun getTimeZoneIndex(timeZoneId: String): Int? =
            selectionItems
                .firstOrNull { it.value == timeZoneId }
                .let { it?.index }
    }

    init {
        View.inflate(context, R.layout.time_zone_selecter_view, this)

        timeZoneSelecterViewTimeZoneId.adapter = object : ArrayAdapter<SelectionItem>(
            context,
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
            ): View =
                (super.getView(position, convertView, parent) as TextView).also {
                    it.text = getItem(position)?.text
                }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View =
                (super.getDropDownView(position, convertView, parent) as TextView).also {
                    it.text = getItem(position)?.text
                }
        }

        timeZoneSelecterViewTimeZoneId.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (parent!!.id) {
                    R.id.timeZoneSelecterViewTimeZoneId -> {
                        val selectedItem = parent.selectedItem as SelectionItem
                        if (selectedItem.value == "#") {
                            timeZoneSelecterViewTimeDifference.visibility = View.VISIBLE
                            timeZoneSelecterViewHour.requestFocus()
                        } else {
                            timeZoneSelecterViewTimeDifference.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        timeZoneSelecterViewHour.apply {
            // NumberPickerの既知のバグで、初期状態で選択されている項目に formatter が正常に適用されていない。
            // NumberPicker に対して何らかの操作をすると正しく表示される。
            // formatter を使用せずに、フォーマット済みの表示文字列の配列を displayedValues に与えることによって回避
            displayedValues = (-13..14).map {
                when {
                    it < -1 -> "%+03d".format(it + 1)
                    it == -1 -> "-00"
                    else -> "%+03d".format(it)
                }
            }.toTypedArray()
            minValue = 0
            maxValue = displayedValues.size - 1
            value = 14
        }

        timeZoneSelecterViewMinute.apply {
            displayedValues = (0..59).map { "%02d".format(it) }.toTypedArray()
            minValue = 0
            maxValue = displayedValues.size - 1
            value = 0
        }

        formAccesser.reset()
    }

    var value: TimeZone
        get() =
            TimeZone.of(formAccesser)
        set(value) {
            value.setToForm(formAccesser)
        }
}
