package com.sudoajay.dnswidget.ui.appFilter.dataBase

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.paging.toLiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import com.sudoajay.dnswidget.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AppRepository(private val application: Application, private val appDao: AppDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    lateinit var app: DataSource.Factory<Int, App>
    lateinit var id: List<Int>
    private val webBrowserPackageNames: MutableList<String> =
        ArrayList()

    fun handleFilterChanges(filter: String): LiveData<PagedList<App>> {
        if (filter == application.getString(R.string.filter_changes_text)) {
            //         Sorting Data in Alpha or Install date
            val getOrderBy =
                application.getSharedPreferences("state", Context.MODE_PRIVATE).getString(
                    application.getString(R.string.title_menu_order_by),
                    application.getString(R.string.menu_alphabetical_order)
                )

//         Is System App Show
            val isSystemApp = if (application.getSharedPreferences("state", Context.MODE_PRIVATE)
                    .getBoolean(application.getString(R.string.menu_system_apps), true)
            ) 1 else 2
//         Is User App Show

            val isUserApp = if (application.getSharedPreferences("state", Context.MODE_PRIVATE)
                    .getBoolean(application.getString(R.string.menu_user_apps), true)
            ) 1 else 2

//         get Changes If the Selected App
            modifyDatabase()

            app = if (getOrderBy!! == application.getString(R.string.menu_alphabetical_order)) {
                appDao.getSortByAlpha(isSystemApp, isUserApp)
            } else {
                appDao.getSortByDate(isSystemApp, isUserApp)
            }
            return app.toLiveData(
                PagedList.Config.Builder()
                    .setPageSize(20) //
                    .setInitialLoadSizeHint(20) //
                    .setEnablePlaceholders(false) //
                    .build()
            )
        } else {

            val value = "%$filter%"


            return appDao.searchItem(value).toLiveData(
                PagedList.Config.Builder()
                    .setPageSize(20) //
                    .setInitialLoadSizeHint(20) //
                    .setEnablePlaceholders(false) //
                    .build()
            )

        }
    }

    private fun modifyDatabase() {

        //        Option Selected
        val selectedOption =
            application.getSharedPreferences("state", Context.MODE_PRIVATE).getString(
                application.getString(R.string.title_menu_select_option),
                application.getString(R.string.menu_custom_app)
            ).toString()

        when (selectedOption) {
            application.getString(R.string.menu_no_apps) ->
                getId(1, SimpleSQLiteQuery("Select id From AppTable Where Selected = '1'"))
            application.getString(R.string.menu_all_apps) ->
                getId(2, SimpleSQLiteQuery("Select id From AppTable Where Selected = '0'"))
            application.getString(R.string.menu_only_user_apps) ->
                getId(3, SimpleSQLiteQuery("Select id From AppTable Where User_App = '1'"))
            application.getString(R.string.menu_only_system_apps) ->
                getId(4, SimpleSQLiteQuery("Select id From AppTable Where System_App = '1'"))
            application.getString(R.string.menu_system_app_except_browser) -> {
                getAppExceptBrowser()
                getId(5, SimpleSQLiteQuery(""))
            }
        }
    }

    private fun getAppExceptBrowser() {

        val resolveInfoList: List<ResolveInfo> =
            application.packageManager.queryIntentActivities(newBrowserIntent(), 0)
        for (resolveInfo in resolveInfoList) {
            webBrowserPackageNames.add(resolveInfo.activityInfo.packageName)
        }
        webBrowserPackageNames.add("com.google.android.webview")
        webBrowserPackageNames.add("com.android.htmlviewer")
        webBrowserPackageNames.add("com.google.android.backuptransport")
        webBrowserPackageNames.add("com.google.android.gms")
        webBrowserPackageNames.add("com.google.android.gsf")
    }

    private fun newBrowserIntent(): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://isabrowser.dns66.jak-linux.org/")
        return intent
    }

    private fun getId(type: Int, query: SimpleSQLiteQuery) {

        var value: Boolean
        CoroutineScope(Dispatchers.Default).launch {
            value = when (type) {
                1 -> true
                3, 4, 5 -> {
                    withContext(Dispatchers.Default) {
                        id =
                            appDao.getIdViaQuery(SimpleSQLiteQuery("Select id From AppTable Where Selected = '1'"))
                        updateTheList(false, id)
                    }
                    false
                }
                else -> false
            }

            withContext(Dispatchers.Default) {
                id = if (type != 5) {
                    appDao.getIdViaQuery(query)
                } else {
                    appDao.getIdFromArray(webBrowserPackageNames)
                }

            }
            withContext(Dispatchers.Default) {
                updateTheList(!value, id)
            }

        }
    }

    private suspend fun updateTheList(value: Boolean, id: List<Int>) {
        for (i in id) {
            appDao.updateSelectedById(value, i)
        }
    }

    suspend fun insert(app: App) {
        appDao.insert(app)
    }


    suspend fun getCount(packageName: String): Int {
        return appDao.getCount(packageName)
    }
    suspend fun setUpdateInstall(packageName: String) {
        appDao.updateInstalledByPackage(packageName)
    }

    suspend fun setDefaultValueInstall(){
        appDao.setDefaultValueInstall()
    }

    suspend fun removeUninstallAppFromDB(){
        for( i in appDao.getUninstallList()){
            appDao.deleteRow(i)
        }
    }
    suspend fun updateSelectedApp(selected: Boolean, packageName: String){
        appDao.updateSelectedApp(selected, packageName)
    }
}