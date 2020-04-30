package com.palmtreesoftware.experimentandroid5_1

import android.app.Activity
import android.content.Context
import android.location.Address
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_test.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import kotlin.coroutines.CoroutineContext

// TODO("OpenWeatherMap.OneCall を表示する部品の作成")
// TODO("OpenWeatherMap.FiveDayWeatherForecast を表示する部品の作成")
class TestActivity : AppCompatActivity(), CoroutineScope {

    private abstract class AddressResolver(
        private val sourceFile: File,
        private val destinationFile: File
    ) {
        private var textReader: BufferedReader? = null
        private var textWriter: PrintWriter? = null
        private var totalCount: Int = 0
        private var startTime: DateTime = DateTime.EPOCH
        private var countofWrittenItems: Int = 0
        private val serialNumbersOfUnprocessedItem = LongRangeSet()
        private var isCancelRequested = false

        fun resolveAddress(context: Context, scope: CoroutineScope) {
            close()
            startTime = DateTime.now()
            sourceFile.useLines { lines ->
                lines.forEach { line ->
                    try {
                        serialNumbersOfUnprocessedItem.add(JSONObject(line).getLong("serialNumber"))
                    } catch (ex: JSONException) {
                    }
                }
            }
            totalCount = serialNumbersOfUnprocessedItem.count()
            countofWrittenItems = 0
            serialNumbersOfUnprocessedItem.clear()
            textReader = BufferedReader(FileReader(sourceFile))
            if (destinationFile.exists()) {
                destinationFile.useLines { lines ->
                    lines.forEach { line ->
                        try {
                            serialNumbersOfUnprocessedItem.remove(
                                JSONObject(line).getLong("serialNumber")
                            )
                        } catch (ex: JSONException) {
                        }
                    }
                }
            }
            textWriter = PrintWriter(BufferedWriter(FileWriter(destinationFile, true)))
            resolveAddressLoop(context, scope)
        }

        private fun resolveAddressLoop(context: Context, scope: CoroutineScope) {
            if (isCancelRequested) {
                isCancelRequested = false
                close()
                onCancelled()
                return
            }
            val data = readJSONObject()
            if (data == null) {
                close()
                onCompleted()
                return
            }
            try {
                data.let {
                    val locale = localeOf(it.getJSONObject("locale"))
                    val coordinates = Coordinates.of(it.getJSONObject("coordinates"))
                    val serialNumber = it.getLong("serialNumber")
                    AsyncUtility.getAddressFromLocation(
                        context,
                        scope,
                        locale,
                        coordinates,
                        { address ->
                            try {
                                writeAddress(address, serialNumber)
                                reportProgress(context)
                                resolveAddressLoop(context, scope)
                            } catch (throwable: Throwable) {
                                close()
                                onFailed(throwable)
                            }
                        },
                        { throwable ->
                            close()
                            onFailed(throwable)
                        }
                    )
                }
            } catch (throwable: Throwable) {
                close()
                onFailed(throwable)
            }
        }

        private fun close() {
            textReader?.close()
            textReader = null
            textWriter?.close()
            textWriter = null
        }

        fun requestToCancel() {
            isCancelRequested = true
        }

        private fun readJSONObject(): JSONObject? {
            textReader.also { reader ->
                if (reader == null)
                    throw Exception("internal error")
                while (true) {
                    val line = reader.readLine()
                        ?: return null
                    try {
                        val o = JSONObject(line)
                        if (serialNumbersOfUnprocessedItem.contains(o.getLong("serialNumber")))
                            return o
                    } catch (throwable: Throwable) {
                    }
                }
            }
        }

        private fun writeAddress(address: Address?, serialNumber: Long) {
            if (address != null) {
                textWriter.also { writer ->
                    if (writer == null)
                        throw Exception("internal error")
                    writer.println(
                        address.toJSONObject().also {
                            it.put("serialNumber", serialNumber)
                        }.toString()
                    )
                }
            }
            ++countofWrittenItems
        }

        private fun reportProgress(context: Context) {
            if (countofWrittenItems != 0 && countofWrittenItems % 100 == 0) {
                val now = DateTime.now()
                val countOfUnprocessedItems = serialNumbersOfUnprocessedItem.count()
                val endtime =
                    now + (now - startTime) *
                            (countOfUnprocessedItems - countofWrittenItems).toLong() /
                            countofWrittenItems.toLong()

                progressReporter(
                    "resolving address... %.02f%% expected to end %s %s%s".format(
                        ((countofWrittenItems + (totalCount - countOfUnprocessedItems)) * 100.0 / totalCount),
                        endtime.formatRelativeTime(context, now, TimeZone.getDefault()),
                        "■".repeat(((countofWrittenItems / 100) % 10).toInt()),
                        "□".repeat(10 - ((countofWrittenItems / 100) % 10).toInt())
                    )
                )
            }
        }

        protected abstract fun progressReporter(progress: String)
        protected abstract fun onCompleted()
        protected abstract fun onCancelled()
        protected abstract fun onFailed(throwable: Throwable)
    }

