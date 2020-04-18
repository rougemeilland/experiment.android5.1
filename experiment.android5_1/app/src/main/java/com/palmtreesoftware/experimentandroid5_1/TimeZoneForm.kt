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
// TODO("以下の仕様は TimeZoneForm を実装するフォーム側に持ち込む")
// android の NumberPicker は負数は扱えない模様
// ※ minValue に負の数を入れてみたら、例外 (java.lang.IllegalArgumentException: minValue must be >= 0) が発生した
// この問題を回避するため、以下の対策を行った
// 1) NumberPicker に与える value, minValue, maxValue に innerOffsetOfHourAndMinute だけのゲタをはかせる
// 2) 時の数値をタイムゾーンIDに変換するときはゲタを元に戻す。逆にタイムゾーンIDから時を取得するときは再びゲタをはかせる
/*
private const val innerOffsetOfHour: Int = 100

const val maxTimeZoneHour: Int = 14 + innerOffsetOfHour
const val minTimeZoneHour: Int = -12 + innerOffsetOfHour
const val maxTimeZoneMinute: Int = 59
const val minTimeZoneMinute: Int = 0
const val timeZoneIdOfSystemDefault: String = "-"
const val timeZoneIdOfTimeDifferenceExpression: String = "#"
*/

/*
TODO("NumberPickerの既知のバグで、初期状態で選択されている項目に formatter が正常に適用されていない。NumberPicker に対して何らかの操作をすると正しく表示される。formatter を使用せずに、フォーマット済みの表示文字列の配列を displayedValues に与えることによって回避")
TODO("タイムゾーン絡みの画面操作がややこしくなってきたのでカスタムコントロールみたいなものにまとめられないか？ https://qiita.com/kaleidot725/items/ff4bd7e99012438aaa42 要調査")
 */
interface TimeZoneForm {
    val isDefault: Boolean
    fun getSymbol(): String?
    fun getTimeDifference(): Triple<Int, Int, Int>?
    fun reset()
    fun set(timeZoneId: String)
    fun set(hours: Int, minutes: Int)
}
