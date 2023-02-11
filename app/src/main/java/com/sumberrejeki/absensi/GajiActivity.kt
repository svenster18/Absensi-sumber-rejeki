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
import java.text.NumberFormat
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
        val mCurrencyFormat = NumberFormat.getCurrencyInstance()
        binding.tvTanggal.text = dateFormat.format(tanggal)
        val gajiPokok = userPreference.getUser().gajiPokok.toInt()
        binding.tvGajiPokok.text = mCurrencyFormat.format(gajiPokok)
        val tunjanganMakan = (gajiPokok * 0.2).toInt()
        binding.tvTunjanganMakan.text = mCurrencyFormat.format(tunjanganMakan)
        val lembur = 0
        val totalGaji = gajiPokok + tunjanganMakan + lembur
        binding.tvLembur.text = mCurrencyFormat.format(lembur)
        binding.tvGaji.text = mCurrencyFormat.format(totalGaji)
        binding.tvTotalGaji.text = mCurrencyFormat.format(totalGaji)
    }
}