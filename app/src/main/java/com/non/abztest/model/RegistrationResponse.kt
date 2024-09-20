package com.non.abztest.model

data class RegistrationResponse(
    val success: Boolean,
    val userId: String?,
    val message: String
)