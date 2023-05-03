package com.example.dogdex.api

import com.example.dogdex.*
import com.example.dogdex.api.Response.DogListApiResponse
import com.example.dogdex.api.Response.LoginApiResponse
import com.example.dogdex.api.Response.SignUpApiResponse
import com.example.dogdex.api.dto.LoginDTO
import com.example.dogdex.api.dto.SignUpDTO
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create())
    .build()

interface ApiService {
    @GET(GET_ALL_DOGS)
    suspend fun getAllDogs(): DogListApiResponse

    @POST(SIGN_UP_URL)
    suspend fun signUp(@Body signUpDTO: SignUpDTO): SignUpApiResponse

    @POST(SIGN_IN_URL)
    suspend fun login(@Body loginDTO: LoginDTO): LoginApiResponse
}

object DogsApi {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}