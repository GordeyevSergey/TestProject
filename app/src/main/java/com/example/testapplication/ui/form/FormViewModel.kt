package com.example.testapplication.ui.form

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testapplication.models.Form
import com.example.testapplication.network.ApiService
import com.example.testapplication.util.FormStatus
import com.example.testapplication.util.LogTags
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.lang.Exception

class FormViewModel(private val apiClient: ApiService) : ViewModel() {
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

    fun saveTextForm(name: String, comment: String) {
        currentForm.name = name
        currentForm.comment = comment
        _formLiveData.value = currentForm
    }

    fun saveImageForm(photo: Uri) {
        currentForm.photo = photo
        _formLiveData.value = currentForm
    }

    private fun clearForm() {
        currentForm.name = ""
        currentForm.comment = ""
        currentForm.photo = null
        _formLiveData.postValue(currentForm)
    }

    fun sendForm() {
        if (formValidation()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val name = RequestBody.create(MediaType.parse("multipart/form-data"), currentForm.name)
                    val comment = RequestBody.create(MediaType.parse("multipart/form-data"), currentForm.comment)
                    var photo: MultipartBody.Part? = null

                    currentForm.photo?.let {
                        val file = File(it.path)
                        photo = MultipartBody.Part.createFormData("photo", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
                    }

                    val response = apiClient.sendForm(name, comment, photo)

                    if (response.isSuccessful) {
                        clearForm()
                        response.body()?.let {
                            _sendFormResult.postValue(it.result)
                        }
                        Log.i(LogTags.LOG_RETROFIT_INTERACTION_SUCCESS.name, response.body()?.result.toString())
                    } else {
                        _sendFormResult.postValue(response.errorBody().toString())
                        Log.i(LogTags.LOG_RETROFIT_INTERACTION_FAILURE.name, response.errorBody().toString())
                    }
                } catch (e: Exception) {
                    Log.i(LogTags.LOG_RETROFIT_INTERACTION_FAILURE.name, e.toString())
                }

            }
        } else {
            _sendFormResult.value = FormStatus.WRONG_FORM.message
        }
    }

    private fun formValidation(): Boolean {
        if (currentForm.name.isBlank() || currentForm.comment.isBlank()) {
            return false
        } else {
            currentForm.apply {
                this.name = this.name.trim()
                this.comment = this.comment.trim()
                _formLiveData.value = this
            }
            return true
        }
    }
}