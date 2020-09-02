package com.sudoajay.dnswidget.ui.customDns.database

import android.content.Context
import androidx.lifecycle.LiveData
import com.sudoajay.dnswidget.R

class DnsRepository(private val context: Context, private val dnsDao: DnsDao) {


    fun dnsListUpdate(filter: String): LiveData<List<Dns>> {
        if (filter == context.getString(R.string.filter_changes_text_trans)) {

            //   Is Custom Dns Show
            val isCustomDns = if (context.getSharedPreferences("state", Context.MODE_PRIVATE)
                    .getBoolean(context.getString(R.string.menu_custom_dns), true)
            ) 1 else 2

            //   Is Default Dns Show
            val isDefaultDns = if (context.getSharedPreferences("state", Context.MODE_PRIVATE)
                    .getBoolean(context.getString(R.string.menu_default_dns), true)
            ) 0 else 2

            return dnsDao.getDnsByOption(isCustomDns, isDefaultDns, context.getString(R.string.custom_dns_enter_manually_text))
        } else {
            val value = "%$filter%"
            return dnsDao.searchItem(value, context.getString(R.string.custom_dns_enter_manually_text))

        }

    }

    suspend fun getDnsList(): List<Dns>{
        return dnsDao.getDns()
    }

    suspend fun getList(): List<Dns> {
        return dnsDao.getList(context.getString(R.string.custom_dns_enter_manually_text))
    }


    suspend fun deleteRowFromId(id: Long) {
        dnsDao.deleteRowFromId(id)
    }

    suspend fun insert(dns: Dns) {
        dnsDao.insert(dns)
    }

    suspend fun getCount(): Int {
        return dnsDao.getCount()
    }

    suspend fun getCustomCount(): Int{
        return dnsDao.getCustomCount()
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

    suspend fun getDnsFromId(id: Long): Dns {
        return dnsDao.getDnsFromId(id)
    }


}