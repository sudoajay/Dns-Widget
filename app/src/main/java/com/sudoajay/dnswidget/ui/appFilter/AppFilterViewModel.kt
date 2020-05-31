package com.sudoajay.dnswidget.ui.appFilter

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import androidx.paging.PagedList
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.ui.appFilter.dataBase.App
import com.sudoajay.dnswidget.ui.appFilter.dataBase.AppDao
import com.sudoajay.dnswidget.ui.appFilter.dataBase.AppRepository
import com.sudoajay.dnswidget.ui.appFilter.dataBase.AppRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AppFilterViewModel(application: Application) : AndroidViewModel(application) {


    private var loadApps: LoadApps
    private var _application = application
    var appRepository: AppRepository

    private var appDao: AppDao = AppRoomDatabase.getDatabase(application, viewModelScope).appDao()


    val headingText: String = application.getString(R.string.action_app_filter)

    private var hideProgress: MutableLiveData<Boolean>? = null

    private val filterChanges: MutableLiveData<String> = MutableLiveData()


    var appList: LiveData<PagedList<App>>? = null


    init {

//        Creating Object and Initialization
        appRepository = AppRepository(_application, appDao)
        loadApps = LoadApps(application, this)

        setDefaultValue()

        appList = Transformations.switchMap(filterChanges) {
            appRepository.handleFilterChanges(it)
        }

        filterChanges()

        databaseConfiguration()

    }


    fun filterChanges(filter: String = _application.getString(R.string.filter_changes_text)) {
        filterChanges.value = filter

    }

    private fun setDefaultValue() {
        // Set Custom Apps to SharedPreferences
        _application.getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putString(
                _application.getString(R.string.title_menu_select_option),
                "Custom Apps"
            ).apply()


    }

    private fun databaseConfiguration() {
        CoroutineScope(IO).launch {
            withContext(IO) {
                loadApps.searchInstalledApps()
            }
            hideProgress!!.postValue(  false)
        }

    }

    fun onRefresh() {

        appList!!.value!!.dataSource.invalidate()
    }


    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    suspend fun insert(app: App) {
        appRepository.insert(app)
    }


    fun getHideProgress(): LiveData<Boolean> {
        if (hideProgress == null) {
            hideProgress = MutableLiveData<Boolean>()
            loadHideProgress()
        }
        return hideProgress as MutableLiveData<Boolean>
    }

    private fun loadHideProgress() {
        hideProgress!!.value = true
    }



}

