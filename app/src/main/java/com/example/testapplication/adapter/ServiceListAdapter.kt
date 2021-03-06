package com.example.testapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.models.ServiceItem
import com.example.testapplication.R
import com.example.testapplication.util.OnServiceItemClick
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_services_item.view.*
import timber.log.Timber

class ServiceListAdapter(private val listener: OnServiceItemClick) : RecyclerView.Adapter<ServiceListAdapter.ServiceViewHolder>() {
    private var serviceList: List<ServiceItem> = ArrayList()

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemIcon: ImageView = itemView.service_icon
        val itemTitle: TextView = itemView.service_title
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_services_item, parent, false)
        val viewHolder = ServiceViewHolder(view)
        view.setOnClickListener {
            listener.onClick(serviceList[viewHolder.adapterPosition])
            Timber.d("${serviceList[viewHolder.adapterPosition].title} clicked")
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val currentService = serviceList[position]

        holder.itemTitle.text = currentService.title
        Picasso.get()
                .load(currentService.icon)
                .error(R.drawable.ic_rv_services)
                .into(holder.itemIcon)

    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    fun setServices(newServiceList: List<ServiceItem>) {
        serviceList = newServiceList
        notifyDataSetChanged()
        Timber.d("service list updated")
    }
}