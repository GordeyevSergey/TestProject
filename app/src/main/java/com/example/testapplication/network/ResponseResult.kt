package com.example.testapplication.network

sealed class ResponseResult<out T : Any> {
    class Success<out T: Any>(val data: T): ResponseResult<T>()
    class Error<out T: Any>(val message: T): ResponseResult<T>()
}