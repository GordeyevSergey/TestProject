package com.example.testapplication.network

import com.example.testapplication.models.EmptyBody
import com.example.testapplication.models.ServiceItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("services.php?os=android")
    suspend fun getServiceList(): Response<List<ServiceItem>>

    @POST("/upload.php")
    suspend fun sendForm() : Response<EmptyBody>
}