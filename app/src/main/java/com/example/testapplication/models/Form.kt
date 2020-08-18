package com.example.testapplication.models

import android.net.Uri

data class Form(var title: String = "",
                var description: String = "",
                var photo: Uri? = null)