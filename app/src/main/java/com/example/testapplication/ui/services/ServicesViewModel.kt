package com.example.testapplication.ui.services

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testapplication.models.ServiceItem
import com.example.testapplication.network.ServiceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.UnknownHostException

class ServicesViewModel(private val repository: ServiceRepository) : ViewModel() {
    private val _serviceListLiveData = MutableLiveData<List<ServiceItem>>()
    val serviceListLiveData: LiveData<List<ServiceItem>>
        get() = _serviceListLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String>
        get() = _errorLiveData

    init {
        getServiceList()
    }

    private fun getServiceList() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                repository.getServices()?.let {
                    _serviceListLiveData.postValue(it)
                }
            } catch (exception: UnknownHostException) {
                _errorLiveData.postValue("Отсутствует интернет соединение")
                Timber.e(exception)
            }
        }
    }
}