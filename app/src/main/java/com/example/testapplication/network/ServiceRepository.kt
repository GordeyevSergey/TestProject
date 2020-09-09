package com.example.testapplication.network

import com.example.testapplication.models.Form
import com.example.testapplication.models.ServiceItem
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import timber.log.Timber

class ServiceRepository(private val api: ApiService) {
    suspend fun getServices(): List<ServiceItem>? {
        safeApiCall(
                call = { api.getServiceList() },
                error = "getServerList error"
        ).also {
            when (it) {
                is ResponseResult.Success -> {
                    return it.data
                }
                is ResponseResult.Error -> {
                    return null
                }
            }
        }
        return null
    }

    suspend fun sendForm(form: Form): String? {
        val mediaType = MediaType.parse("multipart/form-data")
        val name = RequestBody.create(mediaType, form.name)
        val comment = RequestBody.create(mediaType, form.comment)
        var photo: MultipartBody.Part? = null

        form.photo?.let {
            photo = MultipartBody.Part.createFormData("photo", it.name, RequestBody.create(mediaType, it))
        }

        safeApiCall(
                call = { api.sendForm(name, comment, photo) },
                error = "send form error"
        ).also {
            when (it) {
                is ResponseResult.Success -> {
                    return it.data?.message
                }
                is ResponseResult.Error -> {
                    return it.message
                }
            }
        }
        return null
    }

    private suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>, error: String): ResponseResult<T>? {
        call.invoke().also {
            return if (it.isSuccessful) {
                Timber.d("api response successful")
                ResponseResult.Success(it.body()!!)
            } else {
                Timber.d("api response failure. ${it.message()}")
                ResponseResult.Error(it.message())
            }
        }
    }
}