package com.sudoajay.dnswidget.ui.home

import android.annotation.SuppressLint
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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.google.android.material.textfield.TextInputLayout
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.databinding.FragmentHomeBinding
import com.sudoajay.dnswidget.helper.CustomToast
import com.sudoajay.dnswidget.ui.customDns.database.DnsRepository
import com.sudoajay.dnswidget.ui.customDns.database.DnsRoomDatabase
import com.sudoajay.dnswidget.vpnClasses.AdVpnService
import com.sudoajay.dnswidget.vpnClasses.Command
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.Serializable


class HomeFragment : Fragment(), Serializable, View.OnFocusChangeListener {

    private lateinit var homeViewModel: HomeViewModel
    private var requestDnsCode = 1
    private lateinit var binding: FragmentHomeBinding
    private val dnsList: MutableList<String> = arrayListOf("", "", "", "")


    // Boolean to check if our activity is bound to service or not
    var mIsBound: Boolean = false

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

    @SuppressLint("ClickableViewAccessibility")
    private fun reference() {


        homeViewModel.getDnsName().observe(viewLifecycleOwner, Observer {

            binding.materialSpinner.setItems(it)
            Log.e(TAG, it.size.toString())
            if (it.isNotEmpty()) {
                val index = requireContext().getSharedPreferences("state", Context.MODE_PRIVATE)
                    .getLong("id", 2).toInt() - 1
                binding.materialSpinner.selectedIndex = index
                addItem(index, firstTime = true)

                addTextInputLayout()
            }


        })
        binding.materialSpinner.setOnItemSelectedListener { _, position, _, _ ->

            addItem(position)

            addTextInputLayout()

            binding.dns1TextInputLayout.error = null
            binding.dns1TextInputLayout.isErrorEnabled = false
        }


        binding.materialSpinner.setOnTouchListener { _, _ ->

            binding.dns1TextInputLayout.editText!!.clearFocus()
            binding.dns2TextInputLayout.editText!!.clearFocus()
            binding.dns3TextInputLayout.editText!!.clearFocus()
            binding.dns4TextInputLayout.editText!!.clearFocus()

            false
        }



        binding.useDns4CheckBox
            .setOnCheckedChangeListener { _, isChecked ->
                binding.dns1TextInputLayout.error = null
                binding.dns1TextInputLayout.isErrorEnabled = false
                setVisibleDNSv4(isChecked)

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
                binding.dns1TextInputLayout.error = null
                binding.dns1TextInputLayout.isErrorEnabled = false
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
                    binding.dns1TextInputLayout.hint = getString(R.string.dns1_text)
                    binding.dns1TextInputLayout.editText!!.setText(dnsList[0])

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

        binding.useDns4CheckBox.isChecked = isVisibleDNSv4()
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
            if (!isError()) {
                if (binding.connectDnsButton.text == requireContext().getString(R.string.start_text)) {
                    var value = binding.materialSpinner.selectedIndex
                    if (value == 0) {

                        CustomToast.toastIt(requireContext(), " Value ")
                        //        Creating Object and Initialization
                        val dnsDao = DnsRoomDatabase.getDatabase(requireContext()).dnsDao()
                        val dnsRepository = DnsRepository(requireContext(), dnsDao)
                        CoroutineScope(Dispatchers.IO).launch {
                            dnsRepository.updateDns(
                                value.toLong(),
                                requireContext().getString(R.string.custom_dns_enter_manually_text),
                                dnsList[0],
                                dnsList[1],
                                dnsList[2],
                                dnsList[3]
                            )
                        }
                    }
                    value += 1
                    startVpn(value.toLong())

                } else {
                    stopService()
                }
            }

        }

        val value =
            requireContext().getSharedPreferences("state", Context.MODE_PRIVATE)
                .getBoolean("isDnsActive", false)
        if (value) {
            binding.connectDnsButton.text = requireContext().getText(R.string.stop_text)
            binding.statusDnsTextView.text = requireContext().getText(R.string.connected_text)
        }

        binding.dns1TextInputLayout.editText!!.onFocusChangeListener = this
        binding.dns2TextInputLayout.editText!!.onFocusChangeListener = this
        binding.dns3TextInputLayout.editText!!.onFocusChangeListener = this
        binding.dns4TextInputLayout.editText!!.onFocusChangeListener = this


        binding.dns1TextInputLayout.editText!!.addTextChangedListener {
            if (binding.useDns4CheckBox.isChecked) {
                dnsList[0] = it.toString()
            } else {
                dnsList[2] = it.toString()
            }
        }
        binding.dns2TextInputLayout.editText!!.addTextChangedListener {
            dnsList[1] = it.toString()
        }
        binding.dns3TextInputLayout.editText!!.addTextChangedListener {
            if (binding.useDns4CheckBox.isChecked && binding.useDns6CheckBox.isChecked) {
                dnsList[2] = it.toString()
            } else if (binding.useDns4CheckBox.isChecked) {
                dnsList[1] = it.toString()
            } else {
                dnsList[3] = it.toString()
            }
        }
        binding.dns4TextInputLayout.editText!!.addTextChangedListener {
            dnsList[3] = it.toString()
        }
    }

