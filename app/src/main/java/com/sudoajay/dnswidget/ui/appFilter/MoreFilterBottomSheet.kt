package com.sudoajay.dnswidget.ui.appFilter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.databinding.LayoutFilterBottomSheetBinding
import com.sudoajay.dnswidget.databinding.LayoutMoreFilterBottomSheetBinding


class MoreFilterBottomSheet : BottomSheetDialogFragment() {

    private var binding: LayoutMoreFilterBottomSheetBinding? = null
    private var isSelectedBottomSheetFragment: FilterBottomSheet.IsSelectedBottomSheetFragment? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.layout_more_filter_bottom_sheet, container, false)
        binding!!.bottomSheet = this

        isSelectedBottomSheetFragment = activity as FilterBottomSheet.IsSelectedBottomSheetFragment?


        return binding!!.root
    }

    fun setValue(key: String, value: String) {
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putString(key, value).apply()
        isSelectedBottomSheetFragment!!.handleDialogClose()

        dismiss()
    }



}