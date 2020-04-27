package com.sudoajay.dnswidget.ui.customDns

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sudoajay.dnswidget.R

class CustomDnsFragment : Fragment() {

    private lateinit var customDnsViewModel: CustomDnsViewModel
    private lateinit var bottomSheetDialog: BottomSheetDialog
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

    @SuppressLint("InflateParams")
    private fun reference() {

        root.findViewById<FloatingActionButton>(R.id.addCustomDns_FloatingActionButton).setOnClickListener {
            callCustomDns()
        }
        val recycleView: RecyclerView = root.findViewById(R.id.recycler_view)


//        bottomSheetDialog = BottomSheetDialog(requireContext())
//        val sheetView = layoutInflater.inflate(R.layout.layout_bottomsheet_moreoption, null)
//        sheetView.findViewById<LinearLayout>(R.id.share_BottomSheetDialog).setOnClickListener{
//            bottomSheetDialog.dismiss()
//        }
//        sheetView.findViewById<LinearLayout>(R.id.edit_BottomSheetDialog).setOnClickListener{
//            bottomSheetDialog.dismiss()
//        }
//        sheetView.findViewById<LinearLayout>(R.id.delete_BottomSheetDialog).setOnClickListener{
//            bottomSheetDialog.dismiss()
//        }
//        sheetView.findViewById<LinearLayout>(R.id.copy_BottomSheetDialog).setOnClickListener{
//            bottomSheetDialog.dismiss()
//        }

        // Loads animals into the ArrayList
        customDnsViewModel.addAnimals()

        // Creates a vertical Layout Manager
        recycleView.layoutManager = LinearLayoutManager(requireContext())

        // You can use GridLayoutManager if you want multiple columns. Enter the number of columns as a parameter.
//        rv_animal_list.layoutManager = GridLayoutManager(this, 2)

        // Access the RecyclerView Adapter and load the data into it
        recycleView.adapter = CustomDnsAdapter(customDnsViewModel.animals, this)
    }

    private fun callCustomDns() {
        val ft = requireActivity().supportFragmentManager.beginTransaction()
        val addCustomDnsDialog = AddCustomDnsDialog()
        addCustomDnsDialog.show(ft, "dialog")
    }

    fun showBottomShow() {
        val addPhotoBottomDialogFragment: ActionBottomDialogFragment =
            ActionBottomDialogFragment()
        addPhotoBottomDialogFragment.show(
            requireActivity().supportFragmentManager.beginTransaction(),
            "ActionBottomDialog"
        )
    }

}
