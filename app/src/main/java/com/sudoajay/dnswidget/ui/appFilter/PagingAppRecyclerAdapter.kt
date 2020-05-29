package com.sudoajay.dnswidget.ui.appFilter

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sudoajay.dnswidget.R
import com.sudoajay.dnswidget.ui.appFilter.dataBase.App

class PagingAppRecyclerAdapter(context: Context) :
    PagedListAdapter<App, PagingAppRecyclerAdapter.MyViewHolder>(DIFF_CALLBACK) {


    private var packageManager = context.packageManager


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_app_item, parent, false)

        return MyViewHolder(view)
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.imageView)
        val title: TextView = itemView.findViewById(R.id.title_TextView)
        val appPackage: TextView = itemView.findViewById(R.id.appPackage_TextView)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val app = getItem(position)

        holder.title.text = app!!.name
        holder.appPackage.text = app.packageName
        holder.icon.setImageDrawable(getApplicationsIcon(app.icon))

        holder.checkBox.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean ->
//            app.isSelected = isChecked
        }
        holder.checkBox.isChecked = app.isSelected
    }

    companion object {
        private val DIFF_CALLBACK = object :
            DiffUtil.ItemCallback<App>() {
            // Concert details may have changed if reloaded from the database,
            // but ID is fixed.
            override fun areItemsTheSame(oldConcert: App,
                                         newConcert: App) = oldConcert.id == newConcert.id

            override fun areContentsTheSame(oldConcert: App,
                                            newConcert: App) = oldConcert == newConcert
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