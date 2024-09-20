package com.non.abztest.model

data class ApiResponse(
    val success: Boolean,
    val total_pages: Int,
    val total_users: Int,
    val count: Int,
    val page: Int,
    val links: Links,
    val users: List<UserGet>
)