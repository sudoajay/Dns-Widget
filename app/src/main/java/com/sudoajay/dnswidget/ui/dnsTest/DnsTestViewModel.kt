package com.sudoajay.dnswidget.ui.dnsTest

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.ui.customDns.database.Dns
import com.sudoajay.dnswidget.ui.customDns.database.DnsRepository
import com.sudoajay.dnswidget.ui.customDns.database.DnsRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DnsTestViewModel(application: Application) : AndroidViewModel(application) {


    var dnsRepository: DnsRepository
    private var _application: Application = application
    private var dnsDao = DnsRoomDatabase.getDatabase(application).dnsDao()
    var dnsList: List<Dns> = ArrayList()
    val msList: MutableList<Long> = mutableListOf()
    var show: MutableLiveData<String>? = null


    init {
        //        Creating Object and Initialization
        dnsRepository = DnsRepository(application, dnsDao)
        getShow()
    }

    fun runThread() {
        show!!.value = "Progress"
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                dnsList = dnsRepository.getList()
            }
            withContext(Dispatchers.IO) {

                for (dns in dnsList) {
                    msList.add(PingInfo.run(getCorrectDns(dns)))
                    show!!.postValue("progressRecyclerList")
                }

            }
            show!!.postValue("recyclerList")
        }


    }

    private fun getCorrectDns(dns: Dns): String? {
        val get = _application.getString(R.string.unspecified_text)
        return when {
            dns.dns1 != get -> dns.dns1
            dns.dns2 != get -> dns.dns2
            dns.dns3 != get -> dns.dns3
            dns.dns4 != get -> dns.dns4
            else -> null
        }
    }

    fun onRefresh() {
        msList.clear()
        runThread()
    }


    private fun getShow(): LiveData<String> {
        if (show == null) {
            show = MutableLiveData()
            loadShow()
        }
        return show as MutableLiveData<String>
    }

    private fun loadShow() {
        show!!.value = "Button"
    }

}