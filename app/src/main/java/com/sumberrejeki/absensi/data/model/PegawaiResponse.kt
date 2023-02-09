package com.sumberrejeki.absensi.data.model

import com.google.gson.annotations.SerializedName

data class PegawaiResponse(

	@field:SerializedName("Status")
	val status: String,

	@field:SerializedName("Message")
	val message: String,

	@field:SerializedName("Error")
	val error: String,

	@field:SerializedName("Data")
	val data: Data
)

data class Data(

	@field:SerializedName("Nama")
	val nama: String,

	@field:SerializedName("Email")
	val email: String,

	@field:SerializedName("NIP")
	val nIP: String,

	@field:SerializedName("Jabatan")
	val jabatan: String,

	@field:SerializedName("No Telp")
	val noTelp: String
)
