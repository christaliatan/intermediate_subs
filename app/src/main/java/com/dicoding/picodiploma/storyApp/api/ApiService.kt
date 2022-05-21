package com.dicoding.picodiploma.storyApp.api

import com.dicoding.picodiploma.storyApp.view.login.LoginResult
import com.dicoding.picodiploma.storyApp.view.story.ListStory
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

data class RegisterResponse(
    @field:SerializedName(false.toString())
    val error: Boolean,
    @field:SerializedName("User Created")
    val message: String
)

data class LoginResponse(
    @field:SerializedName(false.toString())
    val error: Boolean,
    @field:SerializedName("success")
    val message: String,
    @field:SerializedName("loginResult")
    val loginResult: LoginResult?
)

data class AddNewStoryResponse(
    @field:SerializedName(false.toString())
    val error: Boolean,
    @field:SerializedName("success")
    val message: String
)

data class GetAllStoriesResponse(
    val error: Boolean,
    val message: String,
    @field:SerializedName("listStory")
    val listStory: List<ListStory>
)

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String?,
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String?,
        @Field("password") password: String?
    ): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun addNewStory(
        @Header("Authorization") authToken: String,
        @Part photo: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<AddNewStoryResponse>

    @GET("stories")
    fun getAllStories(
        @Header("Authorization") token: String?,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 15,
        @Query("location") location: Int = 0
    ): Call<GetAllStoriesResponse>

    @GET("stories")
    suspend fun getAllStoriesPaging(
        @Header("Authorization") token: String?,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 15,
        @Query("location") location: Int = 0
    ): Response<GetAllStoriesResponse>
}

object ApiConfig {
    private const val BASE_URL = "https://story-api.dicoding.dev/v1/"

    val instances: ApiService by lazy {
        val loggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        retrofit.create(ApiService::class.java)
    }
}