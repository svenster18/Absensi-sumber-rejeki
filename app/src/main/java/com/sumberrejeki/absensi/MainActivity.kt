package com.sumberrejeki.absensi

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.sumberrejeki.absensi.databinding.ActivityMainBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var active = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nama = intent.getStringExtra(EXTRA_NAMA)

        val timeFormat: DateFormat = SimpleDateFormat("HH:mm")
        val dateFormat: DateFormat = SimpleDateFormat("dd MMM yyyy")

        binding.tvPengguna.text = nama

        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        active = true
        executor.execute {
            while (active) {
                handler.post {
                    binding.tvTanggalBulanTahun.text = getString(R.string.time, timeFormat.format(Date()), dateFormat.format(Date()))
                }
                Thread.sleep(60000)
            }
        }

        binding.cvAbsen.setOnClickListener {
            val intent = Intent(this@MainActivity, AbsenActivity::class.java)
            startActivity(intent)
        }

        binding.cvListKehadiran.setOnClickListener {
            val intent = Intent(this@MainActivity, ListKehadiranActivity::class.java)
            startActivity(intent)
        }

        binding.cvSlipGaji.setOnClickListener {
            val intent = Intent(this@MainActivity, GajiActivity::class.java)
            startActivity(intent)
        }

        binding.cvAkun.setOnClickListener {
            val intent = Intent(this@MainActivity, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        active = false
    }

    override fun onResume() {
        super.onResume()
        active = true
    }

    override fun onDestroy() {
        super.onDestroy()
        active = false
    }

    companion object {
        const val EXTRA_NAMA = "extra_nama"
    }
}