package com.example.testapplication.network

import retrofit2.Response
import timber.log.Timber

open class BaseRepository {
    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>, error: String): T? {
        apiCall(call, error).also {
            when (it) {
                is ResponseResult.Success -> {
                    Timber.d("api response successful")
                    return it.data
                }
                is ResponseResult.Error -> {
                    Timber.d("api response failure. ${it.message}")
                    return it.message
                }
            }
        }
        return null
    }

    private suspend fun <T : Any> apiCall(call: suspend () -> Response<T>, error: String): ResponseResult<T>? {
        call.invoke().also {
            return if (it.isSuccessful) {
                ResponseResult.Success(it.body()!!)
            } else {
                ResponseResult.Error(it.message() as T)
            }
        }
    }

}