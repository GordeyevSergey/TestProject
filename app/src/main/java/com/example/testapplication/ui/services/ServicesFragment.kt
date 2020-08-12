package com.example.testapplication.ui.services

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapplication.R
import com.example.testapplication.adapter.ServiceListAdapter
import com.example.testapplication.databinding.FragmentServicesBinding
import com.example.testapplication.models.ServiceItem

class ServicesFragment : Fragment(){

    private lateinit var binding: FragmentServicesBinding
    private lateinit var servicesViewModel: ServicesViewModel

    private val serviceListAdapter = ServiceListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_services, container, false)
        servicesViewModel = ViewModelProviders.of(this).get(ServicesViewModel::class.java)

        //Recycler
        binding.servicesList.adapter = serviceListAdapter
        binding.servicesList.layoutManager = LinearLayoutManager(context)

        //Observers
        servicesViewModel.serviceListLiveData.observe(this, Observer { newServiceList: List<ServiceItem> ->
            serviceListAdapter.setServices(newServiceList)
        })

        servicesViewModel.getServiceList()
        return binding.root
    }
}
