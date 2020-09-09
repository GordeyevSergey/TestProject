package com.example.testapplication.network

import com.example.testapplication.models.MessageBody
import com.example.testapplication.models.ServiceItem
import okhttp3.MultipartBody
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
    suspend fun sendForm(@Part("name") name: RequestBody,
                         @Part("comment") comment: RequestBody,
                         @Part photo: MultipartBody.Part?): Response<MessageBody>
}