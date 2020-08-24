package com.example.testapplication.util

enum class FormStatus(val message: String) {
    FORM_SEND_SUCCESSFUL("Отправлено"),
    FORM_SEND_FAILURE("Ошибка отправки"),
    WRONG_FORM("Необходимо заполнить все поля")
}