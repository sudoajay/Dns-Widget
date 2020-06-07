package com.sudoajay.dnswidget.ui.home

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.ColorStateList
import android.net.VpnService
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.textfield.TextInputLayout
import com.jaredrummler.materialspinner.MaterialSpinner
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.databinding.FragmentHomeBinding
import com.sudoajay.dnswidget.vpnClasses.AdVpnService
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {

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
            if (it.isNotEmpty()) {
                binding.materialSpinner.selectedIndex = 0
                addItem(0)
                if (isVisibleDNSv6()) {
                    binding.dns1TextInputLayout.editText!!.setText(homeViewModel.dnsList[0].dns1)
                    binding.dns2TextInputLayout.editText!!.setText(homeViewModel.dnsList[0].dns2)
                    binding.dns3TextInputLayout.editText!!.setText(homeViewModel.dnsList[0].dns3)
                    binding.dns4TextInputLayout.editText!!.setText(homeViewModel.dnsList[0].dns4)
                } else {
                    binding.dns1TextInputLayout.editText!!.setText(homeViewModel.dnsList[0].dns1)
                    binding.dns3TextInputLayout.editText!!.setText(homeViewModel.dnsList[0].dns2)
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


        binding.customDnsButton.setOnClickListener {
            Log.i(TAG, "Attempting to connect")
            val intent = VpnService.prepare(requireContext())
            if (intent != null) {
                Log.i(TAG, "Intent Null ")
                startActivityForResult(intent, requestDnsCode)
            } else {
                Log.i(TAG, "Intent Not  Null ")
                onActivityResult(requestDnsCode, Activity.RESULT_OK, null)
            }
        }
    }

    private fun isVisibleDNSv6(): Boolean {
        return requireContext().getSharedPreferences("state", Context.MODE_PRIVATE)
            .getBoolean("Dnsv6", false)
    }

    private fun setVisibleDNSv6(value: Boolean) {
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putBoolean("Dnsv6", value).apply()
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
//        if (requestCode == requestDnsCode && resultCode == Activity.RESULT_CANCELED)
//            CustomToast.toastIt(
//                requireContext(),
//                getString(R.string.could_not_configure_vpn_service)
//            )
//
//        if (requestCode == requestDnsCode && resultCode == Activity.RESULT_OK) {
//            val bindIntent = Intent(activity, AdVpnService::class.java)
//            requireActivity().bindService(bindIntent, serviceConnection, Context.BIND_ABOVE_CLIENT)
//            requireContext().startService(bindIntent)
//
//        }

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
        mService!!.dnsStatus.observe(this
            , Observer {
                statusDns_textView.text = it
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mIsBound != null) requireContext().unbindService(serviceConnection)
    }
}
