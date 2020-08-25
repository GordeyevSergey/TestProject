package com.example.testapplication.models

import android.net.Uri

data class Form(var name: String = "",
                var comment: String = "",
                var photo: Uri? = null,
                var realPhotoPath: String? = null)