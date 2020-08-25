package com.example.testapplication.util

import android.content.Context
import android.database.Cursor
import android.net.Uri

class PhotoUriPathConverter {
    companion object {
        fun convert(context: Context, uri: Uri): String? {
            var cursor: Cursor? = null
            val column = "_data"
            val projection = arrayOf(column)

            try {
                cursor = context.contentResolver.query(uri, projection, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val index = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(index)
                }
            } finally {
                cursor?.close()
            }
            return null
        }
    }
}