package com.sudoajay.dnswidget.ui.appFilter.dataBase

import androidx.paging.DataSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery


@Dao
interface AppDao {

    @Query("Select * From app_table Where System_App = :isSystemApp Or User_App = :isUserApp Order By name asc ")
    fun getSortByAlpha(isSystemApp: Int, isUserApp: Int): DataSource.Factory<Int, App>

    @Query("Select * From app_table Where System_App = :isSystemApp Or User_App = :isUserApp Order By Date Desc ")
    fun getSortByDate(isSystemApp: Int, isUserApp: Int): DataSource.Factory<Int, App>

    @RawQuery
    suspend fun getIdViaQuery(query: SupportSQLiteQuery): List<Int>

    @Query("Select id FROM app_table Where System_App = '1' and Package_Name Not In (:webBrowserPackageNames)")
    fun getIdFromArray(webBrowserPackageNames: MutableList<String>): List<Int>

    @Query("UPDATE app_table SET Selected = :value WHERE id = :id")
    fun updateSelectedById(value: Boolean, id: Int)


    @Query("SELECT * FROM app_table WHERE name LIKE :search")
    fun searchItem(search: String?): DataSource.Factory<Int, App>


    @Query("SELECT COUNT(id) FROM app_table WHERE Package_Name = :packageName")
    fun getCount(packageName: String): Int

    @Query("SELECT id FROM app_table WHERE Installed = '0' ")
    fun getUninstallList(): List<Int>


    @Query("UPDATE app_table SET Installed = '0' ")
    fun setDefaultValueInstall()


    @Query("UPDATE app_table SET Installed = '1' WHERE Package_Name = :packageName")
    fun updateInstalledByPackage(packageName: String)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(app: App)


    @Query("DELETE FROM app_table")
    suspend fun deleteAll()

    @Query("DELETE FROM app_table Where id = :ID")
    fun deleteRow(ID: Int)



}