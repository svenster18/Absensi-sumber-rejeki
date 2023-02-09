package com.sumberrejeki.absensi.data

import com.sumberrejeki.absensi.data.model.PegawaiResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<PegawaiResponse>

}