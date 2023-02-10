package com.sumberrejeki.absensi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import com.sumberrejeki.absensi.data.ApiConfig
import com.sumberrejeki.absensi.data.model.PegawaiResponse
import com.sumberrejeki.absensi.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "LoginActivity"
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMasuk.setOnClickListener { view ->
            login(binding.edEmailMasuk.text.toString(), binding.edKataSandiMasuk.text.toString());
        }

        handler = Handler(Looper.getMainLooper())
    }

    private fun login(username: String, password: String) {
        showLoading(true)
        val client = ApiConfig.getApiService().login(username, password)
        client.enqueue(object : Callback<PegawaiResponse> {
            override fun onResponse(
                call: Call<PegawaiResponse>,
                response: Response<PegawaiResponse>
            ) {
                showLoading(false)
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.putExtra(MainActivity.EXTRA_NAMA, responseBody.data.nama)
                    startActivity(intent)
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    handler.post {
                        Toast.makeText(this@LoginActivity, "Username/Password salah", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<PegawaiResponse>, t: Throwable) {
                showLoading(false)
                handler.post {
                    Toast.makeText(this@LoginActivity, "Username/Password salah", Toast.LENGTH_SHORT).show()
                }
                Log.e(TAG, "onFailure: ${t.message}")
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