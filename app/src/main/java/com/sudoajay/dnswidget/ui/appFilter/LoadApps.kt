package com.sudoajay.dnswidget.ui.appFilter

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.sudoajay.dnswidget.ui.appFilter.dataBase.App
import com.sudoajay.dnswidget.ui.appFilter.dataBase.AppRepository
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class LoadApps(private val context: Context, private  val appRepository: AppRepository) {
    private lateinit var packageManager: PackageManager

    suspend fun searchInstalledApps() {
        appDatabaseConfiguration(getInstalledApplication(context))
    }

    private fun getInstalledApplication(context: Context): List<ApplicationInfo> {
        packageManager = context.packageManager
        return packageManager.getInstalledApplications(0)
    }

    private suspend fun appDatabaseConfiguration(installedApplicationsInfo: List<ApplicationInfo>) {



//        Here we Just add new Install App Into Data base

        for (applicationInfo in installedApplicationsInfo) {
            val packageName = getApplicationPackageName(applicationInfo)
            if (packageName == context.packageName) continue
            if (appRepository.getCount(packageName) == 0) {
                createApp(applicationInfo)
            }
            appRepository.setUpdateInstall(
                packageName
            )
        }

        //        Here we Just add default value of install app

        appRepository.setDefaultValueInstall()

//        Here we remove Uninstall App from Data base
        appRepository.removeUninstallAppFromDB()

    }

    private suspend fun createApp(applicationInfo: ApplicationInfo) {

        val label = getApplicationLabel(applicationInfo)
        val sourceDir = getApplicationSourceDir(applicationInfo)
        val packageName = getApplicationPackageName(applicationInfo)
        val icon = getApplicationsIcon(applicationInfo)
        val installedDate = getInstalledDate(packageName)
        val systemApp = isSystemApps(applicationInfo)


        appRepository.insert(
            App(
                null,
                label,
                sourceDir,
                packageName,
                icon,
                installedDate,
                systemApp,
                !systemApp,
                isSelected = true,
                isInstalled = true
            )
        )
    }

    private fun isSystemApps(applicationInfo: ApplicationInfo): Boolean {
        return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1
    }

    private fun getApplicationLabel(applicationInfo: ApplicationInfo): String {
        return packageManager.getApplicationLabel(applicationInfo) as String
    }

    private fun getApplicationSourceDir(applicationInfo: ApplicationInfo): String {
        return applicationInfo.sourceDir
    }

    private fun getApplicationPackageName(applicationInfo: ApplicationInfo): String {
        return applicationInfo.packageName
    }

    private fun getApplicationsIcon(applicationInfo: ApplicationInfo): String {
        return try {
            applicationInfo.processName
        } catch (e: PackageManager.NameNotFoundException) {
            "defaultApplicationIcon"
        }
    }

    private fun getInstalledDate(packageName: String): String {
        val installDate: Long?
        installDate = try {
            packageManager.getPackageInfo(packageName, 0).firstInstallTime
        } catch (e: PackageManager.NameNotFoundException) {
            Calendar.getInstance().timeInMillis
        }
        return convertDateToStringFormat(Date(installDate))
    }

    private fun convertDateToStringFormat(date: Date): String {
        val pattern = "yyyy-MM-dd HH:mm:ss"

        val df: DateFormat = SimpleDateFormat(pattern, Locale.getDefault())

        return df.format(date)

    }

}