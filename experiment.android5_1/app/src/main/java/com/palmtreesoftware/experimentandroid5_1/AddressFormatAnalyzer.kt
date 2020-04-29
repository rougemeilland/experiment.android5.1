package com.palmtreesoftware.experimentandroid5_1

import android.content.Context
import android.location.Address
import kotlinx.coroutines.CoroutineScope
import org.json.JSONArray
import org.json.JSONObject

// Geocoder における住所表現の書式が共通の Locale をグループ化する
abstract class AddressFormatAnalyzer {
    private class AddressSummary private constructor(
        val addressText: String,
        val countryName: String?,
        val postalCode: String?,
        val adminArea: String?,
        val subAdminArea: String?,
        val locality: String?,
        val subLocality: String?
    ) : JSONObjectCompatible {
        fun getStyle(locales: Array<String>): String {
            if (countryName == null)
                throw Exception()
            if (postalCode == null)
                throw Exception()
            return when {
                addressText.startsWith(
                    arrayOf(adminArea, subAdminArea, locality, subLocality)
                        .filterNotNull()
                        .joinToString(separator = "", prefix = "$countryName、〒$postalCode ")
                ) -> {
                    "ja:<countryName>、〒<postalCode> <adminArea><subAdminArea>?<locality><subLocality>?***"
                }
                addressText.endsWith(
                    arrayOf(subLocality, locality, subAdminArea, adminArea)
                        .filterNotNull()
                        .joinToString(", ", postfix = " $postalCode, $countryName")
                ) -> {
                    "en:***(, <subLocality>)?, <locality>(, <subAdminArea>)?, <adminArea> <postalCode>, <countryName>"
                }
                addressText.endsWith(
                    arrayOf(subLocality, locality, subAdminArea, adminArea)
                        .filterNotNull()
                        .joinToString(", ", postfix = " $postalCode\u060C $countryName")
                ) -> {
                    "ar:***(, <subLocality>)?, <locality>(, <subAdminArea>)?, <adminArea> <postalCode>&#x60C; <countryName>"
                }
                addressText.endsWith(
                    arrayOf(subLocality, locality, subAdminArea, adminArea)
                        .filterNotNull()
                        .joinToString(", ", postfix = " $postalCode$countryName")
                ) -> {
                    "zh:***(, <subLocality>)?, <locality>(, <subAdminArea>)?, <adminArea> <postalCode><countryName>"
                }
                addressText.endsWith(
                    arrayOf(subLocality, locality, subAdminArea, adminArea)
                        .filterNotNull()
                        .joinToString(", ", postfix = " $postalCode $countryName")
                ) -> {
                    "ko:***(, <subLocality>)?, <locality>(, <subAdminArea>)?, <adminArea> <postalCode> <countryName>"
                }
                else -> {
                    throw Exception("unknown style of address: locales=${locales.toJSONArray()}")
                }
            }
        }

        override fun toJSONObject(): JSONObject {
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
                    address.addressLines.joinToString("\n"),
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
                    o.optString("countryName").let { if (it.isEmpty()) null else it },
                    o.optString("postalCode").let { if (it.isEmpty()) null else it },
                    o.optString("adminArea").let { if (it.isEmpty()) null else it },
                    o.optString("subAdminArea").let { if (it.isEmpty()) null else it },
                    o.optString("locality").let { if (it.isEmpty()) null else it },
                    o.optString("subLocality").let { if (it.isEmpty()) null else it }
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

    private class IntermediateResult(
        val locale: java.util.Locale,
        val place: Place,
        val addressSummary: AddressSummary
    ) : JSONObjectCompatible {
        override fun toJSONObject(): JSONObject =
            JSONObject().apply {
                put("locale", locale.toJSONObject())
                put("place", place.toString())
                put("address", addressSummary.toJSONObject())
            }

        val localeAndPlace: LocaleAndPlace
            get() = LocaleAndPlace(locale, place)

        companion object {
            fun of(o: JSONObject): IntermediateResult {
                return IntermediateResult(
                    localeOf(o.getJSONObject("locale")),
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
    ) : JSONObjectCompatible {
        override fun toJSONObject(): JSONObject {
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

    private class LocaleAndPlace(val locale: java.util.Locale, val place: Place) {
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
    ) : JSONObjectCompatible {
        override fun toJSONObject(): JSONObject {
            return JSONObject().also { o ->
                o.put("summaries", summariesOfLocale.toJSONObject())
                o.put("locales", locales.toJSONArray())
            }
        }
    }

    private class AddressStyle(val style: String, val locales: Array<String>) :
        JSONObjectCompatible {
        override fun toJSONObject(): JSONObject {
            return JSONObject().also { o ->
                o.put("style", style)
                o.put("locales", locales.toJSONArray())
            }
        }
    }

    companion object {

        fun collectAddressSummary(
            context: Context,
            scope: CoroutineScope,
            progress: (String) -> Unit,
            onCompleted: (JSONArray) -> Unit,
            maxCount: Int = -1
        ) {
            val requests: java.util.Queue<Pair<java.util.Locale, Place>> = java.util.LinkedList()
            java.util.Locale.getAvailableLocales()
                .crossMap(Place.values()) { locale, place -> Pair(locale, place) }
                .let { if (maxCount >= 0) it.take(maxCount) else it }
                .forEach {
                    requests.add(it)
                }
            addressSummaryCollectingWorker(context, scope, requests, JSONArray(), progress, {
                progress("completed")
                onCompleted(it)
            })
        }

        private fun addressSummaryCollectingWorker(
            context: Context,
            scope: CoroutineScope,
            requests: java.util.Queue<Pair<java.util.Locale, Place>>,
            results: JSONArray,
            progress: (String) -> Unit,
            onCompleted: (JSONArray) -> Unit
        ) {
            if (requests.isEmpty()) {
                onCompleted(results)
                return
            }
            progress("working ${requests.size}")
            requests.remove().let { request ->
                AsyncUtility.getAddressFromLocation(
                    context,
                    scope,
                    request.first,
                    request.second.coordinates,
                    { address ->
                        if (address == null)
                            throw Exception("")
                        else {
                            results.put(
                                IntermediateResult(
                                    request.first,
                                    request.second,
                                    AddressSummary.of(address)
                                ).toJSONObject()
                            )
                            addressSummaryCollectingWorker(
                                context,
                                scope,
                                requests,
                                results,
                                progress,
                                onCompleted
                            )
                        }
                    },
                    { ex ->
                        throw ex
                    }
                )
            }
        }

        fun analyzeAddressSummary(source: JSONArray): JSONArray {
            val map = (0 until source.length())
                .map { index ->
                    IntermediateResult.of(source.getJSONObject(index))
                        .let { Pair(it.localeAndPlace, it) }
                }.toMap()
            return map.keys
                .groupBy { it.locale }.keys
                .asSequence()
                .map { locale ->
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
                    ) to locale.toLanguageTag()
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
                .toJSONArray()
        }

        fun collectAddressForTest(
            context: Context,
            scope: CoroutineScope,
            progress: (String) -> Unit,
            onCompleted: (Array<JSONObject>) -> Unit,
            maxCount: Int = -1
        ) {
            val requests: java.util.Queue<Pair<java.util.Locale, Place>> = java.util.LinkedList()
            java.util.Locale.getAvailableLocales()
                .crossMap(Place.values()) { locale, place -> Pair(locale, place) }
                .let { if (maxCount >= 0) it.take(maxCount) else it }
                .forEach {
                    requests.add(it)
                }
            addressForTestCollectingWorker(context, scope, requests, mutableListOf(), progress, {
                progress("completed")
                onCompleted(it)
            })
        }

        private fun addressForTestCollectingWorker(
            context: Context,
            scope: CoroutineScope,
            requests: java.util.Queue<Pair<java.util.Locale, Place>>,
            results: MutableList<JSONObject>,
            progress: (String) -> Unit,
            onCompleted: (Array<JSONObject>) -> Unit
        ) {
            if (requests.isEmpty()) {
                onCompleted(results.toTypedArray())
                return
            }
            progress("working ${requests.size}")
            requests.remove().let { request ->
                AsyncUtility.getAddressFromLocation(
                    context,
                    scope,
                    request.first,
                    request.second.coordinates,
                    { address ->
                        if (address == null)
                            throw Exception("")
                        else {
                            results.add(address.toJSONObject())
                            addressForTestCollectingWorker(
                                context,
                                scope,
                                requests,
                                results,
                                progress,
                                onCompleted
                            )
                        }
                    },
                    { ex ->
                        throw ex
                    }
                )
            }
        }
    }
}