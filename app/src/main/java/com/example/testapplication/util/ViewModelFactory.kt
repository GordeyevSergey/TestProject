package com.example.testapplication.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testapplication.network.ApiClient
import com.example.testapplication.ui.form.FormViewModel
import com.example.testapplication.ui.services.ServicesViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    private val retrofit = ApiClient.getRetrofitInstance()

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ServicesViewModel::class.java)) {
            return ServicesViewModel(retrofit) as T
        } else if (modelClass.isAssignableFrom(FormViewModel::class.java)) {
            return FormViewModel(context, retrofit) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}