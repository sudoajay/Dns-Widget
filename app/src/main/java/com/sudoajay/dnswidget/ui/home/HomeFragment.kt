package com.sudoajay.dnswidget.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputLayout
import com.jaredrummler.materialspinner.MaterialSpinner
import com.sudoajay.dnswidget.R

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var root: View


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_home, container, false)

        reference()

        return root
    }

    private fun reference() {

        val dns2TextInputLayout: TextInputLayout = root.findViewById(R.id.dns2_TextInputLayout)
        val dns3TextInputLayout: TextInputLayout = root.findViewById(R.id.dns3_TextInputLayout)
        val dns4TextInputLayout: TextInputLayout = root.findViewById(R.id.dns4_TextInputLayout)
        val materialSpinner :MaterialSpinner = root.findViewById(R.id.materialSpinner)
        materialSpinner.setItems(homeViewModel.getItemsSpinner())


        root.findViewById<CheckBox>(R.id.enableIpv6_checkBox)
            .setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    dns3TextInputLayout.hint = getString(R.string.dns3_text)
                    dns2TextInputLayout.visibility = View.VISIBLE
                    dns4TextInputLayout.visibility = View.VISIBLE
                } else {
                    dns3TextInputLayout.hint = getString(R.string.dns2_text)
                    dns2TextInputLayout.visibility = View.GONE
                    dns4TextInputLayout.visibility = View.GONE
                }

            }

        root.findViewById<Button>(R.id.customDns_Button).setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_open_custom_dns)
        }

        root.findViewById<Button>(R.id.dnsTest_Button).setOnClickListener {
            Navigation.findNavController(root).navigate(R.id.action_open_dns_test)
        }


    }


}
