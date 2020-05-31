package com.sudoajay.dnswidget.ui.customDns.database

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.sudoajay.dnswidget.R

class DnsRepository(private val application: Application, private val dnsDao: DnsDao) {


    fun dnsListUpdate(filter: String): LiveData<List<Dns>> {
        if (filter == application.getString(R.string.filter_changes_text)) {

            //   Is Custom Dns Show
            val isCustomDns = if (application.getSharedPreferences("state", Context.MODE_PRIVATE)
                    .getBoolean(application.getString(R.string.menu_custom_dns), true)
            ) 1 else 2

            //   Is Default Dns Show
            val isDefaultDns = if (application.getSharedPreferences("state", Context.MODE_PRIVATE)
                    .getBoolean(application.getString(R.string.menu_default_dns), true)
            ) 0 else 2

            return dnsDao.getDns(isCustomDns, isDefaultDns)
        } else {
            val value = "%$filter%"
            return dnsDao.searchItem(value)

        }

    }


    suspend fun deleteRow(id: Long) {
        dnsDao.deleteRow(id)
    }

    suspend fun insert(dns: Dns) {
        dnsDao.insert(dns)
    }

    suspend fun getCount(): Int {
        return dnsDao.getCount()
    }

    suspend fun updateDns(
        id: Long,
        name: String,
        dns1: String,
        dns2: String,
        dns3: String,
        dns4: String
    ) {
        return dnsDao.updateSelectedDns(id, name, dns1, dns2, dns3, dns4)
    }
}