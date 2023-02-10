package com.sumberrejeki.absensi.data

import com.sumberrejeki.absensi.data.model.AbsensiResponse
import com.sumberrejeki.absensi.data.model.ListAbsensiResponse
import com.sumberrejeki.absensi.data.model.PegawaiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<PegawaiResponse>

    @Multipart
    @POST("absensi")
    fun absen(
        @Part("nip") nip: RequestBody,
        @Part fotoMasuk: MultipartBody.Part,
        @Part("latitude_masuk") latitudeMasuk: RequestBody,
        @Part("longitude_masuk") longitudeMasuk: RequestBody
    ): Call<AbsensiResponse>

    @Multipart
    @POST("absenkeluar")
    fun absenKeluar(
        @Part("nip") nip: RequestBody,
        @Part fotoKeluar: MultipartBody.Part,
        @Part("latitude_keluar") latitudeKeluar: RequestBody,
        @Part("longitude_keluar") longitudeKeluar: RequestBody
    ): Call<AbsensiResponse>

    @GET("absensi/{nip}")
    fun getAbsensi(
        @Path("nip") nip: String
    ): Call<ListAbsensiResponse>
}