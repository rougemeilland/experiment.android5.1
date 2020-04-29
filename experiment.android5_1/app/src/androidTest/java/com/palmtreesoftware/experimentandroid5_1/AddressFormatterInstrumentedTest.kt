package com.palmtreesoftware.experimentandroid5_1

import de.mannodermaus.junit5.ActivityScenarioExtension
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class AddressFormatterInstrumentedTest {

    @JvmField
    @RegisterExtension
    val scenarioExtension = ActivityScenarioExtension.launch<TestActivity>()

    @Test
    fun omitAddress() {
        TODO("海外の住所も正しく短縮できるかどうかのテストをする。まずはテストデータ収集から。どの範囲のデータを収集するか？すべての組み合わせは現実的に無理。")
        val scenario = scenarioExtension.scenario
        scenario.onActivity { activity ->
            activity.resources
                .getJSONArray(R.raw.address_formatter_test)
                .toIterableOfJSONObject()
                .map { addressOf(it) }
                .forEach { address ->
                    val addressText =
                        AddressFormatter.of(address.locale).omitAddress(address, address)
                    assertTrue(address.countryName == null || !addressText.contains(address.countryName))
                    assertTrue(address.postalCode == null || !addressText.contains(address.postalCode))
                    assertTrue(address.adminArea == null || !addressText.contains(address.adminArea))
                    assertTrue(address.subAdminArea == null || !addressText.contains(address.subAdminArea))
                    assertTrue(address.locality == null || !addressText.contains(address.locality))
                    assertTrue(address.subLocality == null || !addressText.contains(address.subLocality))
                }
        }
    }

    @Test
    fun omitAddressExtension() {
        val scenario = scenarioExtension.scenario
        scenario.onActivity { activity ->
            activity.resources
                .getJSONArray(R.raw.address_formatter_test)
                .toIterableOfJSONObject()
                .map { addressOf(it) }
                .forEach { address ->
                    val addressText = address.omitAddress(address)
                    assertTrue(address.countryName == null || !addressText.contains(address.countryName))
                    assertTrue(address.postalCode == null || !addressText.contains(address.postalCode))
                    assertTrue(address.adminArea == null || !addressText.contains(address.adminArea))
                    assertTrue(address.subAdminArea == null || !addressText.contains(address.subAdminArea))
                    assertTrue(address.locality == null || !addressText.contains(address.locality))
                    assertTrue(address.subLocality == null || !addressText.contains(address.subLocality))
                }
        }
    }
}
