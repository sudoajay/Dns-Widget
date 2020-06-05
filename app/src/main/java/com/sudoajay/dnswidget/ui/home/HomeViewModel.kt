package com.sudoajay.dnswidget.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sudoajay.dnswidget.ui.customDns.database.Dns
import com.sudoajay.dnswidget.ui.customDns.database.DnsRepository
import com.sudoajay.dnswidget.ui.customDns.database.DnsRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    var dnsList: List<Dns> = listOf()
    private var dnsName: MutableLiveData<List<String>>? = null
    private var _application = application
    var dnsRepository: DnsRepository

    private var dnsDao = DnsRoomDatabase.getDatabase(application, viewModelScope).dnsDao()

    init {
        //        Creating Object and Initialization
        dnsRepository = DnsRepository(application, dnsDao)

        // dbms setup
        fetchData()
    }

    private fun fetchData() {
        val dnsNameList: MutableList<String> = mutableListOf()
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                dnsList = dnsRepository.getDnsList()
            }

            for (dns in dnsList) {
                dnsNameList.add(dns.dnsName)
            }

            dnsName!!.postValue(dnsNameList)
        }
    }


    fun getDnsName(): LiveData<List<String>> {
        if (dnsName == null) {
            dnsName = MutableLiveData<List<String>>()
            loadDnsName()
        }
        return dnsName as MutableLiveData<List<String>>
    }

    private fun loadDnsName() {
        dnsName!!.value = listOf()
    }


}