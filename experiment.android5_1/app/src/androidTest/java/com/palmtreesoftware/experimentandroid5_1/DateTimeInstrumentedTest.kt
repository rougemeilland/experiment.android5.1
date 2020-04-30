package com.palmtreesoftware.experimentandroid5_1

import de.mannodermaus.junit5.ActivityScenarioExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

internal class DateTimeInstrumentedTest {

    // TODO("パラメタつきテスト これを参考に。 https://qiita.com/opengl-8080/items/efe54204e25f615e322f#%E3%83%A1%E3%82%BD%E3%83%83%E3%83%89%E3%82%92%E3%82%BD%E3%83%BC%E3%82%B9%E3%81%AB%E3%81%99%E3%82%8B")

    @JvmField
    @RegisterExtension
    val scenarioExtension = ActivityScenarioExtension.launch<TestActivity>()

    @Test
    fun formatRelativeTime() {
        val scenario = scenarioExtension.scenario
        scenario.onActivity { activity ->
            val timeZone = TimeZone.getDefault()
            val now = ZonedDateTime.of(2020, Month.FEBRUARY, 27, 12, 56, 3, timeZone).toDateTime()
            val language = java.util.Locale.getDefault().language
            assertEquals(
                if (language == "ja") "3日前の12:56:03" else "3 days ago at 12:56:03",
                now.minusDays(3).formatRelativeTime(activity, now, timeZone),
                "3日前"
            )
            assertEquals(
                if (language == "ja") "昨日の12:56:03" else "yesterday at 12:56:03",
                now.minusDays(1).formatRelativeTime(activity, now, timeZone),
                "昨日"
            )
            assertEquals(
                if (language == "ja") "09:55:03 (3時間前)" else "09:55:03 (3 hours ago)",
                now.minusMinutes(181).formatRelativeTime(activity, now, timeZone),
                "3時間1分前"
            )
            assertEquals(
                if (language == "ja") "09:56:03 (3時間前)" else "09:56:03 (3 hours ago)",
                now.minusHours(3).formatRelativeTime(activity, now, timeZone),
                "3時間前"
            )
            assertEquals(
                if (language == "ja") "09:57:03 (2時間前)" else "09:57:03 (2 hours ago)",
                now.minusMinutes(179).formatRelativeTime(activity, now, timeZone),
                "2時間59分前"
            )
            assertEquals(
                if (language == "ja") "11:55:03 (1時間前)" else "11:55:03 (one hour ago)",
                now.minusMinutes(61).formatRelativeTime(activity, now, timeZone),
                "61分前"
            )
            assertEquals(
                if (language == "ja") "11:56:03 (1時間前)" else "11:56:03 (one hour ago)",
                now.minusHours(1).formatRelativeTime(activity, now, timeZone),
                "1時間前"
            )
            assertEquals(
                if (language == "ja") "11:57:03 (59分前)" else "11:57:03 (59 minutes ago)",
                now.minusMinutes(59).formatRelativeTime(activity, now, timeZone),
                "59分前"
            )
            assertEquals(
                if (language == "ja") "12:53:02 (3分前)" else "12:53:02 (3 minutes ago)",
                now.minusSeconds(181).formatRelativeTime(activity, now, timeZone),
                "3分1秒前"
            )
            assertEquals(
                if (language == "ja") "12:53:03 (3分前)" else "12:53:03 (3 minutes ago)",
                now.minusMinutes(3).formatRelativeTime(activity, now, timeZone),
                "3分前"
            )
            assertEquals(
                if (language == "ja") "12:53:04 (2分前)" else "12:53:04 (2 minutes ago)",
                now.minusSeconds(179).formatRelativeTime(activity, now, timeZone),
                "2分59秒前"
            )
            assertEquals(
                if (language == "ja") "12:55:02 (1分前)" else "12:55:02 (one minute ago)",
                now.minusSeconds(61).formatRelativeTime(activity, now, timeZone),
                "61秒前"
            )
            assertEquals(
                if (language == "ja") "12:55:03 (1分前)" else "12:55:03 (one minute ago)",
                now.minusMinutes(1).formatRelativeTime(activity, now, timeZone),
                "1分前"
            )
            assertEquals(
                "12:55:04",
                now.minusSeconds(59).formatRelativeTime(activity, now, timeZone),
                "59秒前"
            )
            assertEquals(
                "12:56:00",
                now.minusSeconds(3).formatRelativeTime(activity, now, timeZone),
                "3秒前"
            )
            assertEquals(
                "12:56:02",
                now.minusSeconds(1).formatRelativeTime(activity, now, timeZone),
                "1秒前"
            )
            assertEquals(
                "12:56:03",
                now.formatRelativeTime(activity, now, timeZone),
                "ジャスト"
            )
            assertEquals(
                "12:56:04",
                now.plusSeconds(1).formatRelativeTime(activity, now, timeZone),
                "1秒後"
            )
            assertEquals(
                "12:56:06",
                now.plusSeconds(3).formatRelativeTime(activity, now, timeZone),
                "3秒後"
            )
            assertEquals(
                "12:57:02",
                now.plusSeconds(59).formatRelativeTime(activity, now, timeZone),
                "59秒後"
            )
            assertEquals(
                if (language == "ja") "12:57:03 (1分後)" else "12:57:03 (one minute later)",
                now.plusMinutes(1).formatRelativeTime(activity, now, timeZone),
                "1分後"
            )
            assertEquals(
                if (language == "ja") "12:57:04 (1分後)" else "12:57:04 (one minute later)",
                now.plusSeconds(61).formatRelativeTime(activity, now, timeZone),
                "61秒後"
            )
            assertEquals(
                if (language == "ja") "12:59:02 (2分後)" else "12:59:02 (2 minutes later)",
                now.plusSeconds(179).formatRelativeTime(activity, now, timeZone),
                "2分59秒後"
            )
            assertEquals(
                if (language == "ja") "12:59:03 (3分後)" else "12:59:03 (3 minutes later)",
                now.plusMinutes(3).formatRelativeTime(activity, now, timeZone),
                "3分後"
            )
            assertEquals(
                if (language == "ja") "12:59:04 (3分後)" else "12:59:04 (3 minutes later)",
                now.plusSeconds(181).formatRelativeTime(activity, now, timeZone),
                "3分1秒後"
            )
            assertEquals(
                if (language == "ja") "13:55:03 (59分後)" else "13:55:03 (59 minutes later)",
                now.plusMinutes(59).formatRelativeTime(activity, now, timeZone),
                "59分後"
            )
            assertEquals(
                if (language == "ja") "13:56:03 (1時間後)" else "13:56:03 (one hour later)",
                now.plusHours(1).formatRelativeTime(activity, now, timeZone),
                "1時間後"
            )
            assertEquals(
                if (language == "ja") "13:57:03 (1時間後)" else "13:57:03 (one hour later)",
                now.plusMinutes(61).formatRelativeTime(activity, now, timeZone),
                "1時間1分後"
            )
            assertEquals(
                if (language == "ja") "15:55:03 (2時間後)" else "15:55:03 (2 hours later)",
                now.plusMinutes(179).formatRelativeTime(activity, now, timeZone),
                "2時間59分後"
            )
            assertEquals(
                if (language == "ja") "15:56:03 (3時間後)" else "15:56:03 (3 hours later)",
                now.plusHours(3).formatRelativeTime(activity, now, timeZone),
                "3時間後"
            )
            assertEquals(
                if (language == "ja") "15:57:03 (3時間後)" else "15:57:03 (3 hours later)",
                now.plusMinutes(181).formatRelativeTime(activity, now, timeZone),
                "3時間１分後"
            )
            assertEquals(
                if (language == "ja") "明日の12:56:03" else "tomorrow 12:56:03",
                now.plusDays(1).formatRelativeTime(activity, now, timeZone),
                "明日"
            )
            assertEquals(
                if (language == "ja") "3日後の12:56:03" else "3 days later at 12:56:03",
                now.plusDays(3).formatRelativeTime(activity, now, timeZone),
                "3日後"
            )
        }
    }
}