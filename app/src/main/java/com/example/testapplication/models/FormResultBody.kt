package com.example.testapplication.models

import com.google.gson.annotations.SerializedName

data class FormResultBody(
        @SerializedName("filename")
        val result: String)