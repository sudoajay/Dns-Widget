package com.sudoajay.dnswidget.ui.customDns

import android.app.Application
import androidx.lifecycle.*
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.ui.customDns.database.Dns
import com.sudoajay.dnswidget.ui.customDns.database.DnsRepository
import com.sudoajay.dnswidget.ui.customDns.database.DnsRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CustomDnsViewModel(application: Application) : AndroidViewModel(application) {

    private var _application = application
    val headingText: String = application.getString(R.string.action_custom_dns)
    var dnsRepository: DnsRepository

    private var dnsDao = DnsRoomDatabase.getDatabase(application, viewModelScope).dnsDao()
    private var loadDns: LoadDns

    var dnsList: LiveData<List<Dns>>? = null
    private val filterChanges: MutableLiveData<String> = MutableLiveData()


    init {
        //        Creating Object and Initialization
        dnsRepository = DnsRepository(_application, dnsDao)
        loadDns = LoadDns(_application, dnsRepository)

        dnsList = Transformations.switchMap(filterChanges) {
            dnsRepository.dnsListUpdate(it)
        }

        databaseConfiguration()

        filterChanges()
    }

    private fun databaseConfiguration() {
        CoroutineScope(Dispatchers.IO).launch {
            if (dnsRepository.getCount() == 0)
                loadDns.fillDefaultData()
        }
    }

    fun filterChanges(filter: String = _application.getString(R.string.filter_changes_text)) {
        filterChanges.value = filter
    }

    fun onRefresh() {
        filterChanges()
    }


}