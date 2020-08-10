package com.example.testapplication.models

import com.google.gson.annotations.SerializedName

data class ServiceItem(
        @SerializedName("icon")
        val icon: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("link")
        val link: String)