package com.palmtreesoftware.experimentandroid51

import android.os.Build
import android.text.format.DateFormat
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.*

class Experiment1 {
    companion object {

        private fun いろいろな形式のタイムゾーンIDの名前がどのように表示されるか() {
            val dateTimeFormat = "yyyy年MM月dd日(E) HH:mm:ss"
            arrayOf(
                "Asia/Tokyo",
                "GMT+09:00",
                "Europe/Berlin",
                "GMT",
                "Universal",
                "Europe/London",
                "Europe/Lisbon",
                "America/New_York",
                "America/Los_Angeles",
                "UTC",
                "UTC+09:00"
            ).forEach { timeZoneId ->
                val dateTime =
                    Platform.sdK26Depended(
                        @RequiresApi(Build.VERSION_CODES.O) {
                            java.time.ZoneId.of(timeZoneId).let {
                                Triple(
                                    it.id,
                                    java.time.LocalDateTime.now(it)
                                        .format(
                                            java.time.format.DateTimeFormatter.ofPattern(
                                                dateTimeFormat
                                            )
                                        ),
                                    it.getDisplayName(
                                        java.time.format.TextStyle.FULL, Locale.ENGLISH
                                    )
                                )
                            }
                        }, {
                            java.util.TimeZone.getTimeZone(timeZoneId).let {
                                Triple(
                                    it.id,
                                    DateFormat.format(dateTimeFormat, Calendar.getInstance(it))
                                        .toString(),
                                    it.getDisplayName(
                                        Locale.ENGLISH
                                    )
                                )
                            }
                        })
                Log.d(
                    "Experiment",
                    dateTime.second + ": " + dateTime.first + "(" + dateTime.third + ")"
                )
            }
        }

        private fun タイムゾーンIDが正しいことをどのように確認できるか() {
            arrayOf(
                "JST",
                "GMT",
                "UTC",
                "EST",
                "PST",
                "CET",
                "WET",
                "Asia/Tokyo",
                "GMT+09:00",
                "UTC+09:00",
                "Asia/Toky",
                "AAA",
                "+",
                "-",
                ":"
            ).map { timeZoneId ->
                (Platform.sdK26Depended(
                    @RequiresApi(Build.VERSION_CODES.O) {
                        val zoneId = try {
                            java.time.ZoneId.of(timeZoneId)
                        } catch (ex: Exception) {
                            java.time.ZoneId.systemDefault()
                        }
                        val now = java.time.LocalDateTime.now(zoneId)
                        Triple(timeZoneId, zoneId.id, now.toString())
                    }, {
                        val zoneId = java.util.TimeZone.getTimeZone(timeZoneId)
                        val now = Calendar.getInstance(zoneId)
                        Triple(timeZoneId, zoneId.id, now)
                    })).let {
                    Log.d(
                        "Experiment",
                        (if (it.first == it.second) "OK" else "NG") + ": original-id=" + it.first + ", actual-id=" + it.second
                    )
                }

            }
        }

        private fun 時差を表示するためにどのような書式が使用できるか() {
            Log.d("Experiment", "%+03d".format(12, 0))
            Log.d("Experiment", "%+03d".format(2, 0))
            Log.d("Experiment", "%+03d".format(0, 0))
            Log.d("Experiment", "%+03d".format(-2, 0))
            Log.d("Experiment", "%+03d".format(-12, 0))
        }


    }
}