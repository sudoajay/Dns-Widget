package com.sudoajay.dnswidget.ui.customDns

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.databinding.LayoutFilterDnsBottomSheetBinding
import com.sudoajay.dnswidget.helper.CustomToast


class FilterDnsBottomSheet : BottomSheetDialogFragment() {

    private var isSelectedBottomSheetFragment: IsSelectedBottomSheetFragment? = null

    interface IsSelectedBottomSheetFragment {
        fun handleDialogClose()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val myDrawerView = layoutInflater.inflate(R.layout.layout_filter_dns_bottom_sheet, null)
        val binding = LayoutFilterDnsBottomSheetBinding.inflate(layoutInflater, myDrawerView as ViewGroup, false)
        binding.bottomSheet = this


        isSelectedBottomSheetFragment = activity as IsSelectedBottomSheetFragment?

        return binding.root
    }


    fun isVisible(key: String): Boolean {
        return requireContext().getSharedPreferences("state", Context.MODE_PRIVATE)
            .getBoolean(key, true)
    }

    private fun setVisible(key: String) {
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putBoolean(key, !isVisible(key)).apply()
    }

    fun setUpVisible(key: String) {
        setVisible(key)

        if (!isVisible(getString(R.string.menu_default_dns_trans)) && !isVisible(getString(R.string.menu_custom_dns_trans))) {
            CustomToast.toastIt(requireContext(), getString(R.string.at_least_one_item_text))
            setVisible(key)
        } else {
            isSelectedBottomSheetFragment!!.handleDialogClose()
            dismiss()
        }
    }
}

