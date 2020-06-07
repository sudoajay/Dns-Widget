package com.sudoajay.dnswidget.helper

import android.net.TrafficStats
import android.util.Log
import java.util.*

class ConnectivitySpeed {

    companion object {
        private const val GB: Long = 1000000000
        private const val MB: Long = 1000000
        private const val KB: Long = 1000
    }

    fun getNetworkSpeed(): String {

        var downloadSpeedOutput = ""
        var units = ""
        val mBytesPrevious = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes()

        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        val mBytesCurrent = TrafficStats.getTotalRxBytes() + TrafficStats.getTotalTxBytes()

        val mNetworkSpeed = mBytesCurrent - mBytesPrevious

        val mDownloadSpeedWithDecimals: Float

        when {
            mNetworkSpeed >= GB -> {
                mDownloadSpeedWithDecimals = mNetworkSpeed.toFloat() / GB.toFloat()
                units = " GB"
            }
            mNetworkSpeed >= MB -> {
                mDownloadSpeedWithDecimals = mNetworkSpeed.toFloat() / MB.toFloat()
                units = " MB"

            }
            else -> {
                mDownloadSpeedWithDecimals = mNetworkSpeed.toFloat() / KB.toFloat()
                units = " KB"
            }
        }


        downloadSpeedOutput = if (units != " KB" && mDownloadSpeedWithDecimals < 100) {
            String.format(Locale.US, "%.1f", mDownloadSpeedWithDecimals)
        } else {
            mDownloadSpeedWithDecimals.toInt().toString()
        }
        downloadSpeedOutput = (downloadSpeedOutput + units)
        val speed : String = (downloadSpeedOutput.subSequence(0, downloadSpeedOutput.indexOf(" ")+1)).toString()
        val unit : String  = (downloadSpeedOutput.subSequence(downloadSpeedOutput.indexOf(" ")+1,downloadSpeedOutput.length)).toString()


        return getMetricData(convertToBytes(speed.toFloat(), unit))

    }

    private fun convertToBytes(value: Float, unit: String): Long {
        return when (unit) {
            "KB" -> {
                (value.toLong()) * KB
            }
            "MB" -> {
                (value.toLong()) * MB
            }
            "GB" -> {
                (value.toLong()) * GB
            }
            else -> 0
        }
    }

    private fun getMetricData(bytes: Long): String {
        val dataWithDecimals: Float
        val units: String
        when {
            bytes >= GB -> {
                dataWithDecimals = bytes.toFloat() / GB.toFloat()
                units = " GB"
            }
            bytes >= MB -> {
                dataWithDecimals = bytes.toFloat() / MB.toFloat()
                units = " MB"

            }
            else -> {
                dataWithDecimals = bytes.toFloat() / KB.toFloat()
                units = " KB"
            }
        }


        val output = if (units != " KB" && dataWithDecimals < 100) {
            String.format(Locale.US, "%.1f", dataWithDecimals)
        } else {
            dataWithDecimals.toInt().toString()
        }

        Log.e("ShowSomething", output + units)
        return output + units
    }


}