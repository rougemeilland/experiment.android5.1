package com.palmtreesoftware.experimentandroid5_1

import de.mannodermaus.junit5.ActivityScenarioExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class SixteenDirectionsInstrumentedTest {

    // TODO("パラメタつきテスト これを参考に。 https://qiita.com/opengl-8080/items/efe54204e25f615e322f#%E3%83%A1%E3%82%BD%E3%83%83%E3%83%89%E3%82%92%E3%82%BD%E3%83%BC%E3%82%B9%E3%81%AB%E3%81%99%E3%82%8B")

    @JvmField
    @RegisterExtension
    val scenarioExtension = ActivityScenarioExtension.launch<TestActivity>()

    @Test
    fun ofDegreesTest() {
        val scenario = scenarioExtension.scenario
        scenario.onActivity { activity ->
            TODO("テストを書く")
        }
    }

    @Test
    fun getDescriptionTest() {
        val scenario = scenarioExtension.scenario
        scenario.onActivity { activity ->
            assertEquals("East", SixteenDirections.E.getDescription(activity))
            TODO("テストを書く")
        }
    }
}