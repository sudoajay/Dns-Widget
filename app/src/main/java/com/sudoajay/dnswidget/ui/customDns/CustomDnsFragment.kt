package com.sudoajay.dnswidget.ui.customDns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sudoajay.dnswidget.R

class CustomDnsFragment : Fragment() {

    private lateinit var customDnsViewModel: CustomDnsViewModel
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        customDnsViewModel =
            ViewModelProvider(this).get(CustomDnsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_custom_dns, container, false)

        root.findViewById<FloatingActionButton>(R.id.addCustomDns_FloatingActionButton).setOnClickListener {
            callCustomDns()
        }

        return root
    }

    private fun callCustomDns() {
        val ft = requireActivity().supportFragmentManager.beginTransaction()
        val addCustomDnsDialog = AddCustomDnsDialog()
        addCustomDnsDialog.show(ft, "dialog")
    }
}
