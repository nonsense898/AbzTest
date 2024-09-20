package com.non.abztest.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.non.abztest.model.RegistrationResponse
import com.non.abztest.model.TokenResponse
import com.non.abztest.model.UserPost
import com.non.abztest.network.ApiService
import com.non.abztest.paging.UserPagingSource
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class UserRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getPositions() = apiService.getPositions()
    private var currentPagingSource: UserPagingSource? = null

    fun clearUserCache() {
        currentPagingSource?.invalidate()
    }

    fun getUsers() = Pager(
        config = PagingConfig(
            pageSize = 6,
            enablePlaceholders = true
        ),
        pagingSourceFactory = { UserPagingSource(apiService) }
    ).liveData

    private suspend fun getToken(): TokenResponse {
        return apiService.getToken()
    }

    suspend fun registerUser(user: UserPost, photoFile: File): Response<RegistrationResponse> {
        if (!user.isValid()) {
            throw IllegalArgumentException("Invalid user data")
        }

        return try {
            val tokenResponse = getToken()
            val token = tokenResponse.token

            val photoPart = MultipartBody.Part.createFormData(
                "photo",
                photoFile.name,
                photoFile.asRequestBody("image/*".toMediaTypeOrNull())
            )

            val namePart = user.name.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailPart = user.email.toRequestBody("text/plain".toMediaTypeOrNull())
            val phonePart = user.phone.toRequestBody("text/plain".toMediaTypeOrNull())
            val positionPart = user.positionId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            apiService.registerUser(
                token,
                namePart,
                emailPart,
                phonePart,
                positionPart,
                photoPart
            )

        } catch (e: Exception) {
            println("Error: ${e.message}")
            throw e
        }
    }
}