package com.palmtreesoftware.experimentandroid5_1

import android.app.Activity
import android.location.Address
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class TestActivity : AppCompatActivity() {

    private class AddressSummary private constructor(
        val addressText: String,
        val countryName: String?,
        val postalCode: String?,
        val adminArea: String?,
        val subAdminArea: String?,
        val locality: String?,
        val subLocality: String?
    ) {
        fun getStyle(locales: Array<String>): String {
            if (countryName == null)
                throw Exception()
            if (postalCode == null)
                throw Exception()
            return if (
                addressText.startsWith(
                    arrayOf(adminArea, subAdminArea, locality, subLocality)
                        .filter { it != null }
                        .joinToString(separator = "", prefix = "$countryName、〒$postalCode ")
                )
            ) {
                "ja:<countryName>、〒<postalCode> <adminArea><subAdminArea>?<locality><subLocality>?***"
            } else if (
                addressText.endsWith(
                    arrayOf(subLocality, locality, subAdminArea, adminArea)
                        .filter { it != null }
                        .joinToString(", ", postfix = " $postalCode, $countryName")
                )

            ) {
                "en:***(, <subLocality>)?, <locality>(, <subAdminArea>)?, <adminArea> <postalCode>, <countryName>"
            } else if (
                addressText.endsWith(
                    arrayOf(subLocality, locality, subAdminArea, adminArea)
                        .filter { it != null }
                        .joinToString(", ", postfix = " $postalCode\u060C $countryName")
                )

            ) {
                "ar:***(, <subLocality>)?, <locality>(, <subAdminArea>)?, <adminArea> <postalCode>&#x60C; <countryName>"
            } else if (
                addressText.endsWith(
                    arrayOf(subLocality, locality, subAdminArea, adminArea)
                        .filter { it != null }
                        .joinToString(", ", postfix = " $postalCode$countryName")
                )

            ) {
                "zh:***(, <subLocality>)?, <locality>(, <subAdminArea>)?, <adminArea> <postalCode><countryName>"
            } else if (
                addressText.endsWith(
                    arrayOf(subLocality, locality, subAdminArea, adminArea)
                        .filter { it != null }
                        .joinToString(", ", postfix = " $postalCode $countryName")
                )

            ) {
                "ko:***(, <subLocality>)?, <locality>(, <subAdminArea>)?, <adminArea> <postalCode> <countryName>"
            } else {
                throw Exception()
            }
        }

        fun toJSONObject(): JSONObject {
            return JSONObject().apply {
                put("text", addressText)
                putOpt("countryName", countryName)
                putOpt("postalCode", postalCode)
                putOpt("adminArea", adminArea)
                putOpt("subAdminArea", subAdminArea)
                putOpt("locality", locality)
                putOpt("subLocality", subLocality)
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other)
                return true
            if (javaClass != other?.javaClass)
                return false
            other as AddressSummary
            if (addressText != other.addressText)
                return false
            if (countryName != other.countryName)
                return false
            if (postalCode != other.postalCode)
                return false
            if (adminArea != other.adminArea)
                return false
            if (subAdminArea != other.subAdminArea)
                return false
            if (locality != other.locality)
                return false
            if (subLocality != other.subLocality)
                return false
            return true
        }

        override fun hashCode(): Int {
            return (((((addressText.hashCode() * 31 + countryName.hashCode()) * 31 + postalCode.hashCode()) * 31 + adminArea.hashCode()) * 31 + subAdminArea.hashCode()) * 31 + locality.hashCode()) * subLocality.hashCode()
        }

        override fun toString(): String {
            return "AddressSummary(addressText='$addressText', countryName='$countryName', postalCode='$postalCode', adminArea='$adminArea', subAdminArea='$subAdminArea', locality='$locality', subLocality='$subLocality')"
        }

        companion object {
            fun of(address: Address): AddressSummary {
                return AddressSummary(
                    (0..address.maxAddressLineIndex)
                        .joinToString("\n")
                        { index ->
                            address.getAddressLine(index)
                        },
                    address.countryName,
                    address.postalCode,
                    address.adminArea,
                    address.subAdminArea,
                    address.locality,
                    address.subLocality
                )
            }

            fun of(o: JSONObject): AddressSummary {
                return AddressSummary(
                    o.getString("text"),
                    o.optString("countryName", "**null**")
                        .let { if (it == "**null**") null else it },
                    o.optString("postalCode", "**null**")
                        .let { if (it == "**null**") null else it },
                    o.optString("adminArea", "**null**")
                        .let { if (it == "**null**") null else it },
                    o.optString("subAdminArea", "**null**")
                        .let { if (it == "**null**") null else it },
                    o.optString("locality", "**null**")
                        .let { if (it == "**null**") null else it },
                    o.optString("subLocality", "**null**")
                        .let { if (it == "**null**") null else it }
                )
            }
        }
    }

    private enum class Place {
        PLACE_1 {
            override val coordinates: Coordinates
                get() = Coordinates(37.506846, 139.906269)
        },
        PLACE_2 {
            override val coordinates: Coordinates
                get() = Coordinates(37.536306, 140.073389)
        },
        PLACE_3 {
            override val coordinates: Coordinates
                get() = Coordinates(35.475781, 139.609650)
        },
        ;

        abstract val coordinates: Coordinates
    }

    private class Result(val locale: Locale, val place: Place, val addressSummary: AddressSummary) {
        fun toJASONObject(): JSONObject =
            JSONObject().apply {
                put("locale", JSONObject().apply {
                    put("language", locale.language)
                    put("country", locale.country)
                })
                put("place", place.toString())
                put("address", addressSummary.toJSONObject())
            }

        val localeAndPlace: LocaleAndPlace
            get() = LocaleAndPlace(locale, place)

        companion object {
            fun of(o: JSONObject): Result {
                return Result(
                    o.getJSONObject("locale").let { x ->
                        Locale(x.getString("language"), x.getString("country"))
                    },
                    Place.valueOf(o.getString("place")),
                    AddressSummary.of(o.getJSONObject("address"))
                )
            }
        }
    }

    private class SummariesOfLocale(
        val summaryOfPlace1: AddressSummary,
        val summaryOfPlace2: AddressSummary,
        val summaryOfPlace3: AddressSummary
    ) {
        fun toJSONObject(): JSONObject {
            return JSONObject().also {
                it.put("place1", summaryOfPlace1.toJSONObject())
                it.put("place2", summaryOfPlace2.toJSONObject())
                it.put("place3", summaryOfPlace3.toJSONObject())
            }
        }

        fun getStyle(locales: Array<String>): String {
            val style1 = summaryOfPlace1.getStyle(locales)
            val style2 = summaryOfPlace2.getStyle(locales)
            val style3 = summaryOfPlace3.getStyle(locales)
            if (style1 != style2)
                throw Exception()
            if (style1 != style3)
                throw Exception()
            return style1
        }

        override fun equals(other: Any?): Boolean {
            if (this === other)
                return true
            if (javaClass != other?.javaClass)
                return false
            other as SummariesOfLocale
            if (summaryOfPlace1 != other.summaryOfPlace1)
                return false
            if (summaryOfPlace2 != other.summaryOfPlace2)
                return false
            if (summaryOfPlace3 != other.summaryOfPlace3)
                return false
            return true
        }

        override fun hashCode(): Int {
            return ((summaryOfPlace1.hashCode() * 31) + summaryOfPlace2.hashCode()) * 31 + summaryOfPlace3.hashCode()
        }
    }

    private class LocaleAndPlace(val locale: Locale, val place: Place) {
        override fun equals(other: Any?): Boolean {
            if (this === other)
                return true
            if (javaClass != other?.javaClass)
                return false
            other as LocaleAndPlace
            if (locale != other.locale)
                return false
            if (place != other.place)
                return false
            return true
        }

        override fun hashCode(): Int {
            return (locale.hashCode() * 31) + place.hashCode()
        }
    }

    private class GroupedResult(
        val summariesOfLocale: SummariesOfLocale,
        val locales: Array<String>
    ) {
        fun toJSONObject(): JSONObject {
            return JSONObject().also { o ->
                o.put("summaries", summariesOfLocale.toJSONObject())
                o.put(
                    "locales",
                    JSONArray()
                        .also { array ->
                            locales.forEach { locale ->
                                array.put(locale)
                            }
                        })
            }
        }
    }

    private class AddressStyle(val style: String, val locales: Array<String>) {
        fun toJSONObject(): JSONObject {
            return JSONObject().also { o ->
                o.put("style", style)
                o.put(
                    "locales",
                    JSONArray()
                        .also { array ->
                            locales.forEach { locale ->
                                array.put(locale)
                            }
                        })
            }
        }
    }

    private val scope = CoroutineScope(Dispatchers.Default)

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
        testActivityRun1.setOnClickListener { _ ->
            ロケールによる住所表記の検索()
        }
        testActivityRun2.setOnClickListener { _ ->
            データの分析()
        }
    }

    @Suppress("NonAsciiCharacters")
    private fun ロケールによる住所表記の検索() {
        val requests: Queue<Pair<Locale, Place>> = LinkedList()
        Locale.getAvailableLocales().forEach { locale ->
            Place.values().forEach { place ->
                requests.add(Pair(locale, place))
            }
        }
        work(requests, JSONArray()) { results ->
            testActivityState.text = "saving"
            getSharedPreferences(packageName, 0)
                .edit()
                .apply {
                    putString(
                        "results",
                        results.toString(2)
                    )
                    apply()
                }
            testActivityState.text = "completed"
        }
    }

    private fun work(
        requests: Queue<Pair<Locale, Place>>,
        results: JSONArray,
        onCompleted: (JSONArray) -> Unit
    ) {
        if (requests.isEmpty()) {
            onCompleted(results)
            return
        }
        testActivityState.text = "working ${requests.size}"
        requests.remove().let { request ->
            AsyncUtility.getAddressFromLocation(
                this,
                request.first,
                scope,
                request.second.coordinates,
                { address ->
                    if (address == null)
                        throw Exception("")
                    else {
                        results.put(
                            Result(
                                request.first,
                                request.second,
                                AddressSummary.of(address)
                            ).toJASONObject()
                        )
                        work(requests, results, onCompleted)
                    }
                },
                { ex ->
                    throw ex
                }
            )
        }
    }

    @Suppress("NonAsciiCharacters")
    private fun データの分析() {
        val map = JSONArray(
            getSharedPreferences(packageName, 0)
                .getString("results", "[]")
        ).let { array ->
            (0..(array.length() - 1))
                .map { index -> array.getJSONObject(index) }
        }
            .map { Result.of(it) }
            .map { Pair(it.localeAndPlace, it) }
            .toMap()
        map.keys.groupBy { it.locale }.keys.map { locale ->
            SummariesOfLocale(
                map[LocaleAndPlace(
                    locale,
                    Place.PLACE_1
                )].let { it ?: throw Exception() }.addressSummary,
                map[LocaleAndPlace(
                    locale,
                    Place.PLACE_2
                )].let { it ?: throw Exception() }.addressSummary,
                map[LocaleAndPlace(
                    locale,
                    Place.PLACE_3
                )].let { it ?: throw Exception() }.addressSummary
            ) to locale.let {
                arrayOf(it.language, it.country).filter { it.isNotEmpty() }.joinToString("_")
            }
        }
            .groupBy { it.first }
            .map { item ->
                GroupedResult(
                    item.key,
                    item.value.map { it.second }.toTypedArray()
                )
            }
            .sortedByDescending { it.locales.count() }
            .map {
                AddressStyle(
                    it.summariesOfLocale.getStyle(it.locales),
                    it.locales
                )
            }
            .groupBy { it.style }
            .map { g ->
                AddressStyle(
                    g.key,
                    g.value
                        .map { it.locales.toList() }
                        .flatten()
                        .sortedBy { it }
                        .toTypedArray()
                )
            }
            .also { results ->
                getSharedPreferences(packageName, 0)
                    .edit()
                    .apply {
                        putString(
                            "grouped_results",
                            JSONArray().also { array ->
                                results.forEach { array.put(it.toJSONObject()) }
                            }.toString(2)
                        )
                        apply()
                    }
            }
    }
}