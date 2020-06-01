package com.sudoajay.dnswidget.ui.dnsTest

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.databinding.LayoutDnsSpeedTestItemBinding
import com.sudoajay.dnswidget.ui.customDns.database.Dns


class DnsSpeedTestAdapter(
    private var context: Context,
    private val items: List<Dns>,
    var msList: MutableList<String>
) :
    RecyclerView.Adapter<DnsSpeedTestAdapter.MyViewHolder>() {


    class MyViewHolder(
        binding: LayoutDnsSpeedTestItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        var dnsNameTextView = binding.dnsNameTextView
        var dns1TextView = binding.dns1TextView
        var dns2TextView = binding.dns2TextView
        var msTextView = binding.msTextView


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: LayoutDnsSpeedTestItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_dns_speed_test_item, parent, false
        )
        return MyViewHolder(binding)


    }

    override fun getItemCount(): Int {
        return msList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dns = items[position]
        holder.dnsNameTextView.text =
            if (dns.filter == "None") dns.dnsName else dns.dnsName + " (" + dns.filter + ")"

        holder.dns1TextView.text = dns.dns1
        holder.dns2TextView.text = dns.dns2


        val ms = msList[position].toInt()


        when (ms) {
            in 80 downTo 0 -> holder.msTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.ping1
                )
            )
            in 200 downTo 80 -> holder.msTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.ping2
                )
            )
            else -> holder.msTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.ping3
                )
            )
        }

        if (ms == -1) holder.msTextView.text = "Error"
        else holder.msTextView.text = "$ms ms"


    }


}


