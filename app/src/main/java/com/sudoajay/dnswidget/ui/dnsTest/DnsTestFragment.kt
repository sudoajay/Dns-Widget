package com.sudoajay.dnswidget.ui.dnsTest

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.activity.BaseActivity
import com.sudoajay.dnswidget.databinding.FragmentDnsTestBinding
import com.sudoajay.dnswidget.helper.CustomToast
import com.sudoajay.dnswidget.ui.appFilter.InsetDivider

class DnsTestFragment : Fragment() {

    private lateinit var dnsTestViewModel: DnsTestViewModel
    private lateinit var binding: FragmentDnsTestBinding
    private lateinit var isDarkTheme: String

    @SuppressLint("InflateParams")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myDrawerView = layoutInflater.inflate(R.layout.fragment_dns_test, null)
        binding = FragmentDnsTestBinding.inflate(layoutInflater, myDrawerView as ViewGroup, false)

        dnsTestViewModel =
            ViewModelProvider(this).get(DnsTestViewModel::class.java)
        binding.viewModel = dnsTestViewModel
        binding.lifecycleOwner = this

        reference()

        return binding.root
    }

    private fun reference() {
        isDarkTheme = BaseActivity.getDarkMode(requireContext())

        setRecyclerView()


        binding.swipeRefresh.setColorSchemeResources(
            if (isDarkTheme == getString(
                    R.string.off_text
                )
            ) R.color.colorPrimary else R.color.colorAccent_DarkTheme
        )
        binding.swipeRefresh.isEnabled = false
        binding.swipeRefresh.setOnRefreshListener {
            dnsTestViewModel.onRefresh()
            binding.swipeRefresh.isEnabled = false


        }

        binding.refreshFloatingActionButton.setOnClickListener {
            dnsTestViewModel.onRefresh()
        }

    }


    private fun setRecyclerView() {
        var dnsSpeedTestAdapter: DnsSpeedTestAdapter

        val recyclerView = binding.recyclerView
        val divider = getInsetDivider()
        recyclerView.addItemDecoration(divider)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        dnsTestViewModel.show!!.observe(viewLifecycleOwner, Observer {

            if (it == "recyclerList" || it == "progressRecyclerList") {
                if (it == "recyclerList") binding.swipeRefresh.isEnabled = true
                if (dnsTestViewModel.dnsList.isEmpty()) CustomToast.toastIt(
                    requireContext(),
                    "Empty List"
                )
                dnsSpeedTestAdapter =
                    DnsSpeedTestAdapter(
                        requireContext(),
                        dnsTestViewModel.dnsList, dnsTestViewModel.msList

                    )
                recyclerView.adapter = dnsSpeedTestAdapter

                if (binding.swipeRefresh.isRefreshing)
                    binding.swipeRefresh.isRefreshing = false
            }
        })


    }

    private fun getInsetDivider(): RecyclerView.ItemDecoration {
        val dividerHeight = resources.getDimensionPixelSize(R.dimen.divider_height)
        val dividerColor = ContextCompat.getColor(
            requireContext(),
            if (isDarkTheme == getString(R.string.off_text)) R.color.bgWhiteColor else R.color.colorPrimary_DarkTheme
        )
        val marginLeft = resources.getDimensionPixelSize(R.dimen.divider_inset)
        return InsetDivider.Builder(requireContext())
            .orientation(InsetDivider.VERTICAL_LIST)
            .dividerHeight(dividerHeight)
            .color(dividerColor)
            .insets(marginLeft, 0)
            .build()
    }

}



