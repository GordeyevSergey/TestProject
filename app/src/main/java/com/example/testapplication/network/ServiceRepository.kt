package com.example.testapplication.network

import com.example.testapplication.models.Form
import com.example.testapplication.models.ServiceItem
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ServiceRepository(val api: ApiService) : BaseRepository() {
    suspend fun getServices(): List<ServiceItem>? {
        return safeApiCall(
                call = { api.getServiceList() },
                error = "getServerList error"
        )
    }

    suspend fun sendForm(form: Form): String? {
        val mediaType = MediaType.parse("multipart/form-data")
        val name = RequestBody.create(mediaType, form.name)
        val comment = RequestBody.create(mediaType, form.comment)
        var photo: MultipartBody.Part? = null

        form.photo?.let {
            photo = MultipartBody.Part.createFormData("photo", it.name, RequestBody.create(mediaType, it))
        }

        return safeApiCall(
                call = { api.sendForm(name, comment, photo) },
                error = "send form error"
        )?.result
    }
}