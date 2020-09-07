package com.example.testapplication.models

import java.io.File

data class Form(var name: String = "",
                var comment: String = "",
                var photo: File? = null)