package com.example.testapplication.ui.services

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testapplication.models.ServiceItem
import com.example.testapplication.network.RetrofitInstance
import com.example.testapplication.util.LogTags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ServicesViewModel : ViewModel() {
    private val retrofit = RetrofitInstance.getRetrofitInstance()
    private var isResponseBlocked: Boolean = false
    
    private val _serviceListLiveData = MutableLiveData<List<ServiceItem>>()
    val serviceListLiveData: LiveData<List<ServiceItem>>
        get() = _serviceListLiveData

    fun getServiceList() {
        isResponseBlocked = true
        if (!isResponseBlocked) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = retrofit.getServiceList()
                Log.i(LogTags.LOG_RETROFIT_INTERACTION_RESPONSE.name, LogTags.LOG_RETROFIT_INTERACTION_RESPONSE.logMessage)
                when (response.isSuccessful) {
                    true -> {
                        _serviceListLiveData.postValue(response.body())
                        Log.i(LogTags.LOG_RETROFIT_INTERACTION_SUCCESS.name, LogTags.LOG_RETROFIT_INTERACTION_SUCCESS.logMessage)
                    }
                    false -> {
                        Log.i(LogTags.LOG_RETROFIT_INTERACTION_FAILURE.name, LogTags.LOG_RETROFIT_INTERACTION_FAILURE.logMessage)
                    }
                }
            }
        }
    }
}