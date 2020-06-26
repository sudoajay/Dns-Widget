package com.sudoajay.dnswidget.ui.appFilter

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.sudoajay.dnswidget.activity.BaseActivity
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.databinding.ActivityAppFilterBinding
import com.sudoajay.dnswidget.helper.CustomToast
import java.util.*


class AppFilter : BaseActivity(), FilterDnsBottomSheet.IsSelectedBottomSheetFragment {

    lateinit var appFilterViewModel: AppFilterViewModel
    private lateinit var binding: ActivityAppFilterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_filter)

        changeStatusBarColor()

        appFilterViewModel = ViewModelProvider(this).get(AppFilterViewModel::class.java)
        binding.viewmodel = appFilterViewModel
        binding.lifecycleOwner = this

        reference()

    }


    private fun reference() {

        setRecyclerView()
        binding.swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        binding.swipeRefresh.setOnRefreshListener {
            appFilterViewModel.onRefresh()
        }

        binding.filterFloatingActionButton.setOnClickListener {
            val bottomSheetFragment = FilterDnsBottomSheet()
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }

    }

    private fun setRecyclerView() {
        val bottomAppBar = binding.bottomAppBar
        setSupportActionBar(bottomAppBar)

        val recyclerView = binding.recyclerView
        val divider = getInsetDivider()
        recyclerView.addItemDecoration(divider)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val pagingAppRecyclerAdapter = PagingAppRecyclerAdapter(applicationContext, this)

        appFilterViewModel.appList!!.observe(this, Observer {

            pagingAppRecyclerAdapter.submitList(it)
            recyclerView.adapter = pagingAppRecyclerAdapter
            if (binding.swipeRefresh.isRefreshing )
                binding.swipeRefresh.isRefreshing = false

            if (it.isEmpty()) CustomToast.toastIt(applicationContext, "Empty List")

        })


    }

    private fun getInsetDivider(): ItemDecoration {
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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()

            R.id.bottomToolbar_settings -> {
                val moreFilterBottomSheet = MoreFilterAppBottomSheet()
                moreFilterBottomSheet.show(supportFragmentManager, moreFilterBottomSheet.tag)
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
                appFilterViewModel.filterChanges(query)
                return true
            }
        })
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


    override fun handleDialogClose() {
        appFilterViewModel.filterChanges()
    }

    override fun onPause() {
        super.onPause()



    }


}
