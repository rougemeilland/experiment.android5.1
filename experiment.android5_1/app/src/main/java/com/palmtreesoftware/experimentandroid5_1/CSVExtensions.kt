package com.palmtreesoftware.experimentandroid5_1

interface CSVRow {
    val size: Int
    fun getString(index: Int): String
    fun getInt(index: Int): Int
    fun getLong(index: Int): Long
    fun getDoule(index: Int): Double
}

enum class DelimiterOfCSV {
    COMMA,
    TAB
}

fun android.content.res.Resources.getCommaSeparatedValues(
    resourceId: Int,
    delimiter: DelimiterOfCSV = DelimiterOfCSV.COMMA
): Array<CSVRow> {
    class ImplementOfCSVRow(private val columns: Array<String>) : CSVRow {
        override val size: Int
            get() = columns.size

        override fun getString(index: Int): String = columns[index]

        override fun getInt(index: Int): Int = columns[index].toInt()

        override fun getLong(index: Int): Long = columns[index].toLong()

        override fun getDoule(index: Int): Double = columns[index].toDouble()
    }

    class CSVParser(private val sourceText: String, private val delimiteString: String) {
        protected var index = 0

        fun parse(): Array<CSVRow> =
            mutableListOf<CSVRow>().also { rows ->
                while (index < sourceText.length) {
                    // 連続する空行を読み飛ばす
                    while (index < sourceText.length) {
                        if (sourceText.startsWith("\r\n", index))
                            index += 2
                        else if (sourceText.startsWith("\r", index) ||
                            sourceText.startsWith("\n", index)
                        )
                            index += 1
                        else
                            break
                    }
                    // ファイル終端に達していなければ、行の解析をする
                    if (index < sourceText.length)
                        rows.add(parseRow())
                }
            }.toTypedArray()

        fun parseRow(): CSVRow {
            val columns = mutableListOf<String>()
            while (index < sourceText.length) {
                columns.add(parseColumn())
                if (index >= sourceText.length)
                    return ImplementOfCSVRow(columns.toTypedArray())
                else if (sourceText.startsWith("\r", index) || sourceText.startsWith("\n", index))
                    return ImplementOfCSVRow(columns.toTypedArray())
                else if (sourceText.startsWith(delimiteString, index))
                    index += 1
                else {
                    // column の解析をした後の文字が、ファイルの終端、改行、カンマの何れでもない
                    throw Exception("Internal error: column is not trailed ','")
                }
            }
            return ImplementOfCSVRow(columns.toTypedArray())
        }

        private fun parseColumn(): String {
            val builder = StringBuilder()
            return if (sourceText.startsWith("\r", index) ||
                sourceText.startsWith("\n", index) ||
                sourceText.startsWith(delimiteString, index)
            ) {
                builder.toString()
            } else {
                parseColumnUntilCommna(builder)
            }
        }

        private fun parseColumnUntilCommna(builder: StringBuilder): String {
            while (index < sourceText.length) {
                if (sourceText.startsWith("\r", index) ||
                    sourceText.startsWith("\n", index) ||
                    sourceText.startsWith(delimiteString, index)
                ) {
                    return builder.toString()
                } else if (sourceText.startsWith("\"\"", index)) {
                    builder.append("\"")
                    index += 2
                } else if (sourceText.startsWith("\"", index)) {
                    index += 1
                    parseColumnUntilDoubleQuote(builder, index - 1)
                } else {
                    // この時点で、 index から始まる部分文字列は、空でもなく、デリミタでもなく、改行でもなく、ダブルクォートでもない
                    // 少なくとも最初の文字はエスケープ処理が不要なので、以下の検索は index + 1 から始める
                    val found =
                        sourceText.findAnyOf(listOf(delimiteString, "\"", "\r", "\n"), index + 1)
                    if (found == null) {
                        builder.append(sourceText.substring(index).also { index += it.length })
                        return builder.toString()
                    } else {
                        builder.append(
                            sourceText.substring(index, found.first).also { index += it.length }
                        )
                    }
                }
            }
            return builder.toString()
        }

        private fun parseColumnUntilDoubleQuote(
            builder: StringBuilder,
            startOfColumn: Int
        ): String {
            while (index < sourceText.length) {
                if (sourceText.startsWith("\"\"", index)) {
                    builder.append("\"")
                    index += 2
                } else if (sourceText.startsWith("\"", index)) {
                    index += 1
                    return builder.toString()
                } else {
                    val found = sourceText.indexOf('"', index)
                    if (found < 0) {
                        throw Exception("bad CSV format: column is not closed with double quotes: pos=$startOfColumn")
                    } else {
                        builder.append(
                            sourceText.substring(index, found).also { index += it.length }
                        )
                    }
                }
            }
            throw Exception("bad CSV format: column is not closed with double quotes: pos=$startOfColumn")
        }
    }

    return this.openRawResource(resourceId).bufferedReader().use {
        CSVParser(
            it.readText(),
            if (delimiter == DelimiterOfCSV.COMMA)
                ","
            else if (delimiter == DelimiterOfCSV.TAB)
                "\t"
            else
                throw IllegalArgumentException("Bad delimiter: delimiter=$delimiter")
        ).parse()
    }
}