    private fun isError(): Boolean {
        if (binding.useDns4CheckBox.isChecked) {

            if ((binding.useDns6CheckBox.isChecked && addUnspecified(binding.dns1TextInputLayout) && addUnspecified(
                    binding.dns2TextInputLayout
                )
                        && addUnspecified(binding.dns3TextInputLayout) && addUnspecified(binding.dns4TextInputLayout))
                || (!binding.useDns6CheckBox.isChecked && addUnspecified(binding.dns1TextInputLayout) && addUnspecified(
                    binding.dns3TextInputLayout
                ))
            ) {
                binding.dns1TextInputLayout.error =
                    getString(R.string.please_enter_dns_value_text)
                return true

            }
        } else {

            if (addUnspecified(binding.dns1TextInputLayout) && addUnspecified(binding.dns2TextInputLayout)) {
                binding.dns1TextInputLayout.error =
                    getString(R.string.please_enter_dns_value_text)
                return true
            }

        }


        return false
    }

    private fun addUnspecified(layout: TextInputLayout): Boolean {
        return layout.editText!!.text.toString().isEmpty()
                || layout.editText!!.text.toString() == requireContext().getString(R.string.unspecified_text)
    }

    private fun startVpn(value: Long) {


        saveSelectedDnsInfo(value)

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

    private fun addTextInputLayout() {
        if (binding.useDns4CheckBox.isChecked && binding.useDns6CheckBox.isChecked) {
            binding.dns1TextInputLayout.editText!!.setText(dnsList[0])
            binding.dns2TextInputLayout.editText!!.setText(dnsList[1])
            binding.dns3TextInputLayout.editText!!.setText(dnsList[2])
            binding.dns4TextInputLayout.editText!!.setText(dnsList[3])

        } else if (binding.useDns4CheckBox.isChecked) {
            binding.dns1TextInputLayout.editText!!.setText(dnsList[0])
            binding.dns3TextInputLayout.editText!!.setText(dnsList[1])
        } else {
            binding.dns1TextInputLayout.editText!!.setText(dnsList[2])
            binding.dns3TextInputLayout.editText!!.setText(dnsList[3])
        }
    }


    override fun onFocusChange(p0: View?, p1: Boolean) {
        when (p0!!.id) {
            R.id.dns1_TextInputLayoutEditText, R.id.dns2_TextInputLayoutEditText,
            R.id.dns3_TextInputLayoutEditText, R.id.dns4_TextInputLayoutEditText ->
                materialSpinner.selectedIndex = 0


        }
    }

    private fun saveSelectedDnsInfo(id: Long) {
        requireContext().getSharedPreferences("state", Context.MODE_PRIVATE).edit()
            .putLong("id", id).apply()

    }

    private fun isVisibleDNSv4(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context).getBoolean("useDnsv4", true)
    }

    private fun setVisibleDNSv4(value: Boolean) {
        PreferenceManager
            .getDefaultSharedPreferences(context).edit().putBoolean("useDnsv4", value).apply()
    }

    private fun isVisibleDNSv6(): Boolean {
        return PreferenceManager
            .getDefaultSharedPreferences(context).getBoolean("useDnsv6", false)
    }


    private fun setVisibleDNSv6(value: Boolean) {
        PreferenceManager
            .getDefaultSharedPreferences(context).edit().putBoolean("useDnsv6", value).apply()
    }


    private fun addItem(position: Int, firstTime: Boolean = false) {
        if (firstTime || position != 0) {
            dnsList.clear()
            dnsList.add(homeViewModel.dnsList[position].dns1)
            dnsList.add(homeViewModel.dnsList[position].dns2)
            dnsList.add(homeViewModel.dnsList[position].dns3)
            dnsList.add(homeViewModel.dnsList[position].dns4)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestDnsCode && resultCode == Activity.RESULT_CANCELED)
            errorVpnService()
        else if (requestCode == requestDnsCode && resultCode == Activity.RESULT_OK) {

            val startIntent = Intent(requireContext(), AdVpnService::class.java)
            startIntent.putExtra("COMMAND", Command.START.ordinal)
            requireContext().bindService(startIntent, serviceConnection, Context.BIND_AUTO_CREATE)
            requireContext().startService(startIntent)

            Log.i(TAG, "$resultCode ---- Accepted ")
        }

    }


    private fun errorVpnService() {

        val theme = requireContext().getSharedPreferences("state", Context.MODE_PRIVATE)
            .getString(getString(R.string.dark_mode_text), getString(
                R.string.off_text
            )).toString()
        val builder: AlertDialog.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AlertDialog.Builder(requireContext(), if(theme == getString(R.string.off_text)) android.R.style.Theme_Material_Light_Dialog_Alert else android.R.style.Theme_Material_Dialog_Alert)
            } else {
                AlertDialog.Builder(requireContext())
            }
        builder.setTitle(requireContext().getString(R.string.error_vpn_text))
            .setMessage(requireContext().getString(R.string.could_not_configure_vpn_service))
            .setNegativeButton("Ok") { _, _ ->

            }
            .setIcon(R.drawable.ic_error)
            .setCancelable(true)
            .show()
    }

    private fun stopService() {

        val stopIntent = Intent(requireContext(), AdVpnService::class.java)
        stopIntent.putExtra("COMMAND", Command.STOP.ordinal)
        requireContext().bindService(stopIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        requireContext().startService(stopIntent)

        if(mIsBound) {
            Log.e(TAG , " got Unibind")
            requireContext().unbindService(serviceConnection)
            mIsBound = false
        }
    }


    /**
     * Interface for getting the instance of binder from our service class
     * So client can get instance of our service class and can directly communicate with it.
     */
    private  val serviceConnection = object : ServiceConnection {
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
                requireContext().getString(R.string.not_connected_text),
                requireContext().getString(R.string.notification_stopped) ->
                    binding.connectDnsButton.text = requireContext().getString(R.string.start_text)

               else -> binding.connectDnsButton.text = requireContext().getString(R.string.stop_text)
            }
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        if(mIsBound) {
            requireContext().unbindService(serviceConnection)
            mIsBound = false
        }
    }


}
