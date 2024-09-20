package com.non.abztest.model

import java.io.File
import java.util.regex.Pattern

data class UserPost(
    val name: String,
    val email: String,
    val phone: String,
    val positionId: Int,
    val photo: File
) {
    companion object {
        private val NAME_REGEX = "^.{2,60}$".toRegex()
        private val PHONE_REGEX = "^\\+380\\d{9}$".toRegex()
        private val EMAIL_REGEX = Pattern.compile("^(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])$", Pattern.CASE_INSENSITIVE)
    }

    fun isValid(): Boolean {
        return isNameValid() &&
                isEmailValid() &&
                isPhoneValid() &&
                isPositionIdValid() &&
                isPhotoValid()
    }

    fun isNameValid(): Boolean = name.matches(NAME_REGEX)

    fun isEmailValid(): Boolean = EMAIL_REGEX.matcher(email).matches()

    fun isPhoneValid(): Boolean = phone.matches(PHONE_REGEX)

    fun isPositionIdValid(): Boolean = positionId >= 1

    fun isPhotoValid(): Boolean {
        return photo.exists() &&
                photo.length() <= 5 * 1024 * 1024 &&
                photo.extension.lowercase() in listOf("jpg", "jpeg")
    }
}