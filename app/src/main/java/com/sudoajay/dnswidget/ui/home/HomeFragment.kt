package com.sudoajay.dnswidget.ui.home

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.databinding.FragmentHomeBinding
import com.sudoajay.dnswidget.ui.customDns.database.Dns
import com.sudoajay.dnswidget.vpnClasses.AdVpnService
import com.sudoajay.dnswidget.vpnClasses.Command
import java.io.Serializable


class HomeFragment : Fragment(), Serializable {

    private lateinit var homeViewModel: HomeViewModel
    private var requestDnsCode = 1
    private lateinit var binding: FragmentHomeBinding
    private val dnsList: MutableList<String> = arrayListOf("", "", "", "")


    // Boolean to check if our activity is bound to service or not
    var mIsBound: Boolean? = null

    var mService: AdVpnService? = null
    val TAG = "ShowSomething"


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val myDrawerView = layoutInflater.inflate(R.layout.fragment_home, null)
        binding = FragmentHomeBinding.inflate(layoutInflater, myDrawerView as ViewGroup, false)
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        reference()

        return binding.root
    }

    private fun reference() {


        homeViewModel.getDnsName().observe(viewLifecycleOwner, Observer {

            binding.materialSpinner.setItems(it)
            Log.e(TAG, it.size.toString())
            if (it.isNotEmpty()) {
                val index = 0
                binding.materialSpinner.selectedIndex =index
                addItem(index)
                if (isVisibleDNSv6()) {
                    binding.dns1TextInputLayout.editText!!.setText(homeViewModel.dnsList[index].dns1)
                    binding.dns2TextInputLayout.editText!!.setText(homeViewModel.dnsList[index].dns2)
                    binding.dns3TextInputLayout.editText!!.setText(homeViewModel.dnsList[index].dns3)
                    binding.dns4TextInputLayout.editText!!.setText(homeViewModel.dnsList[index].dns4)
                } else {
                    binding.dns1TextInputLayout.editText!!.setText(homeViewModel.dnsList[index].dns1)
                    binding.dns3TextInputLayout.editText!!.setText(homeViewModel.dnsList[index].dns2)
                }
            }


        })
        binding.materialSpinner.setOnItemSelectedListener { _, position, _, _ ->
            addItem(position)
            binding.dns1TextInputLayout.editText!!.setText(dnsList[0])
            binding.dns2TextInputLayout.editText!!.setText(dnsList[1])
            binding.dns3TextInputLayout.editText!!.setText(dnsList[2])
            binding.dns4TextInputLayout.editText!!.setText(dnsList[3])
        }


        binding.useDns4CheckBox
            .setOnCheckedChangeListener { _, isChecked ->

                if (isChecked) {
                    binding.useDns6CheckBox.isEnabled = true
                    binding.useDns6CheckBox.alpha = 1f
                    binding.useDns6TextView.alpha = 1f
                    CompoundButtonCompat.setButtonTintList(
                        binding.useDns6CheckBox, ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                        )
                    )
                    binding.dns1TextInputLayout.hint = getString(R.string.dns1_text)
                    binding.dns1TextInputLayout.editText!!.setText(dnsList[0])

                    binding.dns3TextInputLayout.hint = getString(R.string.dns3_text)
                    binding.dns3TextInputLayout.editText!!.setText(dnsList[2])

                    binding.dns2TextInputLayout.visibility = View.VISIBLE
                    binding.dns2TextInputLayout.editText!!.setText(dnsList[1])

                    binding.dns4TextInputLayout.visibility = View.VISIBLE
                    binding.dns4TextInputLayout.editText!!.setText(dnsList[3])
                } else {
                    binding.useDns6CheckBox.isEnabled = false
                    binding.useDns6CheckBox.alpha = .5f
                    binding.useDns6TextView.alpha = .5f
                    CompoundButtonCompat.setButtonTintList(
                        binding.useDns6CheckBox, ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.unCheckedColor)
                        )
                    )
                    binding.dns1TextInputLayout.hint = getString(R.string.dns3_text)
                    binding.dns1TextInputLayout.editText!!.setText(dnsList[2])

                    binding.dns3TextInputLayout.hint = getString(R.string.dns4_text)
                    binding.dns3TextInputLayout.editText!!.setText(dnsList[3])

                    binding.dns2TextInputLayout.visibility = View.GONE
                    binding.dns4TextInputLayout.visibility = View.GONE
                }

            }

        binding.useDns6CheckBox
            .setOnCheckedChangeListener { _, isChecked ->
                setVisibleDNSv6(isChecked)

                if (isChecked) {
                    binding.useDns4CheckBox.isEnabled = true
                    binding.useDns4CheckBox.alpha = 1f
                    binding.useDns4TextView.alpha = 1f
                    CompoundButtonCompat.setButtonTintList(
                        binding.useDns4CheckBox, ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                        )
                    )
                    binding.dns3TextInputLayout.hint = getString(R.string.dns3_text)
                    binding.dns3TextInputLayout.editText!!.setText(dnsList[2])

                    binding.dns2TextInputLayout.visibility = View.VISIBLE
                    binding.dns2TextInputLayout.editText!!.setText(dnsList[1])

                    binding.dns4TextInputLayout.visibility = View.VISIBLE
                    binding.dns4TextInputLayout.editText!!.setText(dnsList[3])

                } else {
                    binding.useDns4CheckBox.isEnabled = false
                    binding.useDns4CheckBox.alpha = .5f
                    binding.useDns4TextView.alpha = .5f
                    CompoundButtonCompat.setButtonTintList(
                        binding.useDns4CheckBox, ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.unCheckedColor)
                        )
                    )

                    binding.dns3TextInputLayout.hint = getString(R.string.dns2_text)
                    binding.dns3TextInputLayout.editText!!.setText(dnsList[1])

                    binding.dns2TextInputLayout.visibility = View.GONE
                    binding.dns4TextInputLayout.visibility = View.GONE
                }

            }


        binding.useDns6CheckBox.isChecked = isVisibleDNSv6()

        binding.customDnsButton.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_open_custom_dns)
        }

        binding.dnsTestButton.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_open_dns_test)
        }


        binding.connectDnsButton.setOnClickListener {

            Log.e(
                TAG,
                binding.materialSpinner.selectedIndex.toString() + " --- " + homeViewModel.dnsList[binding.materialSpinner.selectedIndex].dnsName
            )
            saveSelectedDnsInfo(homeViewModel.dnsList[binding.materialSpinner.selectedIndex])

            Log.i(TAG, "Attempting to connect")
            val intent = VpnService.prepare(requireContext())
            if (intent != null) {
                Log.i(TAG, "Intent Not  Null ")
                startActivityForResult(intent, requestDnsCode)
            } else {
                Log.i(TAG, "Intent Null ")
                onActivityResult(requestDnsCode, Activity.RESULT_OK, null)
            }
        }
    }

    private fun saveSelectedDnsInfo(dns: Dns) {
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putLong("id", dns.id!!).apply()
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putString("dnsName", dns.dnsName).apply()
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putString("dns1", dns.dns1).apply()
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putString("dns2", dns.dns2).apply()
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putString("dns3", dns.dns3).apply()
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putString("dns4", dns.dns4).apply()
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putString("filter", dns.filter).apply()
    }

    private fun isVisibleDNSv6(): Boolean {
        return requireContext().getSharedPreferences("state", Context.MODE_PRIVATE)
            .getBoolean("isDnsV6", false)
    }

    private fun setVisibleDNSv6(value: Boolean) {
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putBoolean("isDnsV6", value).apply()
    }


    private fun addItem(position: Int) {
        dnsList.clear()
        dnsList.add(homeViewModel.dnsList[position].dns1)
        dnsList.add(homeViewModel.dnsList[position].dns2)
        dnsList.add(homeViewModel.dnsList[position].dns3)
        dnsList.add(homeViewModel.dnsList[position].dns4)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestDnsCode && resultCode == Activity.RESULT_CANCELED)
            errorVpnService()
        else if (requestCode == requestDnsCode && resultCode == Activity.RESULT_OK) {


            val bindIntent = Intent(activity, AdVpnService::class.java)

            bindIntent.putExtra("COMMAND", Command.START.ordinal)

            requireActivity().bindService(bindIntent, serviceConnection, Context.BIND_ABOVE_CLIENT)
            requireContext().startService(bindIntent)
            Log.i(TAG, "$resultCode ---- Accepted ")
        }

    }


    private fun errorVpnService() {
        val builder: AlertDialog.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AlertDialog.Builder(requireContext(), android.R.style.Theme_Material_Dialog_Alert)
            } else {
                AlertDialog.Builder(requireContext())
            }
        builder.setTitle(requireContext().getString(R.string.error_vpn_text))
            .setMessage(requireContext().getString(R.string.could_not_configure_vpn_service))
            .setNegativeButton("Ok") { _, _ ->

            }
            .setIcon(R.drawable.error_icon)
            .setCancelable(true)
            .show()
    }

    /**
     * Interface for getting the instance of binder from our service class
     * So client can get instance of our service class and can directly communicate with it.
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
            Log.d(TAG, "ServiceConnection: connected to service.")
            // We've bound to MyService, cast the IBinder and get MyBinder instance
            val binder = iBinder as AdVpnService.MyBinder
            mService = binder.service
            mIsBound = true
            getStatus() // return a random number from the service
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(TAG, "ServiceConnection: disconnected from service.")
            mIsBound = false
        }
    }

    /**
     * Method for listening to random numbers generated by our service class
     */
    private fun getStatus() {
        mService!!.dnsStatus.observe(this, Observer {
            binding.statusDnsTextView.text = it

            when (it) {
                requireContext().getString(R.string.not_connected_text) ->
                    binding.connectDnsButton.text = requireContext().getString(R.string.start_text)

                requireContext().getString(R.string.connected_progress_text),
                requireContext().getString(R.string.connected_text) ->
                    binding.connectDnsButton.text = requireContext().getString(R.string.stop_text)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mIsBound != null) requireContext().unbindService(serviceConnection)
    }
}
