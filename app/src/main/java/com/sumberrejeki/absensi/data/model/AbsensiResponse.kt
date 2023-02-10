package com.sumberrejeki.absensi.data.model

import com.google.gson.annotations.SerializedName

data class AbsensiResponse(

	@field:SerializedName("messages")
	val messages: Messages,

	@field:SerializedName("error")
	val error: Int?,

	@field:SerializedName("status")
	val status: Int
)

data class Messages(

	@field:SerializedName("success")
	val success: String
)
