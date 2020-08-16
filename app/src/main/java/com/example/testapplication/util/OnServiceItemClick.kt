package com.example.testapplication.util

import com.example.testapplication.models.ServiceItem

interface OnServiceItemClick {
    fun onClick(item: ServiceItem)
}