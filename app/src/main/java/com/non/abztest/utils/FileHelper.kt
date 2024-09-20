package com.non.abztest.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.EditText
import java.io.File
import java.io.InputStream

class FileHelper {
    companion object {
        fun getShortenedFileNameFromUri(context: Context, uri: Uri, maxLength: Int, editText: EditText): String? {
            var fileName: String? = null
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    fileName = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                    fileName = cutStringWithEllipsis(fileName, maxLength)
                }
            }

            return fileName
        }

        private fun cutStringWithEllipsis(string: String?, maxLength: Int): String? {
            if (string.isNullOrEmpty()) {
                return null
            }

            if (string.length <= maxLength) {
                return string
            }

            val lastIndexOfDot = string.lastIndexOf('.')
            if (lastIndexOfDot == -1) {
                return string.substring(0, maxLength) + "..."
            }

            val prefixLength = maxLength.coerceAtMost(lastIndexOfDot)
            return string.substring(0, prefixLength) + "..." + string.substring(lastIndexOfDot)
        }

        fun getFileFromUri(uri: Uri, context: Context): File {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            return tempFile
        }
    }
}