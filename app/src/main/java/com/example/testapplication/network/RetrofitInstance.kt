package com.example.testapplication.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        private const val BASE_URL = "http://job.softinvent.ru/"

        fun getRetrofitInstance(): ApiService {
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                    .build()
                    .create(ApiService::class.java)
        }
    }
}