package com.example.testapplication.ui.services

import android.util.Log
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

    val serviceListLiveData = MutableLiveData<List<ServiceItem>>()

    fun getServiceList() {
        if (!isResponseBlocked) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = retrofit.getServiceList()
                Log.i(LogTags.LOG_RETROFIT_INTERACTION_RESPONSE.name, LogTags.LOG_RETROFIT_INTERACTION_RESPONSE.logMessage)
                if (response.isSuccessful) {
                    serviceListLiveData.postValue(response.body())

                    Log.i(LogTags.LOG_RETROFIT_INTERACTION_SUCCESS.name, LogTags.LOG_RETROFIT_INTERACTION_SUCCESS.logMessage)
                } else {
                    //Test
                    val list = ArrayList<ServiceItem>()
                    list.add(ServiceItem("icon", "TitleName", "link"))
                    list.add(ServiceItem("icon", "TitleName", "link"))
                    list.add(ServiceItem("icon", "TitleName", "link"))
                    list.add(ServiceItem("icon", "TitleName", "link"))
                    list.add(ServiceItem("icon", "TitleName", "link"))
                    list.add(ServiceItem("icon", "TitleName", "link"))
                    serviceListLiveData.postValue(list)

                    Log.i(LogTags.LOG_RETROFIT_INTERACTION_FAILURE.name, LogTags.LOG_RETROFIT_INTERACTION_FAILURE.logMessage)
                }
            }
        }
    }
}