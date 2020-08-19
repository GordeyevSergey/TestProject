package com.example.testapplication.ui.services

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.example.testapplication.util.OnServiceItemClick
import com.example.testapplication.util.OnToast
import kotlinx.android.synthetic.main.custom_toolbar.view.*

class ServicesFragment : Fragment(), OnServiceItemClick {

    private lateinit var binding: FragmentServicesBinding
    private lateinit var servicesViewModel: ServicesViewModel

    private lateinit var serviceListAdapter: ServiceListAdapter
    private lateinit var toast: OnToast

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_services, container, false)
        setToolbar()
        servicesViewModel = ViewModelProviders.of(requireActivity()).get(ServicesViewModel::class.java)

        //Recycler
        serviceListAdapter = ServiceListAdapter(this)
        binding.servicesList.adapter = serviceListAdapter
        binding.servicesList.layoutManager = LinearLayoutManager(context)

        //Observers
        servicesViewModel.serviceListLiveData.observe(this, Observer { newServiceList: List<ServiceItem> ->
            serviceListAdapter.setServices(newServiceList)
        })

        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        toast = context as OnToast
    }

    private fun setToolbar() {
        val toolbar = binding.root.custom_actionbar
        toolbar.title.setText(R.string.title_services)
        toolbar.send_form_imagebutton.visibility = View.GONE
    }

    override fun onClick(item: ServiceItem) {
        toast.showMessage("${item.title} открывается")
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.link)))
    }
}
