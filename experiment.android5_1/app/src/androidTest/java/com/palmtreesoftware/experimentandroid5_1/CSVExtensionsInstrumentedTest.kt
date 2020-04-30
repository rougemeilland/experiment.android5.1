package com.palmtreesoftware.experimentandroid5_1

import de.mannodermaus.junit5.ActivityScenarioExtension
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class CSVExtensionsInstrumentedTest {

    @JvmField
    @RegisterExtension
    val scenarioExtension = ActivityScenarioExtension.launch<TestActivity>()

    @Test
    fun getCommaSeparatedValues1() {
        val scenario = scenarioExtension.scenario
        scenario.onActivity { activity ->
            var rows = arrayOf<CSVRow>()
            assertDoesNotThrow({
                rows = activity.resources.getCommaSeparatedValues(
                    R.raw.test_csv_comma_1,
                    DelimiterOfCSV.COMMA
                )
            })
            assertEquals(0, rows.size)
        }
    }

    @Test
    fun getCommaSeparatedValues2() {
        val scenario = scenarioExtension.scenario
        scenario.onActivity { activity ->
            var rows = arrayOf<CSVRow>()
            assertDoesNotThrow({
                rows = activity.resources.getCommaSeparatedValues(
                    R.raw.test_csv_comma_2,
                    DelimiterOfCSV.COMMA
                )
            })
            assertEquals(0, rows.size)
        }
    }

    @Test
    fun getCommaSeparatedValues3() {
        val scenario = scenarioExtension.scenario
        scenario.onActivity { activity ->
            var rows = arrayOf<CSVRow>()
            assertDoesNotThrow({
                rows = activity.resources.getCommaSeparatedValues(
                    R.raw.test_csv_comma_3,
                    DelimiterOfCSV.COMMA
                )
            })
            assertEquals(21, rows.size)

            rows[0].also { row ->
                assertEquals(2, row.size)
                assertEquals("", row.getString(0))
                assertEquals("", row.getString(1))
            }

            rows[1].also { row ->
                assertEquals(2, row.size)
                assertEquals("2", row.getString(0))
                assertEquals("", row.getString(1))
            }

            rows[2].also { row ->
                assertEquals(2, row.size)
                assertEquals("", row.getString(0))
                assertEquals("3", row.getString(1))
            }

            rows[3].also { row ->
                assertEquals(2, row.size)
                assertEquals("\"", row.getString(0))
                assertEquals("4", row.getString(1))
            }

            rows[4].also { row ->
                assertEquals(2, row.size)
                assertEquals("5", row.getString(0))
                assertEquals("\"", row.getString(1))
            }

            rows[5].also { row ->
                assertEquals(2, row.size)
                assertEquals("6", row.getString(0))
                assertEquals("", row.getString(1))
            }

            rows[6].also { row ->
                assertEquals(2, row.size)
                assertEquals("", row.getString(0))
                assertEquals("7", row.getString(1))
            }

            rows[7].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[8].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("9", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[9].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("\"", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[10].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("11", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[11].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("\"12", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[12].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals(" \"13", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[13].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("1\"4", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[14].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("15\"", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[15].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals(",16", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[16].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("1,7", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[17].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("18,", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[18].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("\r\n19", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[19].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("2\r\n0", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[20].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("21\r\n", row.getString(1))
                assertEquals("", row.getString(2))
            }
        }
    }

    @Test
    fun getCommaSeparatedValues4() {
        val scenario = scenarioExtension.scenario
        scenario.onActivity { activity ->
            var rows = arrayOf<CSVRow>()
            assertDoesNotThrow {
                rows = activity.resources.getCommaSeparatedValues(
                    R.raw.test_csv_comma_4,
                    DelimiterOfCSV.TAB
                )
            }
            assertEquals(21, rows.size)

            rows[0].also { row ->
                assertEquals(2, row.size)
                assertEquals("", row.getString(0))
                assertEquals("", row.getString(1))
            }

            rows[1].also { row ->
                assertEquals(2, row.size)
                assertEquals("2", row.getString(0))
                assertEquals("", row.getString(1))
            }

            rows[2].also { row ->
                assertEquals(2, row.size)
                assertEquals("", row.getString(0))
                assertEquals("3", row.getString(1))
            }

            rows[3].also { row ->
                assertEquals(2, row.size)
                assertEquals("\"", row.getString(0))
                assertEquals("4", row.getString(1))
            }

            rows[4].also { row ->
                assertEquals(2, row.size)
                assertEquals("5", row.getString(0))
                assertEquals("\"", row.getString(1))
            }

            rows[5].also { row ->
                assertEquals(2, row.size)
                assertEquals("6", row.getString(0))
                assertEquals("", row.getString(1))
            }

            rows[6].also { row ->
                assertEquals(2, row.size)
                assertEquals("", row.getString(0))
                assertEquals("7", row.getString(1))
            }

            rows[7].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[8].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("9", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[9].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("\"", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[10].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("11", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[11].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("\"12", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[12].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals(" \"13", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[13].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("1\"4", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[14].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("15\"", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[15].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("\t16", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[16].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("1\t7", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[17].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("18\t", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[18].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("\r\n19", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[19].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("2\r\n0", row.getString(1))
                assertEquals("", row.getString(2))
            }

            rows[20].also { row ->
                assertEquals(3, row.size)
                assertEquals("", row.getString(0))
                assertEquals("21\r\n", row.getString(1))
                assertEquals("", row.getString(2))
            }
        }
    }
}
