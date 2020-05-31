package com.sudoajay.dnswidget.ui.customDns

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.databinding.ActivityCustomDnsBinding
import com.sudoajay.dnswidget.helper.CustomToast
import com.sudoajay.dnswidget.ui.appFilter.InsetDivider
import com.sudoajay.dnswidget.ui.customDns.database.Dns
import java.util.*


class CustomDns : AppCompatActivity(), FilterDnsBottomSheet.IsSelectedBottomSheetFragment {


    private lateinit var binding: ActivityCustomDnsBinding
    lateinit var customDnsViewModel: CustomDnsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_custom_dns)

        changeStatusBarColor()

        customDnsViewModel = ViewModelProvider(this).get(CustomDnsViewModel::class.java)
        binding.viewmodel = customDnsViewModel
        binding.lifecycleOwner = this


//        Add Reference
        reference()
    }

    /**
     * Making notification bar transparent
     */
    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun reference() {

        val bottomAppBar = binding.bottomAppBar
        setSupportActionBar(bottomAppBar)

        setRecyclerView()

//      Setup Swipe RecyclerView
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        binding.swipeRefresh.setOnRefreshListener {
            customDnsViewModel.onRefresh()
        }
//        Set On Click
        binding.filterFloatingActionButton.setOnClickListener {
            addCustomDns(null)
        }
    }

    private fun setRecyclerView() {


        val recyclerView = binding.recyclerView
        val divider = getInsetDivider()
        recyclerView.addItemDecoration(divider)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        var customDnsAdapter: CustomDnsAdapter
        customDnsViewModel.dnsList!!.observe(this, Observer {

            customDnsAdapter = CustomDnsAdapter(it, this)
            recyclerView.adapter = customDnsAdapter


            if (it.isEmpty()) CustomToast.toastIt(applicationContext, "Empty List")


            if (binding.swipeRefresh.isRefreshing)
                binding.swipeRefresh.isRefreshing = false

        })


    }

    private fun getInsetDivider(): RecyclerView.ItemDecoration {
        val dividerHeight = resources.getDimensionPixelSize(R.dimen.divider_height)
        val dividerColor = ContextCompat.getColor(applicationContext, R.color.divider)
        val marginLeft = resources.getDimensionPixelSize(R.dimen.divider_inset)
        return InsetDivider.Builder(this)
            .orientation(InsetDivider.VERTICAL_LIST)
            .dividerHeight(dividerHeight)
            .color(dividerColor)
            .insets(marginLeft, 0)
            .build()
    }

    fun addCustomDns(dns: Dns?, type:String = "None") {
        val ft = supportFragmentManager.beginTransaction()
        val addCustomDnsDialog = AddCustomDnsDialog(customDnsViewModel, dns, type)
        addCustomDnsDialog.show(ft, "dialog")

    }

    fun showMoreOption(dns: Dns) {
        val addPhotoBottomDialogFragment = MoreOptionBottomSheet(this,dns)
        addPhotoBottomDialogFragment.show(
            supportFragmentManager.beginTransaction(),
            "ActionBottomDialog"
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()

            R.id.bottomToolbar_settings -> {
                val filterBottomSheet = FilterDnsBottomSheet()
                filterBottomSheet.show(supportFragmentManager, filterBottomSheet.tag)

            }
        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottom_toolbar_menu, menu)
        val actionSearch = menu.findItem(R.id.bottomToolbar_search)
        manageSearch(actionSearch)
        return super.onCreateOptionsMenu(menu)
    }

    private fun manageSearch(searchItem: MenuItem) {
        val searchView =
            searchItem.actionView as SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH
        manageFabOnSearchItemStatus(searchItem)
        manageInputTextInSearchView(searchView)
    }

    private fun manageFabOnSearchItemStatus(searchItem: MenuItem) {
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                binding.filterFloatingActionButton.hide()
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                binding.filterFloatingActionButton.show()
                return true
            }
        })
    }

    private fun manageInputTextInSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val query: String = newText.toLowerCase(Locale.ROOT).trim { it <= ' ' }
                customDnsViewModel.filterChanges(query)
                return true
            }
        })
    }

    override fun handleDialogClose() {
        customDnsViewModel.filterChanges()
    }


}