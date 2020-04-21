package com.sudoajay.dnswidget.ui.sendFeedback

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Debug
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.helper.Connectivity
import com.sudoajay.dnswidget.helper.FileSize
import java.util.*


class SystemInfo() : DialogFragment(), View.OnClickListener {
    private var rootview: View? = null
    private var show: String = "ShowSomething"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootview = inflater.inflate(R.layout.layout_system_info, container, false)

        mainFun()

        return rootview
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun mainFun() { // Reference Object

        val constraintLayout: ConstraintLayout = rootview!!.findViewById(R.id.constraintLayout)
        val closeImageView: ImageView = rootview!!.findViewById(R.id.close_ImageView)
        val okButton: Button     = rootview!!.findViewById(R.id.ok_Button)
        val deviceInfoText: TextView = rootview!!.findViewById(R.id.deviceInfoText_TextView)
        val osApiLevelText: TextView = rootview!!.findViewById(R.id.osApiLevelText_TextView)
        val appVersionText: TextView = rootview!!.findViewById(R.id.appVersionText_TextView)
        val languageText: TextView = rootview!!.findViewById(R.id.languageText_TextView)
        val totalMemoryText: TextView = rootview!!.findViewById(R.id.totalMemoryText_TextView)
        val freeMemoryText: TextView = rootview!!.findViewById(R.id.freeMemoryText_TextView)
        val screenText: TextView = rootview!!.findViewById(R.id.screenText_TextView)
        val networkTypeText: TextView = rootview!!.findViewById(R.id.networkTypeText_TextView)


        // setup dialog box
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        constraintLayout.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.tabBackgroundColor
            )
        )
//         On click Listener
        closeImageView.setOnClickListener(this)
        okButton.setOnClickListener(this)

//        get info App
        val pInfo = requireActivity().packageManager.getPackageInfo(
            requireActivity().packageName, PackageManager.GET_META_DATA
        )
//        Info Heap Size
        val nativeHeapSize = Debug.getNativeHeapSize()
        val nativeHeapFreeSize = Debug.getNativeHeapFreeSize()


//        Screen Size
        val dm = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(dm)



//        Fill in the text View
        deviceInfoText.text= Build.MANUFACTURER.capitalize()+ "  " +Build.MODEL + " (" +  Build.PRODUCT +")"
        osApiLevelText.text = Build.VERSION.SDK_INT.toString()
        appVersionText.text= pInfo.versionName
        languageText.text = Locale.getDefault().displayLanguage
        totalMemoryText.text =FileSize.convertIt(nativeHeapSize)
        freeMemoryText.text = FileSize.convertIt(nativeHeapFreeSize)
        screenText.text = dm.heightPixels.toString() + " x " +dm.widthPixels.toString()
        networkTypeText.text =Connectivity.getNetworkProvider(requireContext())


    }




    override fun onStart() { // This MUST be called first! Otherwise the view tweaking will not be present in the displayed Dialog (most likely overriden)
        super.onStart()
        forceWrapContent(this.view)
    }

    private fun forceWrapContent(v: View?) { // Start with the provided view
        var current = v
        val dm = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels
        // Travel up the tree until fail, modifying the LayoutParams
        do { // Get the parent
            val parent = current!!.parent
            // Check if the parent exists
            if (parent != null) { // Get the view
                current = try {
                    parent as View
                } catch (e: ClassCastException) { // This will happen when at the top view, it cannot be cast to a View
                    break
                }
                // Modify the layout
                current.layoutParams.width = width - 10 * width / 100
            }
        } while (current!!.parent != null)
        // Request a layout to be re-done
        current!!.requestLayout()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.close_ImageView, R.id.ok_Button -> dismiss()
        }
    }


}