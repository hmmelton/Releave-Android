package com.hmmelton.releave.services

import com.hmmelton.releave.data.models.AuthRequestBody
import com.hmmelton.releave.data.models.Restroom
import com.hmmelton.releave.data.models.RestroomRequestBody
import com.hmmelton.releave.data.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ReleaveService {

    // Authentication routes

    @POST("api/v1/auth/facebook")
    fun authenticate(@Header("FacebookAuth") facebookAuthToken: String, @Body body: AuthRequestBody): Call<User>

    @GET("api/v1/auth/me")
    fun getCurrentUser(): Call<User>

    // User routes

    @GET("api/v1/users/{id}")
    fun getUser(@Path("id") id: String): Call<User>

    @POST("api/v1/users")
    fun addUser(@Body user: User): Call<Void>

    @PATCH("api/v1/users/{id}")
    fun updateUser(@Path("id") id: String, @Body user: User): Call<Void>

    @DELETE("api/v1/users/{id}")
    fun deleteUser(@Path("id") id: String): Call<Void>

    // Restroom routes

    @GET("api/v1/restrooms/{id}")
    fun getRestroom(@Path("id") id: String): Call<Restroom>

    @POST("api/v1/restrooms")
    fun addRestroom(@Body requestBody: RestroomRequestBody): Call<Restroom>

    @PATCH("api/v1/restrooms/{id}")
    fun updateRestroom(@Path("id") id: String, @Body requestBody: RestroomRequestBody): Call<Void>

    @DELETE("api/v1/restrooms/{id}")
    fun deleteRestroom(@Path("id") id: String): Call<Void>

    @GET("api/v1/area_restrooms")
    fun getAreaRestrooms(
        @Query("min_lat") minLat: Double,
        @Query("max_lat") maxLat: Double,
        @Query("min_lng") minLng: Double,
        @Query("max_lng") maxLng: Double
    ): Call<List<Restroom>>
}
