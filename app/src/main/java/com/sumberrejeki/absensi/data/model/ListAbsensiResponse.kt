package com.sumberrejeki.absensi.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ListAbsensiResponse(

	@field:SerializedName("data")
	val data: List<Absensi>,

	@field:SerializedName("error")
	val error: String,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

@Parcelize
data class Absensi(

	@field:SerializedName("jam_keluar")
	val jamKeluar: String?,

	@field:SerializedName("nip")
	val nip: String,

	@field:SerializedName("jam_masuk")
	val jamMasuk: String,

	@field:SerializedName("latitude_keluar")
	val latitudeKeluar: String?,

	@field:SerializedName("latitude_masuk")
	val latitudeMasuk: String,

	@field:SerializedName("longitude_masuk")
	val longitudeMasuk: String,

	@field:SerializedName("id_absensi")
	val idAbsensi: String,

	@field:SerializedName("tanggal")
	val tanggal: String,

	@field:SerializedName("foto_keluar")
	val fotoKeluar: String?,

	@field:SerializedName("longitude_keluar")
	val longitudeKeluar: String?,

	@field:SerializedName("foto_masuk")
	val fotoMasuk: String
) : Parcelable
