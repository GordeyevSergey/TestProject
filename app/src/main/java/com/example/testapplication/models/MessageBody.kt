package com.example.testapplication.models

import com.google.gson.annotations.SerializedName

data class MessageBody(
        @SerializedName("filename")
        val message: String)