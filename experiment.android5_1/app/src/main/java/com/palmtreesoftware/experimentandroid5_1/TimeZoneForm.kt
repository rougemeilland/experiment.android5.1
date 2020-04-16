package com.palmtreesoftware.experimentandroid5_1

/*
TODO("TimeZoneForm を abstract class に変更して、TimeZone の以下のプロパティを TimeZoneForm の companion object に移動する")

        const val maxTimeZoneHour: Int = 14 + innerOffsetOfHour
        const val minTimeZoneHour: Int = -12 + innerOffsetOfHour
        const val maxTimeZoneMinute: Int = 59
        const val minTimeZoneMinute: Int = 0
        const val timeZoneIdOfSystemDefault: String = "-"
        const val timeZoneIdOfTimeDifferenceExpression: String = "#"

 */
interface TimeZoneForm {
    var id: String
    var hour: Int
    var minute: Int
}
