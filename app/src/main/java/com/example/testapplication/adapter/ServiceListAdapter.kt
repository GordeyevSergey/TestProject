package com.example.testapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testapplication.models.ServiceItem
import com.example.testapplication.R
import kotlinx.android.synthetic.main.rv_services_item.view.*

class ServiceListAdapter() : RecyclerView.Adapter<ServiceListAdapter.ServiceViewHolder>() {
    private var serviceList: List<ServiceItem> = ArrayList()

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemIcon: ImageView = itemView.service_icon
        val itemTitle: TextView = itemView.service_title
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        return ServiceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_services_item, parent, false))
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val currentService = serviceList[position]

        //holder.itemIcon draw
        holder.itemTitle.text = currentService.title
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }

    fun setServices(newServiceList: List<ServiceItem>) {
        serviceList = newServiceList
    }
}