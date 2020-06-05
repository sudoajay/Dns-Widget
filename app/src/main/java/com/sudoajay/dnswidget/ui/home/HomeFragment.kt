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
import com.sudoajay.dnswidget.helper.CustomToast
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
    val mTAG = "ShowSomething"


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

        val dns1TextInputLayout: TextInputLayout = binding.dns1TextInputLayout
        val dns2TextInputLayout: TextInputLayout = binding.dns2TextInputLayout
        val dns3TextInputLayout: TextInputLayout = binding.dns3TextInputLayout
        val dns4TextInputLayout: TextInputLayout = binding.dns4TextInputLayout
        val materialSpinner: MaterialSpinner = binding.materialSpinner
        val useDns4CheckBox: CheckBox = binding.useDns4CheckBox
        val useDns6CheckBox: CheckBox = binding.useDns6CheckBox
        val useDns4TextView: TextView = binding.useDns4TextView
        val useDns6TextView: TextView = binding.useDns6TextView
        val normalChangesButton: Button = binding.normalChangesButton


        homeViewModel.getDnsName().observe(viewLifecycleOwner, Observer {

            materialSpinner.setItems(it)
            if (it.isNotEmpty()) {
                materialSpinner.selectedIndex = 0
                addItem(0)
                if (isVisibleDNSv6()) {
                    dns1TextInputLayout.editText!!.setText(homeViewModel.dnsList[0].dns1)
                    dns2TextInputLayout.editText!!.setText(homeViewModel.dnsList[0].dns2)
                    dns3TextInputLayout.editText!!.setText(homeViewModel.dnsList[0].dns3)
                    dns4TextInputLayout.editText!!.setText(homeViewModel.dnsList[0].dns4)
                } else {
                    dns1TextInputLayout.editText!!.setText(homeViewModel.dnsList[0].dns1)
                    dns3TextInputLayout.editText!!.setText(homeViewModel.dnsList[0].dns2)
                }
            }


        })


        materialSpinner.setOnItemSelectedListener { _, position, _, _ ->
            addItem(position)
            dns1TextInputLayout.editText!!.setText(dnsList[0])
            dns2TextInputLayout.editText!!.setText(dnsList[1])
            dns3TextInputLayout.editText!!.setText(dnsList[2])
            dns4TextInputLayout.editText!!.setText(dnsList[3])
        }



        useDns4CheckBox
            .setOnCheckedChangeListener { _, isChecked ->

                if (isChecked) {
                    useDns6CheckBox.isEnabled = true
                    useDns6CheckBox.alpha = 1f
                    useDns6TextView.alpha = 1f
                    CompoundButtonCompat.setButtonTintList(
                        useDns6CheckBox, ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                        )
                    )
                    dns1TextInputLayout.hint = getString(R.string.dns1_text)
                    dns1TextInputLayout.editText!!.setText(dnsList[0])

                    dns3TextInputLayout.hint = getString(R.string.dns3_text)
                    dns3TextInputLayout.editText!!.setText(dnsList[2])

                    dns2TextInputLayout.visibility = View.VISIBLE
                    dns2TextInputLayout.editText!!.setText(dnsList[1])

                    dns4TextInputLayout.visibility = View.VISIBLE
                    dns4TextInputLayout.editText!!.setText(dnsList[3])
                } else {
                    useDns6CheckBox.isEnabled = false
                    useDns6CheckBox.alpha = .5f
                    useDns6TextView.alpha = .5f
                    CompoundButtonCompat.setButtonTintList(
                        useDns6CheckBox, ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.unCheckedColor)
                        )
                    )
                    dns1TextInputLayout.hint = getString(R.string.dns3_text)
                    dns1TextInputLayout.editText!!.setText(dnsList[2])

                    dns3TextInputLayout.hint = getString(R.string.dns4_text)
                    dns3TextInputLayout.editText!!.setText(dnsList[3])

                    dns2TextInputLayout.visibility = View.GONE
                    dns4TextInputLayout.visibility = View.GONE
                }

            }

        useDns6CheckBox
            .setOnCheckedChangeListener { _, isChecked ->
                setVisibleDNSv6(isChecked)

                if (isChecked) {
                    useDns4CheckBox.isEnabled = true
                    useDns4CheckBox.alpha = 1f
                    useDns4TextView.alpha = 1f
                    CompoundButtonCompat.setButtonTintList(
                        useDns4CheckBox, ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.colorPrimary)
                        )
                    )
                    dns3TextInputLayout.hint = getString(R.string.dns3_text)
                    dns3TextInputLayout.editText!!.setText(dnsList[2])

                    dns2TextInputLayout.visibility = View.VISIBLE
                    dns2TextInputLayout.editText!!.setText(dnsList[1])

                    dns4TextInputLayout.visibility = View.VISIBLE
                    dns4TextInputLayout.editText!!.setText(dnsList[3])

                } else {
                    useDns4CheckBox.isEnabled = false
                    useDns4CheckBox.alpha = .5f
                    useDns4TextView.alpha = .5f
                    CompoundButtonCompat.setButtonTintList(
                        useDns4CheckBox, ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.unCheckedColor)
                        )
                    )

                    dns3TextInputLayout.hint = getString(R.string.dns2_text)
                    dns3TextInputLayout.editText!!.setText(dnsList[1])

                    dns2TextInputLayout.visibility = View.GONE
                    dns4TextInputLayout.visibility = View.GONE
                }

            }


        useDns6CheckBox.isChecked = isVisibleDNSv6()

        binding.customDnsButton.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_open_custom_dns)
        }

        binding.dnsTestButton.setOnClickListener {
            Navigation.findNavController(binding.root).navigate(R.id.action_open_dns_test)
        }


        normalChangesButton.setOnClickListener {

            val intent = VpnService.prepare(requireContext())
            Log.e("GotSomething", "reach ")
            if (intent != null) {
                Log.e("GotSomething", " Not Null ")
                startActivityForResult(intent, requestDnsCode)
            } else {
                Log.e("GotSomething", " Null ")
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
        if (requestCode == requestDnsCode && resultCode == Activity.RESULT_CANCELED)
            CustomToast.toastIt(
                requireContext(),
                getString(R.string.could_not_configure_vpn_service)
            )

        if (requestCode == requestDnsCode && resultCode == Activity.RESULT_OK) {
            val bindIntent = Intent(activity, AdVpnService::class.java)
            requireActivity().bindService(bindIntent, serviceConnection, Context.BIND_ABOVE_CLIENT)
            requireContext().startService(bindIntent)

        }

    }

    /**
     * Interface for getting the instance of binder from our service class
     * So client can get instance of our service class and can directly communicate with it.
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
            Log.d(mTAG, "ServiceConnection: connected to service.")
            // We've bound to MyService, cast the IBinder and get MyBinder instance
            val binder = iBinder as AdVpnService.MyBinder
            mService = binder.service
            mIsBound = true
            getStatus() // return a random number from the service
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d(mTAG, "ServiceConnection: disconnected from service.")
            mIsBound = false
        }
    }

    /**
     * Method for listening to random numbers generated by our service class
     */
    private fun getStatus() {
        mService!!.dnsStatus.observe(this
            , Observer {
                statusDns_editText.text = it
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mIsBound != null) requireContext().unbindService(serviceConnection)
    }
}
