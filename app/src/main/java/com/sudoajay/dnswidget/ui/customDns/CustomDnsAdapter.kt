package com.sudoajay.dnswidget.ui.customDns

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.dnswidget.R
import kotlinx.android.synthetic.main.layout_custom_dns_item.view.*

class CustomDnsAdapter(private val items: ArrayList<String>, private val customDnsFragment:CustomDnsFragment) : RecyclerView.Adapter<CustomDnsAdapter.MyViewHolder>() {


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // each data item is just a string in this case
        val tvAnimalType: TextView = view.tv_animal_type

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(customDnsFragment.requireContext()).inflate(R.layout.layout_custom_dns_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvAnimalType.text = items[position]
        holder.tvAnimalType.setOnClickListener{
            customDnsFragment.showBottomShow()
        }
    }

}


