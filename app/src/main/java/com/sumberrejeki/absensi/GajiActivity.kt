package com.sumberrejeki.absensi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.sumberrejeki.absensi.data.ApiConfig
import com.sumberrejeki.absensi.data.model.ListAbsensiResponse
import com.sumberrejeki.absensi.data.sharedpreferences.UserPreference
import com.sumberrejeki.absensi.databinding.ActivityGajiBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class GajiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGajiBinding
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGajiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userPreference = UserPreference(this)

        binding.tvNamaPengguna.text = userPreference.getUser().nama
        binding.tvNip.text = userPreference.getUser().nIP
        val dateFormat = SimpleDateFormat("d MMMM yyyy")
        val date = Date()
        val tanggal = Date(date.year, date.month, 1)
        binding.tvTanggal.text = dateFormat.format(tanggal)

        handler = Handler(Looper.getMainLooper())
        findAbsensi()
    }

    private fun findAbsensi() {
        showLoading(true)
        val userPreference = UserPreference(this)
        val client = ApiConfig.getApiService().getAbsensi(userPreference.getUser().nIP)
        client.enqueue(object : Callback<ListAbsensiResponse> {
            override fun onResponse(
                call: Call<ListAbsensiResponse>,
                response: Response<ListAbsensiResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        handler.post {
                            val gajiPokok = userPreference.getUser().gajiPokok.toInt()
                            binding.tvGajiPokok.text = gajiPokok.toString()
                            binding.tvTunjanganMakan.text = "${gajiPokok * 0.2}"
                            binding.tvGaji.text = "${gajiPokok + binding.tvTunjanganMakan.text.toString().toInt() + binding.tvLembur.text.toString().toInt()}"
                            binding.tvTotalGaji.text = "${gajiPokok + binding.tvTunjanganMakan.text.toString().toInt() + binding.tvLembur.text.toString().toInt()}"

                        }
                    }
                } else {
                    Log.e("GajiActivity", "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ListAbsensiResponse>, t: Throwable) {
                showLoading(false)
                Log.e("GajiActivity", "onFailure: ${t.message}")
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}