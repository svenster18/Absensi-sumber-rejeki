package com.sumberrejeki.absensi.data.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

@Parcelize
data class PegawaiResponse(

	@field:SerializedName("Status")
	val status: String,

	@field:SerializedName("Message")
	val message: String,

	@field:SerializedName("Error")
	val error: String,

	@field:SerializedName("Data")
	val data: Pegawai
) : Parcelable

@Parcelize
data class Pegawai(

	@field:SerializedName("Nama")
	var nama: String = "",

	@field:SerializedName("Email")
	var email: String = "",

	@field:SerializedName("NIP")
    var nIP: String = "",

	@field:SerializedName("Gaji Pokok")
	var gajiPokok: String = "0",

	@field:SerializedName("Jabatan")
	var jabatan: String = "",

	@field:SerializedName("No Telp")
	var noTelp: String = ""
) : Parcelable
