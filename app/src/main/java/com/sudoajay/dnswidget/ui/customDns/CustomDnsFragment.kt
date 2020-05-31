package com.sudoajay.dnswidget.ui.customDns

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sudoajay.dnswidget.R

class CustomDnsFragment : Fragment() {

    private lateinit var customDnsViewModel: CustomDnsViewModel
    private lateinit var root: View

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        customDnsViewModel =
            ViewModelProvider(this).get(CustomDnsViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_custom_dns, container, false)

        reference()

        return root
    }

    private fun reference() {

        root.findViewById<FloatingActionButton>(R.id.addCustomDns_FloatingActionButton).setOnClickListener {
            callCustomDns()
        }
        val recycleView: RecyclerView = root.findViewById(R.id.recycler_view)

//
//        // Loads animals into the ArrayList
//        customDnsViewModel.addAnimals()
//
//        if (customDnsViewModel.animals.isEmpty()) {
//            recycleView.visibility = View.GONE
//            root.findViewById<ImageView>(R.id.empty_imageView).visibility = View.VISIBLE
//        }
//
//        // Creates a vertical Layout Manager
//        recycleView.layoutManager = LinearLayoutManager(requireContext())
//
//        // Access the RecyclerView Adapter and load the data into it
//        recycleView.adapter = CustomDnsAdapter(customDnsViewModel.animals, this)
    }

    private fun callCustomDns() {
        val ft = requireActivity().supportFragmentManager.beginTransaction()
        val addCustomDnsDialog = AddCustomDnsDialog(customDnsViewModel)
        addCustomDnsDialog.show(ft, "dialog")
    }



}
