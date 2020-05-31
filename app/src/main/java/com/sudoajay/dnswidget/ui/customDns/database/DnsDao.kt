package com.sudoajay.dnswidget.ui.customDns.database

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sudoajay.dnswidget.ui.appFilter.dataBase.App

@Dao
interface DnsDao {

    @Query("Select * FROM DnsTable Where Custom = :isCustomDns Or Custom = :isDefaultDns")
    fun getDns(isCustomDns:Int , isDefaultDns:Int): LiveData<List<Dns>>

    @Query("Select Count(*) FROM DnsTable ")
    suspend fun getCount(): Int

    @Query("SELECT * FROM DnsTable WHERE Name LIKE :search")
    fun searchItem(search: String?): LiveData<List<Dns>>


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(dns: Dns)


    @Query("DELETE FROM DnsTable")
    suspend fun deleteAll()
}