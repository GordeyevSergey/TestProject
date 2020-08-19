package com.example.testapplication.ui.form

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.testapplication.models.Form
import com.example.testapplication.ui.BaseViewModel
import com.example.testapplication.util.LogTags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FormViewModel : BaseViewModel() {
    private var currentForm = Form()

    private val _formLiveData = MutableLiveData<Form>()
    val formLiveData: LiveData<Form>
    get() = _formLiveData

    init {
        _formLiveData.value = currentForm
    }

    fun saveTextForm(title: String, desctiption: String) {
        currentForm.title = title
        currentForm.description = desctiption
        _formLiveData.value = currentForm
    }

    fun saveImageForm(photo: Uri) {
        currentForm.photo = photo
        _formLiveData.value = currentForm
    }

    private fun clearForm() {
        currentForm.title = ""
        currentForm.description = ""
        currentForm.photo = null
        _formLiveData.value = currentForm
    }

    fun sendForm() {
//        formValidation()
        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.sendForm(currentForm.title, currentForm.description, null)

            Log.i(LogTags.LOG_RETROFIT_INTERACTION_RESPONSE.name, LogTags.LOG_RETROFIT_INTERACTION_RESPONSE.logMessage)
            if (response.isSuccessful) {
                //SHOW SUCCESS FORM
                Log.i(LogTags.LOG_RETROFIT_INTERACTION_SUCCESS.name, response.message())
            } else {
                //SHOW ERROR TOAST
                Log.i(LogTags.LOG_RETROFIT_INTERACTION_FAILURE.name, response.errorBody().toString())
            }
        }
    }

    private fun formValidation(){
        
    }
}