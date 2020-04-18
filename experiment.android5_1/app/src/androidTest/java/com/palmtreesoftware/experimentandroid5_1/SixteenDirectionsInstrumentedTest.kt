package com.palmtreesoftware.experimentandroid5_1

import de.mannodermaus.junit5.ActivityScenarioExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class SixteenDirectionsInstrumentedTest {

    @JvmField
    @RegisterExtension
    val scenarioExtension = ActivityScenarioExtension.launch<TestActivity>()

    @Test
    fun getDescription() {
        val scenario = scenarioExtension.scenario
        scenario.onActivity { activity ->
            assertEquals("East", SixteenDirections.E.getDescription(activity))
        }
    }
}