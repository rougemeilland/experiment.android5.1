package com.palmtreesoftware.experimentandroid5_1

import android.location.Address
import java.util.*

abstract class AddressFormatter {
    /*
     日本における Address クラスのプロパティの意味付けは以下の通り
     countryName => 国名
     postalCode => 郵便番号、ただし先頭の'〒'はない
     adminArea => 都道府県
     subAdminArea => 郡 (optional) (例: 福島県耶麻郡 など)
     locality => 区市町村 (例：東京都中央区 などの区はこちら)
     subLocality => 区 （optional） (例: 横浜市鶴見区 などの区はこちら)
     thoroughfare => xx丁目?
     subThoroughfare => 番地?
     featureName, => 号?
     premises => 号?
     ※ xxxx町 に該当するプロパティは存在しない模様
     */
    protected class WorkingState(
        private val isEnd: Boolean,
        val addressText: String
    ) {
        fun omitLeadingComponent(
            currentComponent: String?,
            targetComponent: String?,
            delimiter: String = ""
        ): WorkingState {
            if (isEnd) {
                // 前の段階で既に相違点が見つかっていた場合
                return this
            }
            if (currentComponent != targetComponent) {
                // 対応するコンポーネントに新たに相違点が見つかった場合
                return WorkingState(true, addressText)
            }
            if (currentComponent == null) {
                // 対応するコンポーネントがともに null だった場合
                return WorkingState(false, addressText)
            }
            // この時点で、 currentComponent と targetComponent は等しく、かつともに非 null
            if (addressText.startsWith(currentComponent + delimiter)) {
                // addressText が currentComponent + delimiter で始まっていた場合
                // addressText の先頭から currentComponent + delimiter に一致する文字列を削除する
                return WorkingState(
                    false,
                    addressText.substring(currentComponent.length + delimiter.length)
                )
            }
            // この時点で、 currentComponent と targetComponent は等しく、かつともに非 null であるが、 addressText は currentComponent + delimiter で始まっていない
            // addressText が認識できないフォーマットなので、コンポーネントの不一致と同様の扱いとする
            return WorkingState(true, addressText)
        }

        fun omitTrailingComponent(
            currentComponent: String?,
            targetComponent: String?,
            delimiter: String = ""
        ): WorkingState {
            if (isEnd) {
                // 前の段階で既に相違点が見つかっていた場合
                return this
            }
            if (currentComponent != targetComponent) {
                // 対応するコンポーネントに新たに相違点が見つかった場合
                return WorkingState(true, addressText)
            }
            if (currentComponent == null) {
                // 対応するコンポーネントがともに null だった場合
                return WorkingState(false, addressText)
            }
            // この時点で、 currentComponent と targetComponent は等しく、かつともに非 null
            if (addressText.endsWith(delimiter + currentComponent)) {
                // addressText が 与えられた delimiter + currentComponent で終わっていた場合
                // addressText の最後から delimiter + currentComponent に一致する文字列を削除する
                return WorkingState(
                    false,
                    addressText.substring(
                        0,
                        addressText.length - (delimiter.length + currentComponent.length)
                    )
                )
            }
            // この時点で、 currentComponent と targetComponent は等しく、かつともに非 null であるが、 addressText は delimiter + currentComponent で終わっていない
            // addressText が認識できないフォーマットなので、コンポーネントの不一致と同様の扱いとする
            return WorkingState(true, addressText)
        }

        fun omitLeadingText(
            text: String?,
            delimiter: String = ""
        ): WorkingState {
            if (isEnd) {
                // 前の段階で既に相違点が見つかっていた場合
                // そのまま次に渡す
                return this
            }
            if (text == null) {
                // text が null だった場合
                // そのまま次に渡す
                return this
            }
            if (addressText.startsWith(text + delimiter)) {
                // addressText が text + delimiter で始まっていた場合
                // addressText の先頭から text + delimiter に一致する文字列を削除する
                return WorkingState(false, addressText.substring(text.length + delimiter.length))
            }
            // addressText が text + delimiter で始まっていなかった場合
            // addressText が認識できないフォーマットなので、元の addressText のまま完了済みとして返す
            return WorkingState(true, addressText)
        }

        fun omitTrailingText(
            text: String?,
            delimiter: String = ""
        ): WorkingState {
            if (isEnd) {
                // 前の段階で既に相違点が見つかっていた場合
                return this
            }
            if (text == null) {
                // text が null だった場合
                // そのまま次に渡す
                return this
            }
            if (addressText.endsWith(delimiter + text)) {
                // addressText が delimiter + text で終わっていた場合
                // addressText の最後から delimiter + text に一致する文字列を削除する
                return WorkingState(
                    false,
                    addressText.substring(0, addressText.length - (delimiter.length + text.length))
                )
            }
            // addressText が delimiter + text で終わっていなかった場合
            // addressText が認識できないフォーマットなので、元の addressText のまま完了済みとして返す
            return WorkingState(true, addressText)
        }

        override fun toString(): String {
            return "WorkingState(isEnd='$isEnd', addressText='$addressText')"
        }
    }

