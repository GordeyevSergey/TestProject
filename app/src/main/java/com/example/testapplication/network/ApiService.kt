package com.example.testapplication.network

import com.example.testapplication.models.FormResultBody
import com.example.testapplication.models.ServiceItem
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @GET("services.php?os=android")
    suspend fun getServiceList(): Response<List<ServiceItem>>

    @Multipart
    @POST("/upload.php")
    suspend fun sendForm(@Part("name") name: String,
                         @Part("comment") comment: String,
                         @Part("photo") photo: RequestBody?): Response<FormResultBody>
}