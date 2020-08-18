package com.example.testapplication.ui

import androidx.lifecycle.ViewModel
import com.example.testapplication.network.RetrofitInstance

open class BaseViewModel : ViewModel()  {
    val retrofit = RetrofitInstance.getRetrofitInstance()
}