package com.palmtreesoftware.experimentandroid5_1

import android.os.Build
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.memberFunctions

class Platform {
    companion object {
        private val currentSdkVersion: Int by lazy {
            try {
                System.getenv("__UNITTESTSDKVERSION")?.toInt() ?: Build.VERSION.SDK_INT
            } catch (ex: Exception) {
                /*Build.VERSION.SDK_INT*/ throw ex
            }
        }

        fun <RESULT_T> sdK23Depended(
            greaterThanOrEqual22: () -> RESULT_T,
            lesserThan22: () -> RESULT_T
        ): RESULT_T =
            sdKDepended(Build.VERSION_CODES.M, greaterThanOrEqual22, lesserThan22)

        fun <RESULT_T> sdK26Depended(
            greaterThanOrEqual26: () -> RESULT_T,
            lesserThan26: () -> RESULT_T
        ): RESULT_T =
            sdKDepended(Build.VERSION_CODES.O, greaterThanOrEqual26, lesserThan26)

        private fun <RESULT_T> sdKDepended(
            specifiedSdkVersion: Int,
            greaterThanOrEqualSpecifiedSdkVersion: () -> RESULT_T,
            lesserThanSpecifiedSdkVersion: () -> RESULT_T
        ): RESULT_T =
            if (currentSdkVersion >= specifiedSdkVersion) {
                greaterThanOrEqualSpecifiedSdkVersion()
            } else {
                lesserThanSpecifiedSdkVersion()
            }

        // debug モードでビルドされたときにしか存在しないクラスのコードをリフレクションで呼び出す
        fun callTestCode(): String? {
            // テストコードのコードをリフレクションで探す。 release のときは見つからないので例外が通知される
            val testCodeClass = try {
                Class.forName("com.palmtreesoftware.experimentandroid5_1.TestCode").kotlin
            } catch (ex: ClassNotFoundException) {
                return null
            }
            val testCompanion = testCodeClass.companionObject
            if (testCompanion == null)
                return null
            // run 関数を探す
            val testFunction = testCompanion.memberFunctions.firstOrNull { it.name == "run" }
            if (testFunction == null)
                return null
            // run 関数を呼び出す。第一パラメタには companion object のインスタンスを渡さないといけないらしい
            return testFunction.call(testCompanion.objectInstance).toString()
        }
    }
}