package com.sudoajay.dnswidget.ui.appFilter

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.databinding.RecyclerAppItemBinding
import com.sudoajay.dnswidget.ui.appFilter.database.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PagingAppRecyclerAdapter(context: Context, private var appFilter: AppFilter) :
    PagedListAdapter<App, PagingAppRecyclerAdapter.MyViewHolder>(DIFF_CALLBACK) {


    private var packageManager = context.packageManager


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val recyclerAppItemBinding: RecyclerAppItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recycler_app_item, parent, false
        )
        return MyViewHolder(recyclerAppItemBinding)

    }

    class MyViewHolder(recyclerAppItemBinding: RecyclerAppItemBinding) :
        RecyclerView.ViewHolder(recyclerAppItemBinding.root) {
        val icon: ImageView = recyclerAppItemBinding.imageView
        val title: TextView = recyclerAppItemBinding.titleTextView
        val appPackage: TextView = recyclerAppItemBinding.appPackageTextView
        val checkBox: CheckBox = recyclerAppItemBinding.checkbox
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val app = getItem(position)

        holder.title.text = app!!.name
        holder.appPackage.text = app.packageName
        holder.icon.setImageDrawable(getApplicationsIcon(app.icon))

        holder.checkBox.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                appFilter.appFilterViewModel.appRepository.updateSelectedApp(
                    (it as CompoundButton).isChecked,
                    app.packageName
                )
            }
        }


        holder.checkBox.isChecked = app.isSelected
    }

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<App>() {
            // Concert details may have changed if reloaded from the database,
            // but ID is fixed.
            override fun areItemsTheSame(
                oldConcert: App,
                newConcert: App
            ) = oldConcert.id == newConcert.id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldConcert: App,
                newConcert: App
            ): Boolean = oldConcert == newConcert

        }
    }

    private fun getApplicationsIcon(applicationInfo: String): Drawable {
        return try {
            packageManager.getApplicationIcon(applicationInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            defaultApplicationIcon
        }
    }

    private val defaultApplicationIcon: Drawable
        get() = packageManager.defaultActivityIcon


}