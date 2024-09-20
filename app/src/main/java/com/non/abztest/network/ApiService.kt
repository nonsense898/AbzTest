package com.non.abztest.network

import com.non.abztest.model.ApiResponse
import com.non.abztest.model.Position
import com.non.abztest.model.PositionResponse
import com.non.abztest.model.RegistrationResponse
import com.non.abztest.model.TokenResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET
    suspend fun getUsersFromUrl(@Url url: String): Response<ApiResponse>
    @GET("users")
    suspend fun getUsers(
        @Query("page") page: Int,
        @Query("count") count: Int = 6 // Default page size if not passed
    ): ApiResponse  // No need for Response<> wrapper

    @GET("positions")
    suspend fun getPositions(): PositionResponse

    @GET("token")
    suspend fun getToken(): TokenResponse

    @Multipart
    @POST("users")
    suspend fun registerUser(
        @Header("Token") token: String,
        @Part("name") namePart: RequestBody,
        @Part("email") emailPart: RequestBody,
        @Part("phone") phonePart: RequestBody,
        @Part("position_id") positionPart: RequestBody,
        @Part photoPart: MultipartBody.Part
    ): Response<RegistrationResponse>
}