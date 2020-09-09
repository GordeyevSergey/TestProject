package com.example.testapplication.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testapplication.network.RetrofitClient
import com.example.testapplication.network.ServiceRepository
import com.example.testapplication.ui.form.FormViewModel
import com.example.testapplication.ui.services.ServicesViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    private val retrofit = RetrofitClient.getRetrofitInstance()
    private val repository = ServiceRepository(retrofit)

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ServicesViewModel::class.java)) {
            return ServicesViewModel(repository) as T
        } else if (modelClass.isAssignableFrom(FormViewModel::class.java)) {
            return FormViewModel(application, repository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}