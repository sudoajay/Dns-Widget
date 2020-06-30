package com.sudoajay.dnswidget.ui.dnsTest

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.activity.BaseActivity
import com.sudoajay.dnswidget.activity.MainActivity
import com.sudoajay.dnswidget.databinding.LayoutDnsSpeedTestItemBinding
import com.sudoajay.dnswidget.ui.customDns.database.Dns
import com.sudoajay.dnswidget.ui.home.HomeFragment


class DnsSpeedTestAdapter(
    private var context: Context,
    private val dnsList: List<Dns>,
    private val msList: MutableList<Long>

) :
    RecyclerView.Adapter<DnsSpeedTestAdapter.MyViewHolder>() {

    private lateinit var isDarkTheme: String


    class MyViewHolder(
        binding: LayoutDnsSpeedTestItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        var dnsNameTextView = binding.dnsNameTextView
        var dns1TextView = binding.dns1TextView
        var dns2TextView = binding.dns2TextView
        var msTextView = binding.msTextView
        var useItButton = binding.useItButton


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding: LayoutDnsSpeedTestItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.layout_dns_speed_test_item, parent, false
        )
        isDarkTheme = BaseActivity.getDarkMode(context)
        return MyViewHolder(binding)


    }

    override fun getItemCount(): Int {
        return msList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val dns = dnsList[position]
        holder.dnsNameTextView.text =
            if (dns.filter == "None") dns.dnsName else dns.dnsName + " (" + dns.filter + ")"

        holder.dns1TextView.text = dns.dns1
        holder.dns2TextView.text = dns.dns2
        holder.useItButton.setOnClickListener {

            HomeFragment.saveSelectedDnsInfo(context = context, id = dns.id!!)

            val intent = Intent(context, MainActivity::class.java)
            intent.action = MainActivity.homeShortcutId
            context.startActivity(intent)

        }

        val ms = msList[position].toInt()


        when (ms) {
            in 80 downTo 0 -> holder.msTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isDarkTheme == context.getString(
                            R.string.off_text
                        )
                    ) R.color.ping1 else R.color.ping1_DarkTheme
                )
            )
            in 200 downTo 80 -> holder.msTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isDarkTheme == context.getString(
                            R.string.off_text
                        )
                    ) R.color.ping2 else R.color.ping2_DarkTheme
                )
            )
            else -> holder.msTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (isDarkTheme == context.getString(
                            R.string.off_text
                        )
                    ) R.color.ping3 else R.color.ping3_DarkTheme
                )
            )
        }

        if (ms == -1) holder.msTextView.text = "Error"
        else holder.msTextView.text = msList[position].toString() + " ms"


    }


}


