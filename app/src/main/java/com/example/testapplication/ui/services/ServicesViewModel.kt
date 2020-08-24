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

class ServicesViewModel(private val apiClient: ApiService) : ViewModel() {
    private val _serviceListLiveData = MutableLiveData<List<ServiceItem>>()
    val serviceListLiveData: LiveData<List<ServiceItem>>
        get() = _serviceListLiveData

    init {
        getServiceList()
    }

    private fun getServiceList() {
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiClient.getServiceList()

            Log.i(LogTags.LOG_RETROFIT_INTERACTION_RESPONSE.name, LogTags.LOG_RETROFIT_INTERACTION_RESPONSE.logMessage)
            if (response.isSuccessful) {
                _serviceListLiveData.postValue(response.body())
                Log.i(LogTags.LOG_RETROFIT_INTERACTION_SUCCESS.name, response.body()?.size.toString())
            } else {
                Log.i(LogTags.LOG_RETROFIT_INTERACTION_FAILURE.name, response.errorBody().toString())
            }
        }
    }
}