package com.example.testapplication.ui.services

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testapplication.models.ServiceItem
import com.example.testapplication.network.ApiService
import com.example.testapplication.util.LogTags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class ServicesViewModel(private val apiClient: ApiService) : ViewModel() {
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
                val response = apiClient.getServiceList()
                Log.i(LogTags.LOG_RETROFIT_INTERACTION.name, "${ServicesViewModel::class} Response")
                if (response.isSuccessful) {
                    _serviceListLiveData.postValue(response.body())
                    Log.i(LogTags.LOG_RETROFIT_INTERACTION.name, "${ServicesViewModel::class} Success")
                } else {
                    Log.i(LogTags.LOG_RETROFIT_INTERACTION.name, "${ServicesViewModel::class} Failure")
                }
            } catch (exception: UnknownHostException) {
                _errorLiveData.postValue("Отсутствует интернет соединение")
                Log.i(LogTags.LOG_RETROFIT_INTERACTION.name, "${ServicesViewModel::class} $exception")
            }
        }
    }
}