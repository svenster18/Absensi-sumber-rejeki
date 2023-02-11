package com.sumberrejeki.absensi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sumberrejeki.absensi.data.sharedpreferences.UserPreference
import com.sumberrejeki.absensi.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = UserPreference(this).getUser()
        binding.tvNamaPenggunaProfil2.text = user.nama
        binding.tvJabatan.text = user.jabatan
        val nama = user.nama.split("\\s".toRegex()).toTypedArray()
        binding.tvNamaAwal.text = nama[0]
        binding.tvNamaAkhir.text = nama[1]
        binding.tvEmail.text = user.email
        binding.tvTelepon.text = user.noTelp
    }
}