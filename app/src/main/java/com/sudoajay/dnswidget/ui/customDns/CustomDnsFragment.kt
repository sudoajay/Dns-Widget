package com.sudoajay.dnswidget.ui.customDns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
        val textView: TextView = root.findViewById(R.id.text_gallery)
        customDnsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
