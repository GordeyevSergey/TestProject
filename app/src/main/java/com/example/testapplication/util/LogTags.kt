package com.example.testapplication.util

enum class LogTags(val logMessage: String) {
    LOG_RETROFIT_INTERACTION_RESPONSE("Response"),
    LOG_RETROFIT_INTERACTION_SUCCESS("Success"),
    LOG_RETROFIT_INTERACTION_FAILURE("Failure"),

    LOG_PERMISSIONS_REQUEST("permission request"),
    LOG_PERMISSIONS_GRANTED("permissions granted"),
    LOG_PERMISSIONS_DENIED("permissions denied"),

    LOG_CAMERA_RESULT_SUCCESSFUL("Image received"),
    LOG_FORM_IMAGEBUTTON_SRC_CHANGED("image changed")
}