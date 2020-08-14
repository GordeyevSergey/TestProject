package com.example.testapplication.ui.form

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FormViewModel : ViewModel() {

    val imageLiveData = MutableLiveData<Uri>()

    fun setImageToLd(img: Uri) {
        imageLiveData.value = img
    }
}