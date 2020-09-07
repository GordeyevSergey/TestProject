package com.example.testapplication.ui.form

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
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
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

class FormViewModel(private val context: Context,
                    private val apiClient: ApiService) : ViewModel() {
    companion object {
        private const val CLASS_NAME = "FormViewModel/"
    }

    private var currentForm = Form()

    private lateinit var photoFile: File

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

    fun saveImageForm() {
        currentForm.photo = photoFile
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
                    val mediaType = MediaType.parse("multipart/form-data")
                    val name = RequestBody.create(mediaType, currentForm.name)
                    val comment = RequestBody.create(mediaType, currentForm.comment)
                    var photo: MultipartBody.Part? = null

                    currentForm.photo?.let {
                        photo = MultipartBody.Part.createFormData("photo", it.name, RequestBody.create(mediaType, it))
                    }

                    val response = apiClient.sendForm(name, comment, photo)
                    Log.i(LogTags.LOG_RETROFIT_INTERACTION.name, "$CLASS_NAME Response")
                    if (response.isSuccessful) {
                        clearForm()
                        response.body()?.let {
                            _sendFormResult.postValue(it.result)
                        }
                        Log.i(LogTags.LOG_RETROFIT_INTERACTION.name, "$CLASS_NAME Success")
                    } else {
                        _sendFormResult.postValue(response.message())
                        Log.i(LogTags.LOG_RETROFIT_INTERACTION.name, "$CLASS_NAME Failure")
                    }
                } catch (exception: UnknownHostException) {
                    _sendFormResult.postValue(FormStatus.FORM_SEND_FAILURE.message)
                    Log.i(LogTags.LOG_RETROFIT_INTERACTION.name, "$CLASS_NAME $exception")
                }

            }
        } else {
            _sendFormResult.value = FormStatus.WRONG_FORM.message
            Log.i(LogTags.LOG_FORM.name, "$CLASS_NAME Incorrect form")
        }
    }

    fun clearDialogMessage() {
        _sendFormResult.value = null
        Log.i(LogTags.LOG_ALERT_DIALOG.name, "$CLASS_NAME Message cleared")
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

    fun createPhotoFileAndGetUri(): Uri {
        val dir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val fileSuffix = ".jpg"
        val filePrefix: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        photoFile = File.createTempFile(filePrefix, fileSuffix, dir)

        Log.i(LogTags.LOG_STORAGE.name, "FILE CREATED: ${photoFile.absolutePath}")
        return FileProvider.getUriForFile(context, "com.example.testapplication", photoFile)
    }

}