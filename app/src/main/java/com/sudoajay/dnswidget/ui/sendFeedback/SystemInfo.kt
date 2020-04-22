package com.sudoajay.dnswidget.ui.sendFeedback

import android.app.Activity
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import android.util.DisplayMetrics
import com.sudoajay.dnswidget.helper.Connectivity
import com.sudoajay.dnswidget.helper.FileSize
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*

class SystemInfo(private var activity: Activity) {


    fun getAppInfo(): PackageInfo {
        return activity.packageManager.getPackageInfo(
            activity.packageName, PackageManager.GET_META_DATA
        )
    }

    fun getInfo(str: String): String {
        when (str) {
            "MANUFACTURER" -> return Build.MANUFACTURER.capitalize()
            "MODEL" -> return Build.MODEL
            "PRODUCT" -> return Build.PRODUCT
            "SDK_INT" -> return Build.VERSION.SDK_INT.toString()
        }
        return ""
    }

    fun getLanguage(): String {
        return Locale.getDefault().displayLanguage
    }

    fun getHeapTotalSize(): Long {
        return Debug.getNativeHeapSize()
    }

    fun getHeapFreeSize(): Long {
        return Debug.getNativeHeapFreeSize()
    }

    fun getScreenSize(): DisplayMetrics {
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        return dm
    }

    fun createTxtFile() {
        try {
            val root = File(activity.cacheDir, "SystemInfo.txt")
            if (root.exists() && root.isFile) root.delete()
            val writer = FileWriter(root)
            writer.append("\n\n ===== System-Info =====")
            writer.append(
                "\n\n Devices : " + getInfo("MANUFACTURER") + "  " +
                        getInfo("MODEL") + " (" + getInfo("PRODUCT") + ")"
            )
            writer.append("\n Os Api Level : " + getInfo("SDK_INT"))
            writer.append("\n App Version : " + getAppInfo().versionName)
            writer.append("\n Language : " + getLanguage())
            writer.append("\n Total Heap Memory : " + FileSize.convertIt(getHeapTotalSize()))
            writer.append("\n Free Heap Memory : " + FileSize.convertIt(getHeapFreeSize()))
            writer.append(
                " \n Screen Size : " + getScreenSize().heightPixels.toString()
                        + " x " + getScreenSize().widthPixels.toString()
            )
            writer.append("\n Network Type : " + Connectivity.getNetworkProvider(activity))
            writer.flush()
            writer.close()
        } catch (e: IOException) {

        }
    }


}