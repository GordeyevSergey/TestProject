package com.example.testapplication.ui.services

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.testapplication.R
import com.example.testapplication.adapter.ServiceListAdapter
import com.example.testapplication.databinding.FragmentServicesBinding
import com.example.testapplication.models.ServiceItem
import com.example.testapplication.util.ViewModelFactory
import com.example.testapplication.util.OnServiceItemClick
import kotlinx.android.synthetic.main.custom_toolbar.view.*

class ServicesFragment : Fragment(), OnServiceItemClick {

    private lateinit var binding: FragmentServicesBinding
    private lateinit var servicesViewModel: ServicesViewModel

    private lateinit var serviceListAdapter: ServiceListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_services, container, false)
        setToolbar()
        activity?.application?.let {
            servicesViewModel = ViewModelProviders.of(requireActivity(), ViewModelFactory(it)).get(ServicesViewModel::class.java)
        }

        //Recycler
        serviceListAdapter = ServiceListAdapter(this)
        binding.servicesList.adapter = serviceListAdapter
        binding.servicesList.layoutManager = LinearLayoutManager(context)

        //Observers
        servicesViewModel.serviceListLiveData.observe(viewLifecycleOwner, Observer { newServiceList: List<ServiceItem> ->
            serviceListAdapter.setServices(newServiceList)
        })

        servicesViewModel.errorLiveData.observe(viewLifecycleOwner, Observer {
            showToast(it)
        })

        return binding.root
    }

    private fun setToolbar() {
        val toolbar = binding.root.custom_actionbar
        toolbar.title.setText(R.string.title_services)
        toolbar.send_form_imagebutton.visibility = View.GONE
    }

    override fun onClick(item: ServiceItem) {
        showToast("${item.title} открывается")
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.link)))
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
