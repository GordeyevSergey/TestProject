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
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

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
        _formLiveData.postValue(currentForm)
    }

    fun sendForm() {
        if (formValidation()) {
            CoroutineScope(Dispatchers.IO).launch {
                val title = RequestBody.create(MediaType.parse("multipart/form-data"), currentForm.title)
                val comment = RequestBody.create(MediaType.parse("multipart/form-data"), currentForm.description)
                var photo: MultipartBody.Part? = null

                currentForm.photo?.let {
                    val file = File(it.path)
                    photo = MultipartBody.Part.createFormData("image", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
                }

                val response = retrofit.sendForm(title, comment, photo)
                Log.i(LogTags.LOG_RETROFIT_INTERACTION_SUCCESS.name, response.code().toString())

                if (response.isSuccessful) {
                    clearForm()
                    Log.i(LogTags.LOG_RETROFIT_INTERACTION_SUCCESS.name, response.code().toString())
                } else {
                    Log.i(LogTags.LOG_RETROFIT_INTERACTION_FAILURE.name, response.errorBody().toString())
                }
//                _sendFormResult.postValue("Success")
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