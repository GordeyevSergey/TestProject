package com.example.testapplication.ui.form

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.testapplication.models.Form
import com.example.testapplication.ui.BaseViewModel
import com.example.testapplication.util.FormStatus
import com.example.testapplication.util.LogTags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FormViewModel : BaseViewModel() {
    private var currentForm = Form()

    private val _formLiveData = MutableLiveData<Form>()
    val formLiveData: LiveData<Form>
        get() = _formLiveData

    private val _sendFormResult = MutableLiveData<String>()
    val sendFormResult: LiveData<String>
        get() = _sendFormResult

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
        if (formValidation()) {
            CoroutineScope(Dispatchers.IO).launch {
                val response = retrofit.sendForm(currentForm.title, currentForm.description, null)

                Log.i(LogTags.LOG_RETROFIT_INTERACTION_RESPONSE.name, LogTags.LOG_RETROFIT_INTERACTION_RESPONSE.logMessage)
                if (response.isSuccessful) {
                    _sendFormResult.value = FormStatus.FORM_SEND_SUCCESSFUL.message
                    Log.i(LogTags.LOG_RETROFIT_INTERACTION_SUCCESS.name, response.message())
                } else {
                    _sendFormResult.value = FormStatus.FORM_SEND_FAILURE.message
                    Log.i(LogTags.LOG_RETROFIT_INTERACTION_FAILURE.name, response.errorBody().toString())
                }
            }
        } else {
            _sendFormResult.value = FormStatus.WRONG_FORM.message
        }

    }

    private fun formValidation(): Boolean {
        if (currentForm.title.isBlank() || currentForm.description.isBlank()) {
            return false
        } else {
            currentForm.apply {
                this.title = this.title.trim()
                this.description = this.description.trim()
                _formLiveData.value = this
            }
            return true
        }
    }
}