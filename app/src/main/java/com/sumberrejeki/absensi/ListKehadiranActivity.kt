package com.sumberrejeki.absensi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.sumberrejeki.absensi.data.ApiConfig
import com.sumberrejeki.absensi.data.model.ListAbsensiResponse
import com.sumberrejeki.absensi.databinding.ActivityListKehadiranBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListKehadiranActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListKehadiranBinding

    companion object {
        private const val TAG = "ListKehadiranActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListKehadiranBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.setHasFixedSize(true)

        findAbsensi()
    }

    private fun findAbsensi() {
        showLoading(true)
        val client = ApiConfig.getApiService().getAbsensi("320428180600000201")
        client.enqueue(object : Callback<ListAbsensiResponse> {
            override fun onResponse(
                call: Call<ListAbsensiResponse>,
                response: Response<ListAbsensiResponse>
            ) {
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val adapter = AbsensiAdapter(responseBody.data)
                        binding.recyclerView.adapter = adapter
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: Call<ListAbsensiResponse>, t: Throwable) {
                showLoading(false)
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