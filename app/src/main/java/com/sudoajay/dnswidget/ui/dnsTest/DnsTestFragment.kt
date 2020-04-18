package com.sudoajay.dnswidget.ui.dnsTest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.sudoajay.dnswidget.R

class DnsTestFragment : Fragment() {

    private lateinit var dnsTestViewModel: DnsTestViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dnsTestViewModel =
            ViewModelProvider(this).get(DnsTestViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dns_test, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        dnsTestViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}
