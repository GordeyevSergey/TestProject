package com.example.testapplication.network

import com.example.testapplication.models.FormResultBody
import com.example.testapplication.models.ServiceItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

interface ApiService {
    @GET("services.php?os=android")
    suspend fun getServiceList(): Response<List<ServiceItem>>

    @Multipart
    @POST("/upload.php")
    suspend fun sendForm(@Part name: String,
                         @Part comment: String,
                         @Part photo: File?): Response<FormResultBody>
}