    private val job = Job()

    private var addressResolver: AddressResolver? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        testActivityOk.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finishAndRemoveTask()
        }
        testActivityCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finishAndRemoveTask()
        }
        testActivityRun1.setOnClickListener {
            AsyncUtility.runAasynchronously(
                this,
                beforeWorking = {
                    testActivityRun1.isEnabled = false
                    testActivityState.text = "scanning..."
                    File(filesDir, "addresses.txt").delete()
                    File(filesDir, "requests.txt").delete()
                    ""
                },
                worker = { _, progressReporter ->
                    val dic = resources.getJSONArray(R.raw.city_list).toIterableOfJSONObject()
                        .map { o ->
                            Pair(
                                o.getString("country"),
                                o.getJSONObject("coord").let {
                                    Coordinates(it.getDouble("lat"), it.getDouble("lon"))
                                })
                        }
                        .filter { it.first.isNotEmpty() }
                        .groupBy { it.first }
                        .map { x -> Pair(x.key, x.value.map { it.second }.toTypedArray()) }
                        .toMap()
                    val requests = java.util.Locale.getAvailableLocales()
                        .filter { it.country.isNotEmpty() }
                        .mapNotNull { locale ->
                            dic[locale.country].let { arrayOfCoordinates ->
                                arrayOfCoordinates?.map { Pair(locale, it) }
                            }
                        }.flatten()
                        .union(
                            java.util.Locale.getAvailableLocales()
                                .crossMap(resources.getCommaSeparatedValues(
                                    R.raw.h3104world_utf8,
                                    DelimiterOfCSV.TAB
                                )
                                    .drop(1)
                                    .map {
                                        Coordinates(
                                            it.getDoule(7),
                                            it.getDoule(8)
                                        )
                                    }) { locale, coordinates -> Pair(locale, coordinates) }
                        )
                    PrintWriter(
                        BufferedWriter(FileWriter(File(filesDir, "requests.txt")))
                    ).use { textWriter ->
                        var count = 0L
                        requests.forEach { request ->
                            textWriter.println(JSONObject().apply {
                                put("serialNumber", count)
                                put("locale", request.first.toJSONObject())
                                put("coordinates", request.second.toJSONObject())
                            }.toString())
                            ++count
                            if (count % 1000 == 0L)
                                progressReporter("writing ${",%d".format(count)}")
                        }
                        "${count} items saved"
                    }
                },
                progress = { progress ->
                    progress as String
                    testActivityState.text = progress
                },
                afterWorking = { result ->
                    testActivityRun1.isEnabled = true
                    testActivityState.text = result
                },
                onFailed = { throwable ->
                    testActivityRun1.isEnabled = true
                    testActivityState.text = "error: ${throwable.message}"
                    if (Log.isLoggable(TAG, Log.ERROR)) {
                        Log.e(TAG, throwable.message, throwable)
                    }
                }
            )
        }
        testActivityRun2.setOnClickListener {
            TODO("動作確認をする。特に一度キャンセルして再実行したときの挙動が変だった。")
            AsyncUtility.runAasynchronously(
                this,
                beforeWorking = {
                    testActivityRun2.isEnabled = false
                    testActivityRun3.isEnabled = true
                    ""
                },
                worker = { _, progressReporter ->
                    progressReporter("scanning...")
                    addressResolver = object : AddressResolver(
                        File(filesDir, "requests.txt"),
                        File(filesDir, "addresses.txt")
                    ) {
                        override fun progressReporter(progress: String) {
                            testActivityState.text = progress
                        }

                        override fun onCompleted() {
                            testActivityRun2.isEnabled = true
                            testActivityRun3.isEnabled = false
                            testActivityState.text = "Completed!!"
                        }

                        override fun onCancelled() {
                            testActivityRun2.isEnabled = true
                            testActivityRun3.isEnabled = false
                            testActivityState.text = "Cancelled."
                        }

                        override fun onFailed(throwable: Throwable) {
                            testActivityRun2.isEnabled = true
                            testActivityRun3.isEnabled = false
                            testActivityState.text = "error: ${throwable.message}"
                            if (Log.isLoggable(TAG, Log.ERROR)) {
                                Log.e(TAG, throwable.message, throwable)
                            }
                        }
                    }.also {
                        it.resolveAddress(this, this)
                    }
                },
                progress = { progress ->
                    progress as String
                    testActivityState.text = progress
                },
                onFailed = { throwable ->
                    testActivityRun2.isEnabled = true
                    if (Log.isLoggable(TAG, Log.ERROR)) {
                        Log.e(TAG, throwable.message, throwable)
                    }
                }
            )
        }
        testActivityRun3.setOnClickListener {
            addressResolver?.requestToCancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (Dispatchers.IO + job).cancel()
        (Dispatchers.Default + job).cancel()
    }

    companion object {
        private val TAG = "TestActivity"
    }
}