    private class AddressFormatterOfJa : AddressFormatter() {
        /*
         ja における住所の書式は以下の通り
         <countryName>、〒<postalCode> <adminArea><subAdminArea>?<locality><subLocality>?***
         */
        override fun omitAddress(currentAddress: Address, targetAddress: Address): String {
            return WorkingState(
                false,
                (0..targetAddress.maxAddressLineIndex)
                    .joinToString("\n") { index ->
                        targetAddress.getAddressLine(index)
                    }
            )
                // countryName が共通していれば除去
                .omitLeadingComponent(currentAddress.countryName, targetAddress.countryName, "、")
                // postalCode が存在していれば除去
                .omitLeadingText(targetAddress.postalCode?.let { "〒$it" }, " ")
                // adminArea が共通していれば除去
                .omitLeadingComponent(currentAddress.adminArea, targetAddress.adminArea)
                // subAdminArea が共通していれば除去
                .omitLeadingComponent(currentAddress.subAdminArea, targetAddress.subAdminArea)
                // locality が共通していれば除去
                .omitLeadingComponent(currentAddress.locality, targetAddress.locality)
                // subLocality が共通していれば除去
                .omitLeadingComponent(currentAddress.subLocality, targetAddress.subLocality)
                // 復帰値
                .addressText
        }

        override fun getLocality(address: Address): String =
            address.locality.let { locality ->
                if (locality != null) {
                    address.subLocality.let { subLocality ->
                        if (subLocality != null) {
                            "$locality$subLocality"
                        } else {
                            locality
                        }
                    }
                } else {
                    address.subLocality ?: ""
                }
            }
    }

    /*
     common における住所の書式は以下の通り
     ***(, <subLocality>)?, <locality>(, <subAdminArea>)?, <adminArea> <postalCode><countryDelimiter><countryName>
     */
    private abstract class AddressFormatterOfCommon(val countryDelimiter: String) :
        AddressFormatter() {
        override fun omitAddress(currentAddress: Address, targetAddress: Address): String =
            WorkingState(
                false,
                (0..targetAddress.maxAddressLineIndex)
                    .joinToString("\n") { index ->
                        targetAddress.getAddressLine(index)
                    }
            )
                // countryName が共通していれば除去
                .omitTrailingComponent(
                    currentAddress.countryName,
                    targetAddress.countryName,
                    countryDelimiter
                )
                // postalCode が存在していれば除去
                .omitTrailingText(targetAddress.postalCode, " ")
                // adminArea が共通していれば除去
                .omitTrailingComponent(currentAddress.adminArea, targetAddress.adminArea, ", ")
                // subAdminArea が共通していれば除去
                .omitTrailingComponent(
                    currentAddress.subAdminArea,
                    targetAddress.subAdminArea,
                    ", "
                )
                // locality が共通していれば除去
                .omitTrailingComponent(currentAddress.locality, targetAddress.locality, ", ")
                // subLocality が共通していれば除去
                .omitTrailingComponent(currentAddress.subLocality, targetAddress.subLocality, ", ")
                .addressText

        override fun getLocality(address: Address): String =
            address.locality.let { locality ->
                if (locality != null) {
                    address.subLocality.let { subLocality ->
                        if (subLocality != null) {
                            "$subLocality, $locality"
                        } else {
                            locality
                        }
                    }
                } else {
                    address.subLocality ?: ""
                }
            }
    }

    /*
     en における住所の書式は以下の通り
     ***(, <subLocality>)?, <locality>(, <subAdminArea>)?, <adminArea> <postalCode>, <countryName>
     */
    private class AddressFormatterOfEn : AddressFormatterOfCommon(", ")

    /*
     ar における住所の書式は以下の通り
     ***(, <subLocality>)?, <locality>(, <subAdminArea>)?, <adminArea> <postalCode>&#x60C; <countryName>
     */
    private class AddressFormatterOfAr : AddressFormatterOfCommon("\u060C ")

    /*
     zh における住所の書式は以下の通り
     ***(, <subLocality>)?, <locality>(, <subAdminArea>)?, <adminArea> <postalCode><countryName>
     */
    private class AddressFormatterOfZh : AddressFormatterOfCommon("")

    /*
     ko における住所の書式は以下の通り
     ***(, <subLocality>)?, <locality>(, <subAdminArea>)?, <adminArea> <postalCode> <countryName>
     */
    private class AddressFormatterOfKo : AddressFormatterOfCommon(" ")

    abstract fun omitAddress(currentAddress: Address, targetAddress: Address): String
    abstract fun getLocality(address: Address): String

    // TODO("テストする")
    companion object {
        fun of(locale: Locale): AddressFormatter =
            when (locale.language) {
                "ar", "ckb", "fa", "ks", "lrc", "mzn", "ps", "sd", "ug", "ur" -> {
                    AddressFormatterOfAr()
                }
                "pa" -> {
                    when (locale.country) {
                        "PK" -> AddressFormatterOfAr()
                        else -> AddressFormatterOfEn()
                    }
                }
                "uz" -> {
                    when (locale.country) {
                        "AF" -> AddressFormatterOfAr()
                        else -> AddressFormatterOfEn()
                    }
                }
                "yue", "zh" -> AddressFormatterOfZh()
                "ko", "th" -> AddressFormatterOfKo()
                "ja" -> AddressFormatterOfJa()
                else -> AddressFormatterOfEn()
            }
    }
}