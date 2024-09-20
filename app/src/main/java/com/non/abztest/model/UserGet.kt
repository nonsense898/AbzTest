package com.non.abztest.model

data class UserGet(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val position: String,
    val position_id: Int,
    val registration_timestamp: Long,
    val photo: String
)