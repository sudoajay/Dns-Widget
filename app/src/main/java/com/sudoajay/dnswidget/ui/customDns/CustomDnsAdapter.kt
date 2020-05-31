package com.sudoajay.dnswidget.ui.customDns

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.databinding.LayoutCustomDnsItemBinding
import com.sudoajay.dnswidget.ui.customDns.database.Dns


class CustomDnsAdapter(private val items: List<Dns> , private var customDns: CustomDns) :
    RecyclerView.Adapter<CustomDnsAdapter.MyViewHolder>() {


    class MyViewHolder(
       layoutCustomDnsItemBinding: LayoutCustomDnsItemBinding
    ) :
        RecyclerView.ViewHolder(layoutCustomDnsItemBinding.root) {

        var dnsNameTextView = layoutCustomDnsItemBinding.dnsNameTextView
        var dnsBox = layoutCustomDnsItemBinding.dnsConstraintLayout


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: LayoutCustomDnsItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_custom_dns_item, parent, false
        )
        return MyViewHolder(binding)


    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dns = items[position]
        holder.dnsNameTextView.text =
            if (dns.filter == "None") dns.dnsName else dns.dnsName + " (" + dns.filter + ")"

        holder.dnsBox.setOnClickListener {
            customDns.showMoreOption(dns)
        }


    }



}


