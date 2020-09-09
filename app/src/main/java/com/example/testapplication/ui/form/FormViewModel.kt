package com.example.testapplication.ui.form

import android.app.Application
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testapplication.models.Form
import com.example.testapplication.network.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

class FormViewModel(private val application: Application,
                    private val apiClient: ApiService) : ViewModel() {
    private var currentForm = Form()

    private lateinit var photoFile: File

    private val _formLiveData = MutableLiveData<Form>()
    val formLiveData: LiveData<Form>
        get() = _formLiveData

    private val _sendFormResult = MutableLiveData<String?>()
    val sendFormResult: LiveData<String?>
        get() = _sendFormResult

    init {
        _formLiveData.value = currentForm
    }

    fun saveTextForm(name: String, comment: String) {
        currentForm.name = name
        currentForm.comment = comment
        _formLiveData.value = currentForm
        Timber.d("form data: text saved")
    }

    fun saveImageForm() {
        currentForm.photo = photoFile
        _formLiveData.value = currentForm
        Timber.d("form data: image saved")
    }

    private fun clearForm() {
        currentForm.name = ""
        currentForm.comment = ""
        currentForm.photo = null
        _formLiveData.postValue(currentForm)
        Timber.d("form data: cleared")
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
                    if (response.isSuccessful) {
                        clearForm()
                        response.body()?.let {
                            _sendFormResult.postValue(it.result)
                        }
                        Timber.d("send form successful")
                    } else {
                        _sendFormResult.postValue(response.message())
                        Timber.d("send form failure")
                    }
                } catch (exception: UnknownHostException) {
                    _sendFormResult.postValue("Отсутствует интернет соединение")
                    Timber.e(exception)
                }

            }
        } else {
            _sendFormResult.value = "Необходимо заполнить все поля"
            Timber.d("form have empty fields")
        }
    }

    fun clearDialogMessage() {
        _sendFormResult.value = null
        Timber.d("result of sending cleared")
    }

    private fun formValidation(): Boolean {
        if (currentForm.name.isBlank() || currentForm.comment.isBlank()) {
            Timber.d("form validation: invalid")
            return false
        } else {
            currentForm.apply {
                this.name = this.name.trim()
                this.comment = this.comment.trim()
                _formLiveData.value = this
            }
            Timber.d("form validation: valid")
            return true
        }
    }

    fun createPhotoFileAndGetUri(): Uri {
        val dir: File? = application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val fileSuffix = ".jpg"
        val filePrefix: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        photoFile = File.createTempFile(filePrefix, fileSuffix, dir)
        Timber.d("photo file created")

        return FileProvider.getUriForFile(application, "com.example.testapplication", photoFile)
    }

}